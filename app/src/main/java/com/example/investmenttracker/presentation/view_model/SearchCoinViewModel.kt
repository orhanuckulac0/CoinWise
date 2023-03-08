package com.example.investmenttracker.presentation.view_model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.domain.use_case.util.Resource
import com.example.investmenttracker.domain.use_case.*
import com.example.investmenttracker.domain.use_case.coin.GetCoinBySlugUseCase
import com.example.investmenttracker.domain.use_case.coin.GetCoinBySymbolUseCase
import com.example.investmenttracker.domain.use_case.coin.SaveCoinUseCase
import com.example.investmenttracker.presentation.events.UiEvent
import com.example.investmenttracker.presentation.events.UiEventActions
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException

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

    val coinSearchedBySlug: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val coinSearchedBySymbol: MutableLiveData<Resource<JsonObject>> = MutableLiveData()

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
        coinSearchedBySlug.postValue(Resource.Loading())

        try {
            if (isNetworkAvailable(app)){
                val slugResponse = getCoinBySlugUseCase.execute(slug)
                coinSearchedBySlug.postValue(Resource.Success(slugResponse))
            }else {
                coinSearchedBySlug.postValue(Resource.Error(UiEventActions.NO_INTERNET_CONNECTION))
            }
        }catch (e:java.lang.Exception){
            coinSearchedBySlug.postValue(Resource.Error("No search results. Check your spelling."))
        }catch (e: InvocationTargetException) {
            Log.i("MYTAG", e.cause.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getSearchCoinBySymbol(symbol: String) = viewModelScope.launch(Dispatchers.IO) {
        coinSearchedBySymbol.postValue(Resource.Loading())

        try {
            if (isNetworkAvailable(app)){
                val symbolResponse = getCoinBySymbolUseCase.execute(symbol)
                coinSearchedBySymbol.postValue(Resource.Success(symbolResponse))
            }else {
                coinSearchedBySymbol.postValue(Resource.Error(UiEventActions.NO_INTERNET_CONNECTION))
            }
        }catch (e:java.lang.Exception){
            coinSearchedBySymbol.postValue(Resource.Error("No search results. Check your spelling."))
        }catch (e: InvocationTargetException) {
            Log.i("MYTAG", e.cause.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }
    }

    fun saveCoinToDB(coinModel: CoinModel) = viewModelScope.launch(Dispatchers.IO) {
        saveCoinUseCase.execute(coinModel)
    }

    fun triggerUiEvent(message: String, action: String) = viewModelScope.launch(Dispatchers.Main) {
        if (action == UiEventActions.COIN_ADDED) {
            eventChannel.send(UiEvent.ShowCoinAddedSnackbar(message))
        } else if (action == UiEventActions.NO_INTERNET_CONNECTION) {
            eventChannel.send(UiEvent.ShowErrorSnackbar(message))
        }
    }
}