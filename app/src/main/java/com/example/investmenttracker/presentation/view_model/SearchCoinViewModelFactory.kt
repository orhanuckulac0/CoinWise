package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.coin.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.GetCoinBySlugUseCase
import com.example.investmenttracker.domain.use_case.coin.GetCoinBySymbolUseCase
import com.example.investmenttracker.domain.use_case.coin.SaveCoinUseCase

class SearchCoinViewModelFactory(
    private val app: Application,
    private val getCoinBySlugUseCase: GetCoinBySlugUseCase,
    private val getCoinBySymbolUseCase: GetCoinBySymbolUseCase,
    private val saveCoinUseCase: SaveCoinUseCase,
    private val getAllCoinsUseCase: GetAllCoinsUseCase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchCoinViewModel(
            app,
            getCoinBySlugUseCase,
            getCoinBySymbolUseCase,
            saveCoinUseCase,
            getAllCoinsUseCase
        ) as T
    }
}