package com.example.investmenttracker.data.api

import com.example.investmenttracker.BuildConfig
import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyConverterAPIService {

    @GET("https://api.getgeoapi.com/v2/currency/convert")
    suspend fun convertCurrencies(
        @Query("api_key")
        API_KEY: String = BuildConfig.CURRENCY_KEY,
        @Query("from")
        from: String,
        @Query("to")
        to: String,
        @Query("amount")
        amount: Double,
        @Query("format")
        format: String = "json"
    ): JsonObject

}