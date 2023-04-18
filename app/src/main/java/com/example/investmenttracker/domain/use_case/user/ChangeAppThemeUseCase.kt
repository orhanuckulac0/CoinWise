package com.example.investmenttracker.domain.use_case.user

import androidx.appcompat.app.AppCompatDelegate

class ChangeAppThemeUseCase {

    fun execute(isDark: Boolean){
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}
