package com.example.investmenttracker.presentation.view_model_factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.domain.use_case.coin.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.GetMultipleCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.UpdateCoinDetailsUseCase
import com.example.investmenttracker.domain.use_case.currency.AddCurrencyDataToDBUseCase
import com.example.investmenttracker.domain.use_case.currency.GetCurrencyValueFromDBUseCase
import com.example.investmenttracker.domain.use_case.currency.GetNewCurrencyValueUseCase
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
    private val getNewCurrencyValueUseCase: GetNewCurrencyValueUseCase,
    private val addCurrencyDataToDBUseCase: AddCurrencyDataToDBUseCase,
    private val getCurrencyValueFromDBUseCase: GetCurrencyValueFromDBUseCase
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
            getNewCurrencyValueUseCase,
            addCurrencyDataToDBUseCase,
            getCurrencyValueFromDBUseCase
        ) as T
    }
}