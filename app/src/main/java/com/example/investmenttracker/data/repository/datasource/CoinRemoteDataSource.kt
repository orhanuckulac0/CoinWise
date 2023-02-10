package com.example.investmenttracker.data.repository.datasource

import com.example.investmenttracker.data.model.APIResponse
import retrofit2.Response

interface CoinRemoteDataSource {

    suspend fun getCoin(name: String, currency: String): Response<APIResponse>
    
}