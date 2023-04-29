package com.example.investmenttracker.data.repository.datasource_impl

import com.example.investmenttracker.data.api.CurrencyConverterAPIService
import com.example.investmenttracker.data.repository.datasource.CurrencyRemoteDataSource
import com.google.gson.JsonObject

class CurrencyRemoteDataSourceImpl(
    private val currencyConverterAPIService: CurrencyConverterAPIService
    ): CurrencyRemoteDataSource {

    override suspend fun convertCurrencies(): JsonObject {
        return currencyConverterAPIService.convertCurrencies()
    }
}