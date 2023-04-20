package com.example.investmenttracker.data.repository

import com.example.investmenttracker.data.repository.datasource.CurrencyRemoteDataSource
import com.example.investmenttracker.domain.repository.CurrencyRepository
import com.google.gson.JsonObject

class CurrencyRepositoryImpl(private val currencyRemoteDataSource: CurrencyRemoteDataSource): CurrencyRepository {

    override suspend fun convertCurrencies(from: String, to: String, amount: Double): JsonObject {
        return currencyRemoteDataSource.convertCurrencies(from, to, amount)
    }
}