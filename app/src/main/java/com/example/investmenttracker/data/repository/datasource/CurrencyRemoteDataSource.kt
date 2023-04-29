package com.example.investmenttracker.data.repository.datasource

import com.google.gson.JsonObject

interface CurrencyRemoteDataSource {
    suspend fun convertCurrencies(): JsonObject
}