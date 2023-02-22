package com.example.investmenttracker.domain.use_case

import com.example.investmenttracker.domain.repository.CoinRepository
import com.google.gson.JsonObject
import retrofit2.Call

class GetCoinBySlugUseCase(private val coinRepository: CoinRepository) {
    suspend fun execute(slug: String): JsonObject {
        return coinRepository.getCoinBySlug(slug)
    }
}