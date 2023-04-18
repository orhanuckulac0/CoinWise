package com.example.investmenttracker.domain.use_case.util

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
    return currentUser
}