package com.example.investmenttracker.domain.use_case.util

import com.example.investmenttracker.data.model.CoinModel

fun sortCoinsByAscending(coinsList: List<CoinModel>): List<CoinModel>{
    return coinsList.sortedBy { it.totalInvestmentWorth }
}

fun sortCoinsByDescending(coinsList: List<CoinModel>): List<CoinModel> {
    return coinsList.sortedByDescending { it.totalInvestmentWorth }
}

fun mostProfitByCoin(coinsList: List<CoinModel>): CoinModel {
    return coinsList.maxBy {
        it.totalInvestmentWorth-it.totalInvestmentAmount
    }
}

fun mostLossByCoin(coinsList: List<CoinModel>): CoinModel {
    return coinsList.minBy {
        it.totalInvestmentWorth-it.totalInvestmentAmount
    }
}

fun mostProfitPercentageByCoin(coinsList: List<CoinModel>): List<String> {
    var maxProfitCoin: CoinModel? = null
    var maxProfitPercentage = Double.MIN_VALUE

    for (coin in coinsList){
        val profitPercentage = (coin.totalInvestmentWorth - coin.totalInvestmentAmount) / coin.totalInvestmentAmount * 100
        if (profitPercentage > maxProfitPercentage){
            maxProfitPercentage = profitPercentage
            maxProfitCoin = coin
        }
    }
    if (maxProfitCoin != null) {
        return (formatCoinNameText(maxProfitCoin.symbol) + " " + formatToTwoDecimal(
            maxProfitPercentage
        ).toString() + "%").split(" ")
    }else{
        return emptyList()
    }
}

fun mostLossPercentageByCoin(coinsList: List<CoinModel>): List<String> {
    var minProfitCoin: CoinModel? = null
    var minProfitPercentage = Double.MIN_VALUE

    for (coin in coinsList){
        val profitPercentage = (coin.totalInvestmentWorth - coin.totalInvestmentAmount) / coin.totalInvestmentAmount * 100
        if (profitPercentage < minProfitPercentage){
            minProfitPercentage = profitPercentage
            minProfitCoin = coin
        }
    }
    if (minProfitCoin != null) {
        return (formatCoinNameText(minProfitCoin.symbol) + " " + formatToTwoDecimal(
            minProfitPercentage
        ).toString() + "%").split(" ")
    }else{
        return emptyList()
    }

}