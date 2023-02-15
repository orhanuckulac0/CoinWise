package com.example.investmenttracker.data.repository.datasource_impl

import com.example.investmenttracker.data.db.CoinDAO
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.repository.datasource.CoinLocalDataSource
import kotlinx.coroutines.flow.Flow

class CoinLocalDataSourceImpl(private val coinDAO: CoinDAO): CoinLocalDataSource {

    override suspend fun insertCoinToDB(coin: CoinModel) {
        return coinDAO.insertCoin(coin)
    }

    override fun getAllCoinsFromDB(): Flow<List<CoinModel>> {
        return coinDAO.getAllCoins()
    }

    override suspend fun deleteCoinFromDB(coin: CoinModel) {
        return coinDAO.deleteCoin(coin)
    }
}