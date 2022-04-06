package com.app.weatherforeastapp.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.app.weatherforeastapp.model.WeatherResponse
import com.app.weatherforeastapp.repo.WeatherRepository
import java.text.SimpleDateFormat
import java.util.*


class HomeViewModel (application: Application) : AndroidViewModel(application) {
    var weatherRepository:WeatherRepository

    init{
        weatherRepository = WeatherRepository(application)
    }

    fun getApiObjFromRoom(timeZone:String): WeatherResponse {
        return weatherRepository.getApiObjFromLocalDataBase(timeZone)
    }


    fun loadWeatherObj(context: Context, lat:Double, lon:Double, lang:String, unit:String) : LiveData<WeatherResponse> {
        weatherRepository.fetchWeatherObject(context,lat,lon,lang,unit)
        return weatherRepository.weatherObj
    }

    fun updateAllData(context: Context, lang: String, unit: String){
        weatherRepository.UpdateWeatherData(lang,unit,context)

    }

    fun dateFormat(milliSeconds: Int):String{
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds.toLong() * 1000)
        var month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        var day=calendar.get(Calendar.DAY_OF_MONTH).toString()
        var year=calendar.get(Calendar.YEAR).toString()
        return day+"/"+month// +"/"+year

    }

    fun timeFormat(millisSeconds:Int ): String {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis((millisSeconds * 1000).toLong())
        val format = SimpleDateFormat("hh:00 aaa")
        return format.format(calendar.time)
    }


}
