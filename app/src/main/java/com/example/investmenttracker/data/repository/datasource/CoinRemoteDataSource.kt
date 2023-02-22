package com.example.investmenttracker.data.repository.datasource

import com.google.gson.JsonObject
import retrofit2.Call

interface CoinRemoteDataSource {

    suspend fun getCoinBySlug(slug: String): JsonObject

    suspend fun getCoinBySymbol(symbol: String): JsonObject
    
}