package com.example.investmenttracker.domain.use_case.coin

import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.domain.repository.CoinRepository

class SaveCoinUseCase(private val coinsRepository: CoinRepository) {

    suspend fun execute(coinModel: CoinModel) = coinsRepository.insertCoinToDB(coinModel)

}