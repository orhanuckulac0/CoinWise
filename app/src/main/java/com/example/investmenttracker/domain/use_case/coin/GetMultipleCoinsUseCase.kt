package com.example.investmenttracker.domain.use_case.coin

import com.example.investmenttracker.domain.repository.CoinRepository

class GetMultipleCoinsUseCase(private val coinRepository: CoinRepository) {

    suspend fun execute(slugList: List<String>) = coinRepository.getMultipleCoinsBySlug(slugList)

}