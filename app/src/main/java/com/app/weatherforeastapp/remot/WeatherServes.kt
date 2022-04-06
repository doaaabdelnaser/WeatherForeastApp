package com.app.weatherforeastapp.remot

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
// شبه ال singleton
object WeatherServes {
//client ال هيشتغل علي اللحاجات ال عندى

    private const val BASE_URL = "https://api.openweathermap.org/"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val API_SERVICES_SERVICE: WeatherApiServices = getRetrofit().create(WeatherApiServices::class.java)
}