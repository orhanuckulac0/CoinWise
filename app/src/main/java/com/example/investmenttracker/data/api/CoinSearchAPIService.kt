package com.example.investmenttracker.data.api

import com.example.investmenttracker.data.model.APIResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinSearchAPIService {

    @GET("https://api.coingecko.com/api/v3/simple/price")
    suspend fun getCoinInfo(
        @Query("ids")
        name: String,
        @Query("vs_currencies")
        currency: String
    ): Response<APIResponse>
}