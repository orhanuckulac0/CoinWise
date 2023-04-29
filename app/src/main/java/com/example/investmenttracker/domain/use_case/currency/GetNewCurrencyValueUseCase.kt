package com.example.investmenttracker.domain.use_case.currency

import com.example.investmenttracker.domain.repository.CurrencyRepository
import com.google.gson.JsonObject

class GetNewCurrencyValueUseCase(private val currencyRepository: CurrencyRepository) {

    suspend fun execute(): JsonObject = currencyRepository.convertCurrencies()

}