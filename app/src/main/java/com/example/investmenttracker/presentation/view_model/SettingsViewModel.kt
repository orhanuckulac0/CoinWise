package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.investmenttracker.domain.use_case.user.ChangeAppThemeUseCase

class SettingsViewModel(
    private val app: Application,
    private val changeAppThemeUseCase: ChangeAppThemeUseCase
): AndroidViewModel(app) {

    fun changeTheme(isDark: Boolean){
        changeAppThemeUseCase.execute(isDark)
    }
}