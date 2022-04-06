package com.app.weatherforeastapp.local.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.app.weatherforeastapp.model.WeatherResponse
import com.example.weatherapp.data.local.entities.AlertsEntity


class LocalDataBase {

    lateinit var apiObjectDao: WeatherResposatoryDao
    lateinit var alertsDao:AlertsDao
    constructor (application: Application) {
        apiObjectDao = WeatherDataBase.getDatabase(application).apiObjectDao()
        //alart Dao Object
        alertsDao=WeatherDataBase.getDatabase(application).alertObjDao()


    }

    fun getAll(): LiveData<List<WeatherResponse>> {
        return apiObjectDao.getAllWeatherResponse()
    }

    fun getAllList(): List<WeatherResponse> {
        return apiObjectDao.getAllList()
    }

    suspend fun insert(apiObj: WeatherResponse) {
        apiObjectDao.insert(apiObj)
    }



    suspend fun deleteApiObject(timeZone: String) {
        apiObjectDao.deleteApiObject(timeZone)
    }

    fun getApiObj(timeZone:String) : WeatherResponse {
        return apiObjectDao.getApiObject(timeZone)
    }

    suspend fun deleteAlarmObj(id: Int) {
        alertsDao.deleteAlarmObjById(id)
    }

    fun getAllAlarmObj(): LiveData<List<AlertsEntity>> {
        return alertsDao.getAllAlarms()
    }

    suspend fun insertAlarm(alertsEntity: AlertsEntity):Long {
        return alertsDao.insertAlarm(alertsEntity)
    }



}