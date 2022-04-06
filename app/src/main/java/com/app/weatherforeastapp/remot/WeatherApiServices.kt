package com.app.weatherforeastapp.remot

import com.app.weatherforeastapp.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "64f3f1d2e99728a2edf3aba4c2d5479f"
interface WeatherApiServices {
    //مخزن الداتا ال هترجعلي وهشتغل عليها
    @GET("data/2.5/onecall")
    suspend fun getCurrentWeatherByLatLng(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("exclude") exclude: String = "minutely",
        @Query("appid") appid: String = API_KEY
    ): Response<WeatherResponse>

}