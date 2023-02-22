package com.example.investmenttracker.data.repository.datasource_impl

import com.example.investmenttracker.data.api.CoinSearchAPIService
import com.example.investmenttracker.data.repository.datasource.CoinRemoteDataSource
import com.google.gson.JsonObject
import retrofit2.Call

class CoinRemoteDataSourceImpl(
    private val coinSearchAPIService: CoinSearchAPIService,
): CoinRemoteDataSource {

    override suspend fun getCoinBySlug(slug: String): JsonObject {
        return coinSearchAPIService.getCoinBySlug(slug)
    }

    override suspend fun getCoinBySymbol(symbol: String): JsonObject {
        return coinSearchAPIService.getCoinBySymbol(symbol)
    }

}