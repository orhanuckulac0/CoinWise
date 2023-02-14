package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.GetCoinBySlugUseCase

class SearchCoinViewModelFactory(
    private val app: Application,
    private val getCoinBySlugUseCase: GetCoinBySlugUseCase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchCoinViewModel(
            app,
            getCoinBySlugUseCase
        ) as T
    }
}