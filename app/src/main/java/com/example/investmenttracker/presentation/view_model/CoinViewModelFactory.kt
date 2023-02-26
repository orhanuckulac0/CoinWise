package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.coin.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.InsertUserDataUseCase

class CoinViewModelFactory(
    private val app: Application,
    private val getAllCoinsUseCase: GetAllCoinsUseCase,
    private val insertUserDataUseCase: InsertUserDataUseCase,
    private val getUserDataUseCase: GetUserDataUseCase
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CoinViewModel(
            app,
            getAllCoinsUseCase,
            insertUserDataUseCase,
            getUserDataUseCase
        ) as T
    }
}