package com.example.investmenttracker.data.repository.datasource_impl

import com.example.investmenttracker.data.api.CoinSearchAPIService
import com.example.investmenttracker.data.repository.datasource.CoinRemoteDataSource
import com.google.gson.JsonObject
import retrofit2.Call

class CoinRemoteDataSourceImpl(
    private val coinSearchAPIService: CoinSearchAPIService,
): CoinRemoteDataSource {

    override suspend fun getCoinBySlug(name: String): Call<JsonObject> {
        return coinSearchAPIService.getCoinBySlug(name)
    }

}