package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.GetAllCoinsUseCase

class CoinViewModelFactory(
    private val app: Application,
    private val getAllCoinsUseCase: GetAllCoinsUseCase
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CoinViewModel(
            app,
            getAllCoinsUseCase
        ) as T
    }
}