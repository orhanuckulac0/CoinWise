package com.example.investmenttracker.data.repository

import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.repository.datasource.CoinLocalDataSource
import com.example.investmenttracker.data.repository.datasource.CoinRemoteDataSource
import com.example.investmenttracker.domain.repository.CoinRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import retrofit2.Call

class CoinRepositoryImpl(
    private val coinRemoteDataSource: CoinRemoteDataSource,
    private val coinLocalDataSource: CoinLocalDataSource
): CoinRepository {
    override suspend fun getCoinBySlug(slug: String): JsonObject {
        return coinRemoteDataSource.getCoinBySlug(slug)
    }

    override suspend fun getCoinBySymbol(symbol: String): JsonObject {
        return coinRemoteDataSource.getCoinBySymbol(symbol)
    }

    override suspend fun insertCoinToDB(coinModel: CoinModel) {
        return coinLocalDataSource.insertCoinToDB(coinModel)
    }

    override suspend fun deleteCoinFromDB(coin: CoinModel) {
        return coinLocalDataSource.deleteCoinFromDB(coin)
    }

    override fun getAllCoinsFromDB(): Flow<List<CoinModel>> {
        return coinLocalDataSource.getAllCoinsFromDB()
    }
}