package com.example.investmenttracker.data.repository.datasource_impl

import com.example.investmenttracker.data.db.CoinDAO
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.repository.datasource.CoinLocalDataSource
import kotlinx.coroutines.flow.Flow

class CoinLocalDataSourceImpl(private val coinDAO: CoinDAO): CoinLocalDataSource {

    override suspend fun insertCoinToDB(coin: CoinModel) {
        return coinDAO.insertCoin(coin)
    }

    override suspend fun updateCoinInvestmentDetails(id: Int, totalTokenHeldAmount: Double, totalInvestmentAmount: Double, totalInvestmentWorth: Double) {
        return coinDAO.updateCoinInvestmentDetails(id, totalTokenHeldAmount, totalInvestmentAmount, totalInvestmentWorth)
    }

    override suspend fun updateCoinDetails(coins: List<CoinModel>) {
        return coinDAO.updateCoinDetails(coins)
    }


    override fun getAllCoinsFromDB(): Flow<List<CoinModel>> {
        return coinDAO.getAllCoins()
    }

    override fun getSingleCoinById(id: Int): Flow<CoinModel> {
        return coinDAO.getSingleCoinById(id)
    }

    override suspend fun deleteCoinFromDB(coin: CoinModel) {
        return coinDAO.deleteCoin(coin)
    }
}