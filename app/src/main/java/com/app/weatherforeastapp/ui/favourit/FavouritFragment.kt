package com.app.weatherforeastapp.ui.favourit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.weatherapplication.util.ContextUtils
import com.app.weatherapplication.util.ContextUtils.Companion.setLocale
import com.app.weatherapplication.util.ContextUtils.Companion.settings
import com.app.weatherforeastapp.*
import com.app.weatherforeastapp.databinding.FragmentFavoruritBinding
import com.app.weatherforeastapp.model.WeatherResponse
import com.example.weatherapp.util.Setting
import com.example.weatherapp.util.Setting.lat
import com.example.weatherapp.util.Setting.lon
import com.google.android.gms.maps.MapFragment
import com.google.gson.Gson

class FavouritFragment : Fragment() {
    private lateinit var favouritesViewModel: FavouritViewModel
    private lateinit var binding: FragmentFavoruritBinding
    private lateinit var favoriteAdapter:FavouritAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(requireActivity(),Setting.lang)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        favouritesViewModel =
            ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).
            get(FavouritViewModel::class.java)
        //val root = inflater.inflate(R.layout.fragment_favourites, container, false)
        binding= FragmentFavoruritBinding.inflate(inflater,container,false)
        favoriteAdapter= FavouritAdapter(arrayListOf(),favouritesViewModel,requireActivity().applicationContext)
        settings(requireContext())
        //val bundle=Bundle()
        if (arguments?.getInt("id")==1){
            val lat=arguments?.getDouble("lat",0.0)
            val lon=arguments?.getDouble("lon",0.0)
            setLocale(requireActivity(),Setting.lang)
            observeViewModel(favouritesViewModel,lat!!,lon!!,Setting.lang,Setting.units)
            //  Toast.makeText(requireActivity(),"${lat.toString()},${lon.toString()}",Toast.LENGTH_LONG).show()
        }else{
            setLocale(requireActivity(),Setting.lang)
            getWeatherDataFromRoom(favouritesViewModel)
        }
        binding.btnFab.setOnClickListener {
            val bundle= Bundle()
            bundle.putInt("mapId",1)
            val mapsFragment= MapsFragment()
            mapsFragment.arguments=bundle
            // fragmentManager.
            Toast.makeText(requireActivity(), "${arguments?.getInt("mapId")} ", Toast.LENGTH_LONG).show()
            Navigation.findNavController(it).navigate(R.id.action_nav_favourit_to_nav_map,bundle)
        }
        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.rvFavourites.apply {
            adapter=favoriteAdapter
        }
        val itemTouchHelperCallback=object :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                favoriteAdapter.removeFavouritFromAdapter(viewHolder)
            }

        }

        /////////////////
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvFavourites)
        favoriteAdapter.onItemClickListener=object :FavouritAdapter.OnItemClickListener{
            override fun onClick(pos: Int, weatherResponse: WeatherResponse) {
                val weatherString=Gson().toJson(weatherResponse)
                val bundle:Bundle=Bundle()
                bundle.putString("weatherString",weatherString)
                val favouriteDetailsFragment= FavouritDetails()
                favouriteDetailsFragment.arguments=bundle
                Log.i("ww",weatherResponse.toString())
                findNavController().navigate(R.id.action_nav_favourit_to_nav_fav_details,bundle)
            }

        }

    }

    private fun observeViewModel(favouritesViewModel: FavouritViewModel, lat: Double, lon: Double, lang:String, unit:String) {
        favouritesViewModel.loadWeather(requireActivity().applicationContext, lat, lon, lang, unit).observe(viewLifecycleOwner,{
            favoriteAdapter.onUpdateData(it)
        })

    }

    private fun getWeatherDataFromRoom(favouritesViewModel: FavouritViewModel) {
        favouritesViewModel.getWeatherDataFromRoom().observe(viewLifecycleOwner,{
            favoriteAdapter.onUpdateData(it)
        })

    }

}