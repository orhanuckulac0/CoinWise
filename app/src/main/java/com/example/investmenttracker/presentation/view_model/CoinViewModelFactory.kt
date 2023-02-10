package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.GetCoinUseCase

class CoinViewModelFactory(
    private val app: Application,
    private val getCoinUseCase: GetCoinUseCase
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CoinViewModel(
            app,
            getCoinUseCase
        ) as T
    }
}