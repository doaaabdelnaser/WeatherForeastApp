package com.app.weatherforeastapp.ui.setting

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.app.weatherapplication.util.ContextUtils
import com.app.weatherforeastapp.MainActivity
import com.app.weatherforeastapp.R
import com.app.weatherforeastapp.databinding.FragmentSettingBinding
import com.example.weatherapp.util.Setting

class SettingFragment : PreferenceFragmentCompat() {

    private  lateinit var binding: FragmentSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.setBackgroundColor(Color.WHITE)
        return view

    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
        preferenceManager.findPreference<Preference>("LANGUAGE_SYSTEM")!!
            .setOnPreferenceChangeListener(Preference.OnPreferenceChangeListener { preference, newValue ->
                startActivity(Intent(requireContext(), MainActivity::class.java))
                return@OnPreferenceChangeListener true
            })

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val mapPreference: Preference? = findPreference("CUSTOM_LOCATION")
        mapPreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (sharedPreferences.getBoolean("CUSTOM_LOCATION", true)) {
                view?.findNavController()
                    ?.navigate(R.id.action_nav_setting_to_nav_map)
            }
            true
        }


        val LP = findPreference("LANGUAGE_SYSTEM") as ListPreference?
        val lan = Setting.lang
        if ("en".equals(lan)) {
            ContextUtils.setLocale(requireActivity(), "en")
            LP?.setSummary(LP?.getEntry())

        } else {
            ContextUtils.setLocale(requireActivity(), "ar")
            LP?.setSummary(LP.getEntry())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
    fun editSettings() {
        val LP = findPreference("LANGUAGE_SYSTEM") as ListPreference?
        val lang = Setting.lang
        if ("en".equals(lang)) {
            ContextUtils.setLocale(requireActivity(), "en")
            LP?.setSummary(LP?.getEntry())

        } else {
            ContextUtils.setLocale(requireActivity(), "ar")
            LP?.setSummary(LP.getEntry())
        }

        LP!!.setOnPreferenceChangeListener(androidx.preference.Preference.OnPreferenceChangeListener { prefs, obj ->
            val items = obj as String
            if (prefs.key == "LANGUAGE_SYSTEM") {
                when (items) {
                    "ar" -> {
                        ContextUtils.setLocale(requireActivity(), "ar")
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                    }
                    "en" -> {
                        ContextUtils.setLocale(requireActivity(), "en")
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                    }
                }
                val UU = prefs as ListPreference
                UU.summary = UU.entries[UU.findIndexOfValue(items)]
            }
            true
        })
    }
}
