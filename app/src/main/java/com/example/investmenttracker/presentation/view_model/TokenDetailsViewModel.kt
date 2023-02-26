package com.example.investmenttracker.presentation.view_model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.domain.use_case.coin.DeleteCoinUseCase
import com.example.investmenttracker.domain.use_case.coin.UpdateCoinUseCase
import com.example.investmenttracker.presentation.events.UiEvent
import com.example.investmenttracker.presentation.events.UiEventActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TokenDetailsViewModel(
    private val app: Application,
    private val updateCoinUseCase: UpdateCoinUseCase,
    private val deleteCoinUseCase: DeleteCoinUseCase
    ): AndroidViewModel(app) {

    private val eventChannel = Channel<UiEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

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

    fun updateTokenDetails(id: Int, totalTokenHeldAmount: Double, totalInvestmentAmount: Double){
        if (isNetworkAvailable(app)){
            viewModelScope.launch {
                updateCoinUseCase.execute(id, totalTokenHeldAmount, totalInvestmentAmount)
            }
        }else{
            triggerUiEvent(UiEventActions.NO_INTERNET_CONNECTION, UiEventActions.NO_INTERNET_CONNECTION)
        }
    }

    fun deleteTokenFromDB(coin: CoinModel) {
        if (isNetworkAvailable(app)){
            viewModelScope.launch {
                deleteCoinUseCase.execute(coin)
            }
        }else{
            triggerUiEvent(UiEventActions.NO_INTERNET_CONNECTION, UiEventActions.NO_INTERNET_CONNECTION)
        }
    }

    fun triggerUiEvent(message: String, action: String) = viewModelScope.launch(Dispatchers.Main) {
        if (action == UiEventActions.TOTAL_INVESTMENT_EMPTY || action == UiEventActions.TOTAL_TOKEN_HELD_EMPTY) {
            eventChannel.send(UiEvent.ShowErrorSnackbar(message))
        }
    }
}