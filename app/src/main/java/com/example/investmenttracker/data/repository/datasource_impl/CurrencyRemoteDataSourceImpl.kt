package com.example.investmenttracker.data.repository.datasource_impl

import com.example.investmenttracker.data.api.CurrencyConverterAPIService
import com.example.investmenttracker.data.repository.datasource.CurrencyRemoteDataSource
import com.google.gson.JsonObject

class CurrencyRemoteDataSourceImpl(
    private val currencyConverterAPIService: CurrencyConverterAPIService
    ): CurrencyRemoteDataSource {


    override suspend fun convertCurrencies(from: String, to: String, amount: Double): JsonObject {
        return currencyConverterAPIService.convertCurrencies(
            from = from,
            to = to,
            amount = amount
        )
    }
}