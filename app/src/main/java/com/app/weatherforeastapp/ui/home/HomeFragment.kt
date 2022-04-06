package com.app.weatherforeastapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.app.weatherapplication.util.ContextUtils
import com.app.weatherforeastapp.DailyAdapter
import com.app.weatherforeastapp.HourlyAdapter
import com.app.weatherforeastapp.databinding.FragmentHomeBinding
import com.app.weatherforeastapp.model.Alerts
import com.app.weatherforeastapp.model.WeatherResponse
import com.bumptech.glide.Glide
import com.example.weatherapp.util.AlarmReceiver
import com.example.weatherapp.util.MyWorkerApi
import com.example.weatherapp.util.NotificationUtils
import com.example.weatherapp.util.Setting
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding:FragmentHomeBinding
    lateinit var notificationUtils: NotificationUtils
    lateinit var alrmreciever: AlarmReceiver
    val PERMISSION_ID = 55
    var yourLocationLat:Double=0.0
    var yourLocationLon:Double=0.0

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var prefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var timezone: String=""

//get last location and access location permission using fausedlocation provider

    var handler = Handler(Handler.Callback {
        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
        editor.putString("lat", yourLocationLat.toString())
        editor.putString("lon", yourLocationLon.toString())
        editor.commit()
        ///////////////
        Setting.lon=prefs.getString("lon", "").toString()
        Setting.lat= prefs.getString("lat", "").toString()
        homeViewModel.loadWeatherObj(requireActivity().applicationContext,
            Setting.lat.toDouble(),
            Setting.lon.toDouble(),
            Setting.lang,
            Setting.units).observe(this,{
            updateUI(it)
            alrmreciever=AlarmReceiver()
        })
        true
    })


    lateinit var hourlyAdapter:HourlyAdapter
    lateinit var dailyAdapter:DailyAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding= FragmentHomeBinding.inflate(inflater,container,false)
        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(requireActivity())
        homeViewModel= ViewModelProvider(this).get(HomeViewModel::class.java)

        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
        editor= prefs.edit()
        helpList()
        //  workerInit()

        Setting.lat= prefs.getString("lat","").toString()
        Setting.lon=prefs.getString("lon","").toString()

        hourlyAdapter= HourlyAdapter(arrayListOf())
        dailyAdapter= DailyAdapter(arrayListOf(),requireActivity().applicationContext )

        initRecyclers()
        ContextUtils.settings(requireActivity().applicationContext)
        if (prefs.getBoolean("USE_DEVICE_LOCATION",true)){
            getLastLocation()

        }else{
            Setting.lat= prefs.getString("lat","").toString()
            Setting.lon=prefs.getString("lon","").toString()
            ContextUtils.setLocale(requireActivity(), Setting.lang)
            homeViewModel.loadWeatherObj(requireActivity().applicationContext,
                Setting.lat.toDouble(),
                Setting.lon.toDouble(),
                Setting.lang,
                Setting.units).observe(requireActivity(),{
                updateUI(it)
            })
        }
        timezone=prefs.getString("timezone", ("")).toString()
        if(!timezone.isNullOrEmpty()) {
            getObjByTimezone()
        }


        return binding.root
    }

    private fun initRecyclers() {
        binding.rv24Hour.apply {
            adapter=hourlyAdapter
        }
        binding.rv7Day.apply {
            adapter=dailyAdapter
        }
    }



    private fun updateUI(item: WeatherResponse) {
        item?.let {
            item.apply {
                binding.txtCity.text=timezone

                binding.txtDate.text="${homeViewModel.dateFormat(current.dt)} ${homeViewModel.timeFormat(current.dt)}"
                binding.txtVwTemp.text=current.temp.toInt().toString()+"°"
                CoroutineScope(Dispatchers.Main).launch{
                    Glide.with(binding.imgWeatherIcon).
                    load(hourlyAdapter.getImage(current.weather.get(0).icon)).
                    into(binding.imgWeatherIcon)
                }
                binding.txtVwTempFeels.text=" ${current.feels_like.toString()}"
                binding.txtVwDesc.text=current.weather[0].description
                binding.txtVwValueHumidity.text=current.humidity.toString()
                Log.d(ContentValues.TAG, "timezone: ${it.current?.humidity}")
                binding.txtVwValuePressure.text=current.pressure.toString()
                binding.txtVwValueSpeed.text=current.wind_speed.toString()
                binding.txtVwValueCloud.text=current.clouds.toString()
                dailyAdapter?.updateData(daily)
                hourlyAdapter.updateData(hourly)

            }
            item.alerts?.let {
                notifyUser(it)
            }

        }
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("timezone", item.timezone)
        editor.commit()
    }

    override fun onResume() {
        super.onResume()
        ContextUtils.settings(requireActivity().applicationContext)
        Setting.lon=prefs.getString("lon", ("")).toString()
        Setting.lat=prefs.getString("lat", ("")).toString()
        if (prefs.getBoolean("USE_DEVICE_LOCATION",true)){
            mFusedLocationClient= LocationServices.getFusedLocationProviderClient(requireActivity())
            getLastLocation()
        }else{
            Setting.lat= prefs.getString("lat","")!!
            Setting.lon=prefs.getString("lon","")!!
            ContextUtils.setLocale(requireActivity(), Setting.lang)
            homeViewModel.loadWeatherObj(requireActivity().applicationContext,
                Setting.lat.toDouble(),
                Setting.lon.toDouble(),
                Setting.lang,
                Setting.units)
        }

        homeViewModel.updateAllData(requireActivity().applicationContext, Setting.lang, Setting.units)
    }

    private fun notifyUser(alert:List<Alerts>){
        //setLocale(requireActivity(),Setting.lang)
        notificationUtils = NotificationUtils(requireActivity().applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nb: NotificationCompat.Builder? = notificationUtils.getAndroidChannelNotification(alert.get(0)?.event, ""
                    +homeViewModel.dateFormat(alert.get(0)?.start.toInt())+","+homeViewModel.dateFormat(alert.get(0)?.end.toInt()) +"\n"+alert.get(0)?.description,true)
            notificationUtils.getNManager()?.notify(3, nb?.build())

        }
    }
    private fun locationNotEnable() {

        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        ActivityCompat.startActivityForResult(requireActivity(), intent, PERMISSION_ID, Bundle())
    }


    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        yourLocationLat=location?.latitude
                        yourLocationLon=location?.longitude
                        Setting.lat=location?.latitude.toString()
                        Setting.lon=location?.longitude.toString()

                        handler.sendEmptyMessage(0)
                    }
                }
            } else {
                locationNotEnable()
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Looper.myLooper()?.let {
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                it
            )
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            yourLocationLat=mLastLocation?.latitude
            yourLocationLon=mLastLocation?.longitude
            Setting.lat=mLastLocation?.latitude.toString()
            Setting.lon=mLastLocation?.longitude.toString()
            handler.sendEmptyMessage(0)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getActivity()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
    private fun getObjByTimezone() {
        ContextUtils.setLocale(requireActivity(), Setting.lang)
        CoroutineScope(Dispatchers.IO).launch {
            var weather= homeViewModel.getApiObjFromRoom(timezone)
            withContext(Dispatchers.Main){
                updateUI(weather)
            }
        }
    }
    private fun helpList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = requireActivity().packageName
            val pm: PowerManager = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }
    private fun workerInit() {
        val saveRequest = PeriodicWorkRequest.Builder(MyWorkerApi::class.java,15, TimeUnit.MINUTES).addTag("up").build()
        WorkManager.getInstance(requireActivity().applicationContext).enqueueUniquePeriodicWork("up", ExistingPeriodicWorkPolicy.REPLACE,saveRequest)
    }
}