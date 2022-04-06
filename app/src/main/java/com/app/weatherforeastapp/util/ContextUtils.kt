package com.app.weatherapplication.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.*
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.preference.PreferenceManager
import com.example.weatherapp.util.Setting

class ContextUtils(base:Context) : ContextWrapper(base) {

    companion object{
       // to set defualt language and setting of device
        fun setLocale(activity: Activity,languageCode:String){
            val locale=Locale(languageCode)
            Locale.setDefault(locale)
            val resources:Resources=activity.resources
            val configuration:Configuration=resources.configuration
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration,resources.displayMetrics)


        }

        ///////////////////////////////

        fun settings(context: Context){
            //to get last location

            val sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context)
            val unitSystem = sharedPreferences.getString("UNIT_SYSTEM", "")
            val languageSystem = sharedPreferences.getString("LANGUAGE_SYSTEM", "")
            val location1 = sharedPreferences.getBoolean("USE_DEVICE_LOCATION", false)
            val mapLocation = sharedPreferences.getBoolean("CUSTOM_LOCATION", false)

            if (unitSystem != null){
                Setting.units=unitSystem
            }
            if (languageSystem != null){
                Setting.lang=languageSystem
            }
            if (location1 != null){
                Setting.deviceLocation=location1
            }
            if (mapLocation != null){
                Setting.mapLocation=mapLocation
            }
        }
    }
      /////////////////////////
        fun updateLocalization(c: Context, localeToSwitchTo: Locale): ContextWrapper{
            var context=c
            val resources:Resources=context.resources
            val config:Configuration=resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val localeList = LocaleList(localeToSwitchTo)
                LocaleList.setDefault(localeList)
                config.setLocales(localeList)
            } else {
                config.locale = localeToSwitchTo
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                context = context.createConfigurationContext(config)
            } else {
                resources.updateConfiguration(config, resources.displayMetrics)
            }
            return ContextUtils(context)
        }

        /////////////////////

}