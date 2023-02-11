package com.example.investmenttracker.presentation.view_model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.investmenttracker.data.model.ApiResponse
import com.example.investmenttracker.data.util.Resource
import com.example.investmenttracker.domain.use_case.GetCoinUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*

class CoinViewModel(
    private val app: Application,
    private val getCoinUseCase: GetCoinUseCase
): AndroidViewModel(app) {

    val coinSearched: MutableLiveData<Resource<ApiResponse>> = MutableLiveData()

    fun getCoin(name: String) = viewModelScope.launch(Dispatchers.IO) {
        coinSearched.postValue(Resource.Loading())
        try {
            if (isNetworkAvailable(app)){
                val apiResult = getCoinUseCase.execute(name)
                coinSearched.postValue(apiResult)
            }else{
                coinSearched.postValue(Resource.Error("Internet is not available"))
            }
        }catch (e: java.lang.Exception){
            coinSearched.postValue(Resource.Error(e.message.toString()))
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