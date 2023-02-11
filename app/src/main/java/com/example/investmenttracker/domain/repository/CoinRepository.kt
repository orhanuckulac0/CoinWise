package com.example.investmenttracker.domain.repository

import com.example.investmenttracker.data.model.ApiResponse
import com.example.investmenttracker.data.util.Resource

interface CoinRepository {
    suspend fun getCoin(name: String): Resource<ApiResponse>
}