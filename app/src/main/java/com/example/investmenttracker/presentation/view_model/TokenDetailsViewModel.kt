package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.domain.use_case.coin.DeleteCoinUseCase
import com.example.investmenttracker.domain.use_case.coin.UpdateInvestmentUseCase
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.UpdateUserDataUseCase
import com.example.investmenttracker.presentation.events.UiEvent
import com.example.investmenttracker.presentation.events.UiEventActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TokenDetailsViewModel(
    app: Application,
    private val updateInvestmentUseCase: UpdateInvestmentUseCase,
    private val deleteCoinUseCase: DeleteCoinUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase
    ): AndroidViewModel(app) {

    private val eventChannel = Channel<UiEvent>()
    val eventFlow = eventChannel.receiveAsFlow()
    var userData: UserData? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getUserDataUseCase.execute(1).collect{
                userData = it
            }
        }
    }

    fun updateTokenDetails(id: Int, totalTokenHeldAmount: Double, totalInvestmentAmount: Double, totalInvestmentWorth: Double){
        viewModelScope.launch(Dispatchers.IO) {
            updateInvestmentUseCase.execute(id, totalTokenHeldAmount, totalInvestmentAmount, totalInvestmentWorth)
        }
    }

    fun checkEmptyInput(totalTokenHeld: String, totalInvestment: String): Boolean {
        if (totalTokenHeld.isEmpty()){
            triggerUiEvent(UiEventActions.TOTAL_TOKEN_HELD_EMPTY, UiEventActions.TOTAL_TOKEN_HELD_EMPTY)
            return false
        }else if (totalInvestment.isEmpty()){
            triggerUiEvent(UiEventActions.TOTAL_INVESTMENT_EMPTY, UiEventActions.TOTAL_INVESTMENT_EMPTY)
            return false
        }
        return true
    }

    fun deleteTokenFromDB(coin: CoinModel) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteCoinUseCase.execute(coin)
        }
    }

    fun updateUserDB(userData: UserData){
        viewModelScope.launch(Dispatchers.IO) {
            updateUserDataUseCase.execute(userData)
        }
    }

    private fun triggerUiEvent(message: String, action: String) = viewModelScope.launch(Dispatchers.Main) {
        if (action == UiEventActions.TOTAL_INVESTMENT_EMPTY || action == UiEventActions.TOTAL_TOKEN_HELD_EMPTY) {
            eventChannel.send(UiEvent.ShowErrorSnackbar(message))
        }
    }
}