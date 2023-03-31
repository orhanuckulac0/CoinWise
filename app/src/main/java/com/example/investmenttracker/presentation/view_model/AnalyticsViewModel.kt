package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.*
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

    private val _walletCoins = MutableLiveData<List<CoinModel>>()
    val walletCoins: LiveData<List<CoinModel>>
        get() = _walletCoins

    private val _userDataLiveData = MutableLiveData<UserData>()
    val userDataLiveData: LiveData<UserData>
        get() = _userDataLiveData

    val combinedLiveData = MediatorLiveData<Pair<UserData?, List<CoinModel>>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getUserDataUseCase.execute(1).collect { userData ->
                _userDataLiveData.postValue(userData)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            getAllCoinsUseCase.execute().collect(){
                _walletCoins.postValue(it)
            }
        }
    }
}