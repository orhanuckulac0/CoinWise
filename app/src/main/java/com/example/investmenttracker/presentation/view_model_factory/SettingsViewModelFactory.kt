package com.example.investmenttracker.presentation.view_model_factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.user.ChangeAppThemeUseCase
import com.example.investmenttracker.domain.use_case.user.UpdateUserDataUseCase
import com.example.investmenttracker.presentation.view_model.SettingsViewModel

class SettingsViewModelFactory(
    private val app: Application,
    private val changeAppThemeUseCase: ChangeAppThemeUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase
    ): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(
            app,
            changeAppThemeUseCase,
            updateUserDataUseCase
        ) as T
    }

}