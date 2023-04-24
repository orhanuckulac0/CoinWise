package com.example.investmenttracker.domain.use_case.currency

import com.example.investmenttracker.data.model.CurrencyModel
import com.example.investmenttracker.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow

class GetCurrencyValueFromDBUseCase(private val currencyRepository: CurrencyRepository) {

    fun execute(currencyName: String): Flow<CurrencyModel> {
        return currencyRepository.getCurrencyValues(currencyName)
    }
}