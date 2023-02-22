package com.example.investmenttracker.domain.repository

import com.example.investmenttracker.data.model.CoinModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import retrofit2.Call

interface CoinRepository {
    // functions related to api calls
    suspend fun getCoinBySlug(slug: String): JsonObject

    suspend fun getCoinBySymbol(symbol: String): JsonObject

    // functions related to db
    suspend fun insertCoinToDB(coinModel: CoinModel)
    suspend fun deleteCoinFromDB(coin: CoinModel)
    fun getAllCoinsFromDB(): Flow<List<CoinModel>>

}