package com.app.weatherforeastapp.local.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.weatherforeastapp.model.WeatherResponse
@Dao
interface WeatherResposatoryDao {

    @Query("SELECT * FROM WeatherTable2")
    fun getAllWeatherResponse(): LiveData<List<WeatherResponse>>

    @Query("SELECT * FROM WeatherTable2")
    fun getAllList(): List<WeatherResponse>

    @Query("SELECT * FROM WeatherTable2 Where timezone = :timezone ")
    fun getApiObject(timezone:String): WeatherResponse

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(apiObj: WeatherResponse)

    @Query("DELETE FROM WeatherTable2 WHERE timezone = :timezone")
    suspend fun deleteApiObject(timezone:String)




}