package com.example.investmenttracker.data.repository.datasource

import com.google.gson.JsonObject

interface CoinRemoteDataSource {

    suspend fun getCoinBySlug(slug: String): JsonObject
    suspend fun getCoinBySymbol(symbol: String): JsonObject
    suspend fun getMultipleCoinsBySlug(slugList: List<String>): JsonObject

}