package com.example.investmenttracker.data.api

import com.example.investmenttracker.BuildConfig
import com.example.investmenttracker.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinSearchAPIService {

    @GET("/v2/cryptocurrency/quotes/latest")
    suspend fun getCoinInfo(
        @Query("slug")
        slug: String,
        @Query("CMC_PRO_API_KEY")
        CMC_PRO_API_KEY: String = BuildConfig.API_KEY
    ): Response<ApiResponse>
}