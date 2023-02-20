package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.GetCoinBySlugUseCase
import com.example.investmenttracker.domain.use_case.SaveCoinUseCase

class SearchCoinViewModelFactory(
    private val app: Application,
    private val getCoinBySlugUseCase: GetCoinBySlugUseCase,
    private val saveCoinUseCase: SaveCoinUseCase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchCoinViewModel(
            app,
            getCoinBySlugUseCase,
            saveCoinUseCase
        ) as T
    }
}