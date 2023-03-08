package com.example.investmenttracker.presentation.view_model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.*
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.data.util.Resource
import com.example.investmenttracker.domain.use_case.coin.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.GetMultipleCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.UpdateCoinDetailsUseCase
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.InsertUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.UpdateUserDataUseCase
import com.example.investmenttracker.presentation.events.UiEventActions
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock

class MainViewModel(
    private val app:Application,
    private val getAllCoinsUseCase: GetAllCoinsUseCase,
    private val insertUserDataUseCase: InsertUserDataUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase,
    private val getMultipleCoinsUseCase: GetMultipleCoinsUseCase,
    private val updateCoinDetailsUseCase: UpdateCoinDetailsUseCase
): AndroidViewModel(app) {

    var userData: UserData? = null

    val multipleCoinsListResponse: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var currentWalletCoins = mutableListOf<CoinModel>()
    var slugNames = ""

    // Create an instance of ReentrantLock
    val databaseUpdateLock = ReentrantLock()
    var isDatabaseUpdateInProgress = false

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

    fun getUserData(id: Int) {
        viewModelScope.launch {
            getUserDataUseCase.execute(id).collect {
                userData = it
            }
        }
    }

    fun updateUserdata(data: UserData){
        viewModelScope.launch {
            updateUserDataUseCase.execute(data)
        }
    }

    fun insertUserData(data: UserData) {
        viewModelScope.launch {
            insertUserDataUseCase.execute(data)
        }
    }

    fun getTokensFromWallet() {
        if (!isDatabaseUpdateInProgress){
            viewModelScope.launch {
                getAllCoinsUseCase.execute()
                    .distinctUntilChanged()
                    .collect { walletCoins ->
                        Log.i("MYTAG", "db tokens $walletCoins")

                        currentWalletCoins.clear()
                        currentWalletCoins.addAll(walletCoins)

                        val walletTokenNames = currentWalletCoins.map { coin -> coin.slug.replace("\"", "") }
                        val joinedNames = walletTokenNames.joinToString(",")
                        slugNames = joinedNames
                        Log.i("MYTAG", "joined names for api request $slugNames")
                    }
            }
        }
    }

    fun getMultipleCoinsBySlug(slugList: List<String>) = viewModelScope.launch(Dispatchers.IO) {
        multipleCoinsListResponse.postValue(Resource.Loading())
        try {
            if (isNetworkAvailable(app)){
                val slugListResponse = getMultipleCoinsUseCase.execute(slugList)
                multipleCoinsListResponse.postValue(Resource.Success(slugListResponse))
            }else {
                multipleCoinsListResponse.postValue(Resource.Error(UiEventActions.NO_INTERNET_CONNECTION))
            }
        }catch (e:java.lang.Exception){
            multipleCoinsListResponse.postValue(Resource.Error("${e.message}"))
        }
    }

    suspend fun updateMultipleCoinDetails(coins: List<CoinModel>) = viewModelScope.launch(Dispatchers.IO) {
        updateCoinDetailsUseCase.execute(coins)
    }
}