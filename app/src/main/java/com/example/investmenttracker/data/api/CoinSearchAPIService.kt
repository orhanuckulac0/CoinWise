package com.example.investmenttracker.data.api

import com.example.investmenttracker.BuildConfig
import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinSearchAPIService {

    @GET("/v2/cryptocurrency/quotes/latest")
    suspend fun getCoinBySlug(
        @Query("slug")
        slug: String,
        @Query("CMC_PRO_API_KEY")
        CMC_PRO_API_KEY: String = BuildConfig.API_KEY
    ): JsonObject

    @GET("/v2/cryptocurrency/quotes/latest")
    suspend fun getCoinBySymbol(
        @Query("symbol")
        slug: String,
        @Query("CMC_PRO_API_KEY")
        CMC_PRO_API_KEY: String = BuildConfig.API_KEY
    ): JsonObject

    @GET("/v2/cryptocurrency/quotes/latest")
    suspend fun getMultipleCoinsBySlug(
        @Query("slug")
        slugList: List<String>,
        @Query("CMC_PRO_API_KEY")
        CMC_PRO_API_KEY: String = BuildConfig.API_KEY
    ): JsonObject
}