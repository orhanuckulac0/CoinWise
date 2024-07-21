package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.*
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.CurrencyModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.domain.use_case.coin.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.currency.GetAllCurrenciesUseCase
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.domain.use_case.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnalyticsViewModel(
    app: Application,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getAllCoinsUseCase: GetAllCoinsUseCase,
    private val getAllCurrenciesUseCase: GetAllCurrenciesUseCase
    ): AndroidViewModel(app) {

    private val _walletCoins = MutableLiveData<List<CoinModel>>()
    val walletCoins: LiveData<List<CoinModel>>
        get() = _walletCoins

    private val _userDataLiveData = MutableLiveData<UserData>()
    val userDataLiveData: LiveData<UserData>
        get() = _userDataLiveData

    val combinedLiveData = MediatorLiveData<Pair<UserData?, List<CoinModel>>>()

    val currencyData: MutableLiveData<Resource<List<CurrencyModel>>> = MutableLiveData()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getUserDataUseCase.execute(1).collect { userData ->
                _userDataLiveData.postValue(userData)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            getAllCoinsUseCase.execute().collect {
                _walletCoins.postValue(it)
            }
        }

        combinedLiveData.addSource(_userDataLiveData) { userData ->
            combinedLiveData.value = Pair(userData, _walletCoins.value ?: emptyList())
        }

        combinedLiveData.addSource(_walletCoins) { coins ->
            combinedLiveData.value = Pair(_userDataLiveData.value, coins)
        }

    }

    fun getCurrencyData() {
        viewModelScope.launch(Dispatchers.IO) {
            currencyData.postValue(Resource.Loading())
            try {
                getAllCurrenciesUseCase.execute().collect {
                    currencyData.postValue(Resource.Success(it))
                }
            }catch (e: java.lang.Exception){
                currencyData.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        combinedLiveData.apply {
            removeSource(_userDataLiveData)
            removeSource(_walletCoins)
        }
    }
}