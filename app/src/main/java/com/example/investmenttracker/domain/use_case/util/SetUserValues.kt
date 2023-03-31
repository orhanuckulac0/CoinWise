package com.example.investmenttracker.domain.use_case.util

import android.util.Log
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData

// to set user model with latest values before updating on db
fun setUserValues(currentUser: UserData, coinsList: List<CoinModel>): UserData {
    var userTotalInvestment = 0.0
    var userTotalInvestmentWorth = 0.0

    // calculate UserData values
    coinsList.forEach {
        userTotalInvestment += it.totalInvestmentAmount
        userTotalInvestmentWorth += it.totalInvestmentWorth
    }
    val userTotalProfitAndLoss = formatToTwoDecimal(userTotalInvestmentWorth - userTotalInvestment)
    var userTotalProfitAndLossPercentage: Double = calculateProfitLossPercentage(userTotalInvestmentWorth, userTotalInvestment).replace("%","").toDouble()
    if (userTotalProfitAndLossPercentage.isNaN()){
        userTotalProfitAndLossPercentage = 0.0
    }

    currentUser.userTotalInvestment = formatToTwoDecimal(userTotalInvestment)
    currentUser.userTotalBalanceWorth = formatToTwoDecimal(userTotalInvestmentWorth)
    currentUser.userTotalProfitAndLoss = userTotalProfitAndLoss
    currentUser.userTotalProfitAndLossPercentage = userTotalProfitAndLossPercentage
    currentUser.userTotalCoinInvestedQuantity = coinsList.size
    Log.i("MYTAG", "USER DATA FRAGMENT: ${currentUser.id}")
    Log.i("MYTAG", "total investment : ${currentUser.userTotalInvestment}")
    Log.i("MYTAG", "userTotalBalanceWorth: ${currentUser.userTotalBalanceWorth}")
    Log.i("MYTAG", "userTotalProfitAndLoss: ${currentUser.userTotalProfitAndLoss}")
    Log.i("MYTAG", "userTotalProfitAndLossPercentage: ${currentUser.userTotalProfitAndLossPercentage}")
    Log.i("MYTAG", "quantity: ${currentUser.userTotalCoinInvestedQuantity}")
    return currentUser
}