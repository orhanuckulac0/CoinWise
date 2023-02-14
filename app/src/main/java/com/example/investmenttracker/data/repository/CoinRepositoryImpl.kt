package com.example.investmenttracker.data.repository

import com.example.investmenttracker.data.repository.datasource.CoinRemoteDataSource
import com.example.investmenttracker.domain.repository.CoinRepository
import com.google.gson.JsonObject
import retrofit2.Call

class CoinRepositoryImpl(
    private val coinRemoteDataSource: CoinRemoteDataSource
): CoinRepository {
    override suspend fun getCoinBySlug(name: String): Call<JsonObject> {
        return coinRemoteDataSource.getCoinBySlug(name)
    }
}