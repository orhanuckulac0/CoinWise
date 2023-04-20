package com.example.investmenttracker.domain.use_case.user

import com.example.investmenttracker.domain.repository.CurrencyRepository
import com.google.gson.JsonObject

class GetNewCurrencyValueUseCase(private val currencyRepository: CurrencyRepository) {

    suspend fun execute(
        from: String,
        to: String,
        amount:Double
    ): JsonObject = currencyRepository.convertCurrencies(
        from,
        to,
        amount
    )

}