package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.coin.DeleteCoinUseCase
import com.example.investmenttracker.domain.use_case.coin.UpdateInvestmentUseCase
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.UpdateUserDataUseCase

class TokenDetailsViewModelFactory(
    private val app: Application,
    private val updateInvestmentUseCase: UpdateInvestmentUseCase,
    private val deleteCoinUseCase: DeleteCoinUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TokenDetailsViewModel(
            app,
            updateInvestmentUseCase,
            deleteCoinUseCase,
            getUserDataUseCase,
            updateUserDataUseCase
        ) as T
    }
}