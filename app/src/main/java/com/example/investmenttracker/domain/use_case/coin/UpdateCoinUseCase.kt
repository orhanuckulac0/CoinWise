package com.example.investmenttracker.domain.use_case.coin

import com.example.investmenttracker.domain.repository.CoinRepository

class UpdateCoinUseCase(private val coinsRepository: CoinRepository) {

    suspend fun execute(
        id: Int,
        totalTokenHeldAmount: Double,
        totalInvestmentAmount: Double,
        totalInvestmentWorth: Double
    ) = coinsRepository.updateCoin(id, totalTokenHeldAmount, totalInvestmentAmount, totalInvestmentWorth)

}