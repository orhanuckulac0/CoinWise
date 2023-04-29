package com.example.investmenttracker.data.repository

import com.example.investmenttracker.data.model.CurrencyModel
import com.example.investmenttracker.data.repository.datasource.CurrencyLocalDataSource
import com.example.investmenttracker.data.repository.datasource.CurrencyRemoteDataSource
import com.example.investmenttracker.domain.repository.CurrencyRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class CurrencyRepositoryImpl(
    private val currencyLocalDataSource: CurrencyLocalDataSource,
    private val currencyRemoteDataSource: CurrencyRemoteDataSource): CurrencyRepository {

    override suspend fun convertCurrencies(): JsonObject {
        return currencyRemoteDataSource.convertCurrencies()
    }

    override fun getCurrencyValues(currencyName: String): Flow<CurrencyModel> {
        return currencyLocalDataSource.getUserCurrencyValue(currencyName)
    }

    override fun getAllCurrencies(): Flow<List<CurrencyModel>> {
        return currencyLocalDataSource.getAllCurrencies()
    }

    override suspend fun insertCurrencyData(data: CurrencyModel) {
        return currencyLocalDataSource.insertCurrencyData(data)
    }
}