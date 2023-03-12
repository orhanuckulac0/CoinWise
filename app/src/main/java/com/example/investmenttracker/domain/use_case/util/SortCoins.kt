package com.example.investmenttracker.domain.use_case.util

import com.example.investmenttracker.data.model.CoinModel

fun sortCoinsByAscending(coinsList: List<CoinModel>): List<CoinModel>{
    return coinsList.sortedBy { it.totalInvestmentWorth }
}

fun sortCoinsByDescending(coinsList: List<CoinModel>): List<CoinModel> {
    return coinsList.sortedByDescending { it.totalInvestmentWorth }
}