package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.coin.DeleteCoinUseCase
import com.example.investmenttracker.domain.use_case.coin.UpdateInvestmentUseCase

class TokenDetailsViewModelFactory(
    private val app: Application,
    private val updateInvestmentUseCase: UpdateInvestmentUseCase,
    private val deleteCoinUseCase: DeleteCoinUseCase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TokenDetailsViewModel(
            app,
            updateInvestmentUseCase,
            deleteCoinUseCase
        ) as T
    }
}