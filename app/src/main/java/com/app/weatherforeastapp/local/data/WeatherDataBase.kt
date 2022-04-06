package com.app.weatherforeastapp.local.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.weatherforeastapp.model.WeatherResponse
import com.example.weatherapp.data.local.entities.AlertsEntity

@Database(entities = [WeatherResponse::class,AlertsEntity::class], version = 2)
@TypeConverters(Converter::class)
abstract class WeatherDataBase : RoomDatabase(){
    abstract fun alertObjDao():AlertsDao


    abstract fun apiObjectDao(): WeatherResposatoryDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: WeatherDataBase? = null

        fun getDatabase(application: Application): WeatherDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    application.applicationContext,
                    WeatherDataBase::class.java,
                    "WeatherDB"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
