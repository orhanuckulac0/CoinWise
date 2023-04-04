package com.example.investmenttracker.domain.use_case.util

import androidx.appcompat.app.AppCompatDelegate

fun changeAppTheme(isDarkTheme: Boolean) {
    if (isDarkTheme) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
