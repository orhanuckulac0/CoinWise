package com.example.investmenttracker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.investmenttracker.domain.use_case.util.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoinWiseApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val sharedPref = applicationContext.getSharedPreferences(Constants.THEME_PREF, MODE_PRIVATE)
        val theme = sharedPref!!.getBoolean(Constants.SWITCH_STATE_KEY, true)
        val currentTheme = AppCompatDelegate.getDefaultNightMode()
        val newTheme = if (theme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        if (currentTheme != newTheme) {
            AppCompatDelegate.setDefaultNightMode(newTheme)
        }
    }
}