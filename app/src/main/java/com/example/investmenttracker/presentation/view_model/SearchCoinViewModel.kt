package com.example.investmenttracker.presentation.view_model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.inputmethod.InputMethodSession.EventCallback
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.util.Resource
import com.example.investmenttracker.domain.use_case.*
import com.example.investmenttracker.presentation.events.UiEvent
import com.example.investmenttracker.presentation.events.UiEventActions
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SearchCoinViewModel(
    private val app: Application,
    private val getCoinBySlugUseCase: GetCoinBySlugUseCase,
    private val getCoinBySymbolUseCase: GetCoinBySymbolUseCase,
    private val saveCoinUseCase: SaveCoinUseCase,
): AndroidViewModel(app) {

    private var _coinSearchInputText = MutableLiveData("")
    val coinSearchInputText = _coinSearchInputText

    private val eventChannel = Channel<UiEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    val coinSearched: MutableLiveData<Resource<JsonObject>> = MutableLiveData()

    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.run {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
        return result
    }

    fun getSearchedCoinBySlug(slug: String) = viewModelScope.launch(Dispatchers.IO) {
        coinSearched.postValue(Resource.Loading())

        if (isNetworkAvailable(app)){
            val slugResponse = getCoinBySlugUseCase.execute(slug).execute()
            if (slugResponse.body() != null){
                coinSearched.postValue(Resource.Success(slugResponse.body()!!))
            }else {
                coinSearched.postValue(Resource.Error(UiEventActions.COIN_ADDED_FAILED))
            }
        }else {
            coinSearched.postValue(Resource.Error(UiEventActions.NO_INTERNET_CONNECTION))
        }
    }

    fun getSearchCoinBySymbol(symbol: String) = viewModelScope.launch(Dispatchers.IO) {
        coinSearched.postValue(Resource.Loading())

        if (isNetworkAvailable(app)){
            val symbolResponse = getCoinBySymbolUseCase.execute(symbol).execute()
            if (symbolResponse.body() != null){
                coinSearched.postValue(Resource.Success(symbolResponse.body()!!))
            }else {
                coinSearched.postValue(Resource.Error(UiEventActions.COIN_ADDED_FAILED))
            }
        }else {
            coinSearched.postValue(Resource.Error(UiEventActions.NO_INTERNET_CONNECTION))
        }
    }

    fun saveCoinToDB(coinModel: CoinModel) = viewModelScope.launch(Dispatchers.IO) {
        saveCoinUseCase.execute(coinModel)
    }

    fun triggerUiEvent(message: String, action: String) = viewModelScope.launch(Dispatchers.Main) {
        if (action == UiEventActions.COIN_ADDED) {
            eventChannel.send(UiEvent.ShowCoinAddedSnackbar(message))
        } else if (action == UiEventActions.COIN_ADDED_FAILED || action == UiEventActions.NO_INTERNET_CONNECTION) {
            eventChannel.send(UiEvent.ShowErrorSnackbar(message))
        }
    }
}