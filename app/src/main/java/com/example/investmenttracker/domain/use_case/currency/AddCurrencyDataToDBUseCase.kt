package com.example.investmenttracker.domain.use_case.currency

import com.example.investmenttracker.data.model.CurrencyModel
import com.example.investmenttracker.domain.repository.CurrencyRepository

class AddCurrencyDataToDBUseCase(private val currencyRepository: CurrencyRepository) {

    suspend fun execute(data: CurrencyModel){
        currencyRepository.insertCurrencyData(data)
    }

}