package com.example.investmenttracker.data.repository

import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.repository.datasource.CoinLocalDataSource
import com.example.investmenttracker.data.repository.datasource.CoinRemoteDataSource
import com.example.investmenttracker.domain.repository.CoinRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

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

    override suspend fun getMultipleCoinsBySlug(slugList: List<String>): JsonObject {
        return coinRemoteDataSource.getMultipleCoinsBySlug(slugList)
    }

    override suspend fun insertCoinToDB(coinModel: CoinModel) {
        return coinLocalDataSource.insertCoinToDB(coinModel)
    }

    override suspend fun updateCoinInvestmentDetails(id: Int, totalTokenHeldAmount: Double, totalInvestmentAmount: Double, totalInvestmentWorth: Double) {
        return coinLocalDataSource.updateCoinInvestmentDetails(id, totalTokenHeldAmount, totalInvestmentAmount, totalInvestmentWorth)
    }

    override suspend fun updateCoinDetails(coins: List<CoinModel>) {
        return coinLocalDataSource.updateCoinDetails(coins)
    }

    override suspend fun deleteCoinFromDB(coin: CoinModel) {
        return coinLocalDataSource.deleteCoinFromDB(coin)
    }

    override fun getSingleCoinById(id: Int): Flow<CoinModel> {
        return coinLocalDataSource.getSingleCoinById(id)
    }

    override fun getAllCoinsFromDB(): Flow<List<CoinModel>> {
        return coinLocalDataSource.getAllCoinsFromDB()
    }
}