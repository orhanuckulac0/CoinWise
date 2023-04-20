package com.example.investmenttracker.domain.repository

import com.google.gson.JsonObject

interface CurrencyRepository {

    suspend fun convertCurrencies(from: String, to: String, amount: Double): JsonObject
}