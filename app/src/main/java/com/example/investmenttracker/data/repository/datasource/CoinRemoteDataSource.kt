package com.example.investmenttracker.data.repository.datasource

import com.google.gson.JsonObject
import retrofit2.Call

interface CoinRemoteDataSource {

    suspend fun getCoinBySlug(name: String): Call<JsonObject>
    
}