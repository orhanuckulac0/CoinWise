package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.domain.use_case.coin.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnalyticsViewModel(
    private val app: Application,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getAllCoinsUseCase: GetAllCoinsUseCase
    ): AndroidViewModel(app) {


    var userData: UserData? = null
    var walletCoins = listOf<CoinModel>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getUserDataUseCase.execute(1).collect {
                userData = it
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            getAllCoinsUseCase.execute().collect(){
                walletCoins = it
            }
        }
    }
}