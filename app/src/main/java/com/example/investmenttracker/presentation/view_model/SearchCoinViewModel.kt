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
import com.example.investmenttracker.data.util.Resource
import com.example.investmenttracker.domain.use_case.DeleteCoinUseCase
import com.example.investmenttracker.domain.use_case.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.GetCoinBySlugUseCase
import com.example.investmenttracker.domain.use_case.SaveCoinUseCase
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchCoinViewModel(
    private val app: Application,
    private val getCoinBySlugUseCase: GetCoinBySlugUseCase,
    private val saveCoinUseCase: SaveCoinUseCase,
): AndroidViewModel(app) {

    private var _coinSearchInputText = MutableLiveData("")
    val coinSearchInputText = _coinSearchInputText

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

    fun getSearchedCoin(name: String) = viewModelScope.launch(Dispatchers.IO) {
        coinSearched.postValue(Resource.Loading())

        if (isNetworkAvailable(app)){
            val slugResponse = getCoinBySlugUseCase.execute(name).execute()
            if (slugResponse.body() == null){
                // TODO now search by name
                // TODO if search by name is null, then search by symbol
                // TODO if all returns null, show toast for the error
            }
            coinSearched.postValue(Resource.Success(slugResponse.body()!!))

        }else {
            coinSearched.postValue(Resource.Error("No Internet Connection Error"))
        }
    }

    fun saveCoinToDB(coinModel: CoinModel) = viewModelScope.launch(Dispatchers.IO) {
        saveCoinUseCase.execute(coinModel)
    }

}