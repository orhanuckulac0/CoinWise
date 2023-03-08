package com.example.investmenttracker.domain.use_case.coin

import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.domain.repository.CoinRepository

class UpdateCoinDetailsUseCase(private val coinRepository: CoinRepository) {

    suspend fun execute(coins: List<CoinModel>) = coinRepository.updateCoinDetails(coins)

}