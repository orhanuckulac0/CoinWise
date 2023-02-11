package com.example.investmenttracker.data.repository.datasource

import com.example.investmenttracker.data.model.ApiResponse
import retrofit2.Response

interface CoinRemoteDataSource {

    suspend fun getCoin(name: String): Response<ApiResponse>
    
}