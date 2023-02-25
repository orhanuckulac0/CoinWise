package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.*

class TokenDetailsViewModelFactory(
    private val app: Application,
    private val getSingleCoinByIdUseCase: GetSingleCoinByIdUseCase,
    private val deleteCoinUseCase: DeleteCoinUseCase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TokenDetailsViewModel(
            app,
            getSingleCoinByIdUseCase,
            deleteCoinUseCase
        ) as T
    }
}