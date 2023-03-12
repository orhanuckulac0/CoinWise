package com.example.investmenttracker.presentation.view_model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.*
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.domain.use_case.util.Resource
import com.example.investmenttracker.domain.use_case.coin.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.GetMultipleCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.UpdateCoinDetailsUseCase
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.InsertUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.UpdateUserDataUseCase
import com.example.investmenttracker.domain.use_case.util.formatPrice
import com.example.investmenttracker.domain.use_case.util.parseMultipleCoinsResponseUtil
import com.example.investmenttracker.presentation.events.UiEventActions
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainViewModel(
    private val app:Application,
    private val getAllCoinsUseCase: GetAllCoinsUseCase,
    private val insertUserDataUseCase: InsertUserDataUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase,
    private val getMultipleCoinsUseCase: GetMultipleCoinsUseCase,
    private val updateCoinDetailsUseCase: UpdateCoinDetailsUseCase,
): AndroidViewModel(app) {

    var userData: UserData? = null

    val multipleCoinsListResponse: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var currentWalletCoins = mutableListOf<CoinModel>()
    var newTokensDataResponse = mutableListOf<CoinModel>()
    var walletTokensToUpdateDB = mutableListOf<CoinModel>()
    var slugNames = ""

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
        val startTime = System.nanoTime()

        viewModelScope.launch {
            getAllCoinsUseCase.execute()
                .distinctUntilChanged()
                .collect { walletCoins ->
                    Log.i("MYTAG", "db tokens $walletCoins")

                    currentWalletCoins.clear()
                    currentWalletCoins.addAll(walletCoins)

                    // to send API get request with ["slug1,slug2,slug3"] query
                    val walletTokenNames = currentWalletCoins.map { coin -> coin.slug.replace("\"", "") }
                    val joinedNames = walletTokenNames.joinToString(",")
                    slugNames = joinedNames
                    Log.i("MYTAG", "joined names for api request $slugNames")
                }
        }
        val endTime = System.nanoTime()
        val elapsedTime = (endTime - startTime) / 1000000.0 // convert nanoseconds to milliseconds
        Log.i("MyFunction", "Elapsed time: $elapsedTime ms")

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

    suspend fun updateMultipleCoinDetails() = viewModelScope.launch(Dispatchers.IO) {
        // this will loop through each lists
        // and update tokens in currentWalletCoins with the coins the latest api request returns
        // some values of the tokens will remain the same, such as token held amount investment amount etc.
        // copy creates a new obj but I wont use it so I just applied it to the current walletTokensToUpdateDB.
        for (walletCoin in currentWalletCoins){
            for (coin in newTokensDataResponse){
                if (walletCoin.cmcId == coin.cmcId){
                    val updatedCoin = walletCoin.apply {
                        price = formatPrice(coin.price).toDouble()
                        marketCap = formatPrice(coin.marketCap).toDouble()
                        percentChange1h = coin.percentChange1h
                        percentChange24h = coin.percentChange24h
                        percentChange7d = coin.percentChange7d
                        percentChange30d = coin.percentChange30d
                    }
                    walletTokensToUpdateDB.add(updatedCoin)
                }
            }
        }

        Log.i("MYTAG", "new wallet: $newTokensDataResponse")
        updateCoinDetailsUseCase.execute(walletTokensToUpdateDB)
    }

    fun parseAPIResponse(data: JsonObject) {
        newTokensDataResponse = parseMultipleCoinsResponseUtil(data)
    }
}