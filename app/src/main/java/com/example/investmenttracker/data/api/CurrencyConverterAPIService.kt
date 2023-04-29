package com.example.investmenttracker.data.api

import com.example.investmenttracker.BuildConfig
import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyConverterAPIService {

    @GET(BuildConfig.CURRENCY_API_URL+"v6/{api_key}/latest/{base}")
    suspend fun convertCurrencies(
        @Path("api_key") apiKey: String = BuildConfig.CURRENCY_KEY,
        @Path("base") base: String = "USD"
    ): JsonObject

}