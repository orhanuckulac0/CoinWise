package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.domain.use_case.user.ChangeAppThemeUseCase
import com.example.investmenttracker.domain.use_case.user.UpdateUserDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val app: Application,
    private val changeAppThemeUseCase: ChangeAppThemeUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase
): AndroidViewModel(app) {

    fun changeTheme(isDark: Boolean){
        changeAppThemeUseCase.execute(isDark)
    }
    
    fun updateUser(data: UserData){
        viewModelScope.launch(Dispatchers.IO) {
            updateUserDataUseCase.execute(data)
        }
    }
}