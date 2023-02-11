package com.example.investmenttracker.domain.use_case

import com.example.investmenttracker.data.model.ApiResponse
import com.example.investmenttracker.data.util.Resource
import com.example.investmenttracker.domain.repository.CoinRepository

class GetCoinUseCase(private val coinRepository: CoinRepository) {
    suspend fun execute(name: String): Resource<ApiResponse>{
        return coinRepository.getCoin(name)
    }
}