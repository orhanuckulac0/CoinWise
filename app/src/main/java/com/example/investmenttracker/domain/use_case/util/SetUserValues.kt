package com.example.investmenttracker.domain.use_case.util

import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData

fun setUserValues(currentUser: UserData, coinsList: List<CoinModel>): UserData {
    var userTotalInvestment = 0.0
    var userTotalInvestmentWorth = 0.0

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
    currentUser.userCurrentCurrency = currentUser.userCurrentCurrency
    currentUser.userPreviousCurrency = currentUser.userPreviousCurrency
    currentUser.userTotalCoinInvestedQuantity = coinsList.size
    currentUser.lastApiRequestMade = System.currentTimeMillis()
    return currentUser
}