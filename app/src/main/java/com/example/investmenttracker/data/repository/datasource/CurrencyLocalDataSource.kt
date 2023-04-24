package com.example.investmenttracker.data.repository.datasource

import com.example.investmenttracker.data.model.CurrencyModel
import kotlinx.coroutines.flow.Flow

interface CurrencyLocalDataSource {

    fun getUserCurrencyValue(currencyName: String): Flow<CurrencyModel>

    suspend fun insertCurrencyData(data: CurrencyModel)

}