package com.example.investmenttracker.data.repository.datasource

import com.example.investmenttracker.data.model.CoinModel
import kotlinx.coroutines.flow.Flow

interface CoinLocalDataSource {
    suspend fun insertCoinToDB(coin: CoinModel)

    fun getAllCoinsFromDB(): Flow<List<CoinModel>>

    fun getSingleCoinById(id: Int): Flow<CoinModel>

    suspend fun deleteCoinFromDB(coin: CoinModel)
}