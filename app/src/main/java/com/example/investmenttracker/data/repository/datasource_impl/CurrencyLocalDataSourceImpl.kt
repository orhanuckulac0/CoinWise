package com.example.investmenttracker.data.repository.datasource_impl

import com.example.investmenttracker.data.db.CurrencyDAO
import com.example.investmenttracker.data.model.CurrencyModel
import com.example.investmenttracker.data.repository.datasource.CurrencyLocalDataSource
import kotlinx.coroutines.flow.Flow

class CurrencyLocalDataSourceImpl(private val currencyDAO: CurrencyDAO): CurrencyLocalDataSource {

    override fun getUserCurrencyValue(currencyName: String): Flow<CurrencyModel> {
        return currencyDAO.getUserCurrencyValue(currencyName)
    }

    override suspend fun insertCurrencyData(data: CurrencyModel) {
        return currencyDAO.insertCurrencyData(data)
    }

}