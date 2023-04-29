package com.example.investmenttracker.presentation.view_model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.*
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.CurrencyModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.domain.use_case.coin.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.GetMultipleCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.UpdateCoinDetailsUseCase
import com.example.investmenttracker.domain.use_case.currency.AddCurrencyDataToDBUseCase
import com.example.investmenttracker.domain.use_case.currency.GetCurrencyValueFromDBUseCase
import com.example.investmenttracker.domain.use_case.currency.GetNewCurrencyValueUseCase
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.InsertUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.UpdateUserDataUseCase
import com.example.investmenttracker.domain.use_case.util.*
import com.example.investmenttracker.presentation.events.UiEvent
import com.example.investmenttracker.presentation.events.UiEventActions
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val app:Application,
    private val getAllCoinsUseCase: GetAllCoinsUseCase,
    private val insertUserDataUseCase: InsertUserDataUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase,
    private val getMultipleCoinsUseCase: GetMultipleCoinsUseCase,
    private val updateCoinDetailsUseCase: UpdateCoinDetailsUseCase,
    private val getNewCurrencyValueUseCase: GetNewCurrencyValueUseCase,
    private val addCurrencyDataToDBUseCase: AddCurrencyDataToDBUseCase,
    private val getCurrencyValueFromDBUseCase: GetCurrencyValueFromDBUseCase
): AndroidViewModel(app) {

    var userData: MutableLiveData<UserData> = MutableLiveData()

    val multipleCoinsListResponse: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var currentWalletCoins = mutableListOf<CoinModel>()
    var newTokensDataResponse = mutableListOf<CoinModel>()
    var temporaryTokenListToUseOnFragment = mutableListOf<CoinModel>()
    var walletTokensToUpdateDB = mutableListOf<CoinModel>()
    var slugNames: MutableLiveData<Resource<String>> = MutableLiveData()

    var currencyRequestResult: MutableLiveData<Resource<Map<String, Float>>> = MutableLiveData()
    var currencyData: MutableLiveData<CurrencyModel> = MutableLiveData()

    private val eventChannel = Channel<UiEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    init {
        // get current user and set it in viewModel to later use in Fragment
        viewModelScope.launch(Dispatchers.IO) {
            getUserDataUseCase.execute(1).collect {
                userData.postValue(it)
            }
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

    fun updateUserdata(data: UserData){
        viewModelScope.launch(Dispatchers.IO) {
            updateUserDataUseCase.execute(data)
        }
    }

    fun insertUserData(data: UserData) {
        viewModelScope.launch(Dispatchers.IO) {
            insertUserDataUseCase.execute(data)
        }
    }

    fun getTokensFromWallet() {
        viewModelScope.launch(Dispatchers.IO) {
            slugNames.postValue(Resource.Loading())
            try {
                getAllCoinsUseCase.execute()
                    .distinctUntilChanged()
                    .collect { walletCoins ->
                    Log.i("MYTAG", "db tokens $walletCoins")

                        currentWalletCoins.clear()
                        currentWalletCoins.addAll(walletCoins)
                        Log.i("MYTAG", "currentWalletCoins $currentWalletCoins")

                        // to send API get request with ["slug1,slug2,slug3"] query
                        val walletTokenNames = currentWalletCoins.map { coin -> coin.slug.replace("\"", "") }
                        val joinedNames = walletTokenNames.joinToString(",")
                        slugNames.postValue(Resource.Success(joinedNames))
                        Log.i("MYTAG", "joined names for api request: -> ${slugNames.value?.data}")
                    }
            }catch (e: ConcurrentModificationException){
                e.printStackTrace()
                Log.i("MYTAG", "${e.stackTrace}")
                slugNames.postValue(Resource.Error(e.stackTrace.toString()))
            }
        }
    }

    fun getMultipleCoinsBySlug(slugList: List<String>) = viewModelScope.launch(Dispatchers.IO) {
        multipleCoinsListResponse.postValue(Resource.Loading())
        try {
            if (isNetworkAvailable(app)){
                // validate the list first before making an api request
                // prevent making api request with empty query, resulting HTTP 400
                if (slugList.isNotEmpty()){
                    val slugListResponse = getMultipleCoinsUseCase.execute(slugList)
                    multipleCoinsListResponse.postValue(Resource.Success(slugListResponse))
                }else{
                    multipleCoinsListResponse.postValue(Resource.Error("empty wallet, no api request has been made."))
                }
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
                        percentChange24h = coin.percentChange24h
                        totalInvestmentWorth = formatToTwoDecimal(coin.price * walletCoin.totalTokenHeldAmount)
                    }
                    walletTokensToUpdateDB.add(updatedCoin)
                }
            }
        }

        Log.i("MYTAG", "new wallet: $newTokensDataResponse")
        temporaryTokenListToUseOnFragment.addAll(walletTokensToUpdateDB)
        updateCoinDetailsUseCase.execute(walletTokensToUpdateDB)
    }

    fun parseCoinAPIResponse(data: JsonObject) {
        newTokensDataResponse = parseMultipleCoinsResponseUtil(data)
    }

    fun getNewCurrencyValuesAPIRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            currencyRequestResult.postValue(Resource.Loading())

            try {
                if (isNetworkAvailable(app)) {
                    getNewCurrencyValueUseCase.execute().let { result ->

                        val parsedResult = parseCurrencyAPIResponse(result)!!
                        currencyRequestResult.postValue(Resource.Success(parsedResult))

                        insertDataToCurrencyDB(parsedResult)
                    }
                } else {
                    currencyRequestResult.postValue(Resource.Error(UiEventActions.NO_INTERNET_CONNECTION))
                }
            } catch (e: java.lang.Exception) {
                currencyRequestResult.postValue(Resource.Error("${e.message}"))
            }
        }
    }

    private fun insertDataToCurrencyDB(data: Map<String, Float>){
        viewModelScope.launch(Dispatchers.IO) {
            // use count as key to trigger OnConflict
            var count = 1
            data.forEach {
                addCurrencyDataToDBUseCase.execute(CurrencyModel(
                    count,
                    currencyName = it.key,
                    currencyRate = it.value
                ))
                count += 1
            }
        }
    }

    fun getCurrencyDataFromDB(currencyName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getCurrencyValueFromDBUseCase.execute(currencyName).collect {
                currencyData.postValue(it)
            }
        }
    }

    fun triggerUiEvent(message: String, action: String) = viewModelScope.launch(Dispatchers.Main) {
        if (action == UiEventActions.NO_INTERNET_CONNECTION) {
            eventChannel.send(UiEvent.ShowErrorSnackbar(message))
        }
    }

}