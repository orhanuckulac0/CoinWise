package com.example.investmenttracker.domain.repository

import com.example.investmenttracker.data.model.CurrencyModel
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    suspend fun convertCurrencies(): JsonObject
    fun getCurrencyValues(currencyName: String): Flow<CurrencyModel>
    fun getAllCurrencies(): Flow<List<CurrencyModel>>
    suspend fun insertCurrencyData(data: CurrencyModel)

}