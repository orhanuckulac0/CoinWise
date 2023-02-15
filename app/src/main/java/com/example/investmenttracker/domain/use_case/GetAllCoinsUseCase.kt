package com.example.investmenttracker.domain.use_case

import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow

class GetAllCoinsUseCase(private val coinRepository: CoinRepository) {

    suspend fun execute(): Flow<List<CoinModel>> = coinRepository.getAllCoinsFromDB()

}