package com.example.investmenttracker.presentation.view_model_factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.coin.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.currency.GetAllCurrenciesUseCase
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.presentation.view_model.AnalyticsViewModel

class AnalyticsViewModelFactory(
    private val app: Application,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getAllCoinsUseCase: GetAllCoinsUseCase,
    private val getAllCurrenciesUseCase: GetAllCurrenciesUseCase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AnalyticsViewModel(
            app,
            getUserDataUseCase,
            getAllCoinsUseCase,
            getAllCurrenciesUseCase
        ) as T
    }
}