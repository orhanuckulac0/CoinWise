package com.example.investmenttracker.data.api

import com.example.investmenttracker.BuildConfig
import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyConverterAPIService {

    @GET(BuildConfig.CURRENCY_API_URL)
    suspend fun convertCurrencies(
        @Query("api_key")
        API_KEY: String = BuildConfig.CURRENCY_KEY,
        @Query("base")
        base: String,
    ): JsonObject

}