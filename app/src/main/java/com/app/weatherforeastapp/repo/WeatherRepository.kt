package com.app.weatherforeastapp.repo

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.weatherforeastapp.local.data.LocalDataBase
import com.app.weatherforeastapp.model.WeatherResponse
import com.app.weatherforeastapp.remot.WeatherServes
import com.example.weatherapp.data.local.entities.AlertsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherRepository {

    var weatherObj = MutableLiveData<WeatherResponse>()
    var localDataBase: LocalDataBase
    var weatherService: WeatherServes

    constructor(application: Application) {
        localDataBase = LocalDataBase(application)
        weatherService = WeatherServes
    }

    public fun getApiObjFromLocalDataBase(timeZone:String): WeatherResponse{
        return localDataBase.getApiObj(timeZone)
    }


    fun fetchWeatherData(context: Context, lat: Double, lon: Double, lang: String, unit: String): LiveData<List<WeatherResponse>> {
        if (isOnline(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = weatherService.API_SERVICES_SERVICE.getCurrentWeatherByLatLng(lat, lon, lang, unit)
                try {
                    if (response.isSuccessful) {
                        //لو ال response successfullyهاتلي الداتا ال في ال responseوحطها في ال localdatabase
                        response.body()?.let { localDataBase.insert(it) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return localDataBase.getAll()
    }


    fun fetchWeatherObject(context: Context, lat: Double, lon: Double, lang: String, unit: String) {

        if (isOnline(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = weatherService.API_SERVICES_SERVICE.getCurrentWeatherByLatLng(lat, lon, lang, unit)
                try {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            localDataBase.insert(it)
                            weatherObj.postValue(it)
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

/////////// function for setting

    fun UpdateWeatherData(lang: String, unit: String, context: Context) {
        if (isOnline(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                var weatherData = localDataBase.getAllList()
                for (item in weatherData) {
                    Log.i("repo",lang+" "+unit)
                    val response =
                        weatherService.API_SERVICES_SERVICE.getCurrentWeatherByLatLng(item.lat, item.lon, lang, unit)
                    try {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                localDataBase.insert(it)
                                Log.i("update ", "up")
                            }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    suspend fun deleteApiObject(timeZone: String) {
        localDataBase.deleteApiObject(timeZone)
    }
    fun getApiObject(timeZone:String) : WeatherResponse {
        return localDataBase.getApiObj(timeZone)
    }
    fun getWeatherDataFromLocalDataBase(): LiveData<List<WeatherResponse>> {
        return localDataBase.getAll()
    }





    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }


    suspend fun deleteAlarmObj(id: Int) {
        localDataBase.deleteAlarmObj(id)
    }

    fun getAllAlarmObj(): LiveData<List<AlertsEntity>> {
        return localDataBase.getAllAlarmObj()
    }

    suspend fun insertAlarm(alertsEntity: AlertsEntity):Long {
        return localDataBase.insertAlarm(alertsEntity)
    }

}