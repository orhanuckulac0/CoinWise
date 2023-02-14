package com.example.investmenttracker.domain.repository

import com.google.gson.JsonObject
import retrofit2.Call

interface CoinRepository {
    suspend fun getCoinBySlug(name: String): Call<JsonObject>
}