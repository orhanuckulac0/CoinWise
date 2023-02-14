package com.example.investmenttracker.presentation.view_model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.investmenttracker.data.util.Resource
import com.example.investmenttracker.domain.use_case.GetCoinBySlugUseCase
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchCoinViewModel(
    private val app: Application,
    private val getCoinUseCase: GetCoinBySlugUseCase
): AndroidViewModel(app) {

    private var _coinSearchInputText = MutableLiveData("")
    val coinSearchInputText = _coinSearchInputText

    val coinSearched: MutableLiveData<Resource<JsonObject>> = MutableLiveData()

    fun getSearchedCoin(name: String) = viewModelScope.launch(Dispatchers.IO) {
        coinSearched.postValue(Resource.Loading())

        if (isNetworkAvailable(app)){
            val response = getCoinUseCase.execute(name).execute()
            coinSearched.postValue(Resource.Success(response.body()!!))

        }else {
            coinSearched.postValue(Resource.Error("No Internet Connection Error"))
        }
    }

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

}