package com.example.investmenttracker.presentation.view_model_factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.coin.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.GetMultipleCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.UpdateCoinDetailsUseCase
import com.example.investmenttracker.domain.use_case.user.GetNewCurrencyValueUseCase
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.InsertUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.UpdateUserDataUseCase
import com.example.investmenttracker.presentation.view_model.MainViewModel

class MainViewModelFactory(
    private val app: Application,
    private val getAllCoinsUseCase: GetAllCoinsUseCase,
    private val insertUserDataUseCase: InsertUserDataUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase,
    private val getMultipleCoinsUseCase: GetMultipleCoinsUseCase,
    private val updateCoinDetailsUseCase: UpdateCoinDetailsUseCase,
    private val getNewCurrencyValueUseCase: GetNewCurrencyValueUseCase
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(
            app,
            getAllCoinsUseCase,
            insertUserDataUseCase,
            getUserDataUseCase,
            updateUserDataUseCase,
            getMultipleCoinsUseCase,
            updateCoinDetailsUseCase,
            getNewCurrencyValueUseCase
        ) as T
    }
}