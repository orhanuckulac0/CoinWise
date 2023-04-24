package com.example.investmenttracker.domain.repository

import com.example.investmenttracker.data.model.CurrencyModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    suspend fun convertCurrencies(base: String): JsonObject
    fun getCurrencyValues(currencyName: String): Flow<CurrencyModel>
    suspend fun insertCurrencyData(data: CurrencyModel)

}