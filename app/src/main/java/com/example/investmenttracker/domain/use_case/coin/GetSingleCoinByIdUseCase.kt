package com.example.investmenttracker.domain.use_case.coin

import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow

class GetSingleCoinByIdUseCase(
    private val coinRepository: CoinRepository
    ) {

    fun execute(id: Int): Flow<CoinModel> = coinRepository.getSingleCoinById(id)

}