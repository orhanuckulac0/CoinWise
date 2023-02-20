package com.example.investmenttracker.domain.use_case

import com.example.investmenttracker.domain.repository.CoinRepository
import com.google.gson.JsonObject
import retrofit2.Call

class GetCoinBySymbolUseCase(private val coinRepository: CoinRepository) {

    suspend fun execute(symbol: String): Call<JsonObject> {
        return coinRepository.getCoinBySymbol(symbol)
    }

}