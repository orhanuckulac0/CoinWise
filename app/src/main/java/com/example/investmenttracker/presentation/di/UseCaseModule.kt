package com.example.investmenttracker.presentation.di

import com.example.investmenttracker.domain.repository.CoinRepository
import com.example.investmenttracker.domain.use_case.GetCoinUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    @Singleton
    @Provides
    fun provideGetCoinUseCase(coinRepository: CoinRepository): GetCoinUseCase{
        return GetCoinUseCase(coinRepository)
    }
}