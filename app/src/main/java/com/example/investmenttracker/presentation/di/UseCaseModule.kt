package com.example.investmenttracker.presentation.di

import com.example.investmenttracker.domain.repository.CoinRepository
import com.example.investmenttracker.domain.use_case.DeleteCoinUseCase
import com.example.investmenttracker.domain.use_case.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.GetCoinBySlugUseCase
import com.example.investmenttracker.domain.use_case.SaveCoinUseCase
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
    fun provideGetCoinBySlugUseCase(coinRepository: CoinRepository): GetCoinBySlugUseCase{
        return GetCoinBySlugUseCase(coinRepository)
    }

    @Singleton
    @Provides
    fun provideSaveCoinUseCase(coinRepository: CoinRepository): SaveCoinUseCase{
        return SaveCoinUseCase(coinRepository)
    }

    @Singleton
    @Provides
    fun provideDeleteCoinUseCase(coinRepository: CoinRepository): DeleteCoinUseCase{
        return DeleteCoinUseCase(coinRepository)
    }

    @Singleton
    @Provides
    fun provideGetAllCoinsUseCase(coinRepository: CoinRepository): GetAllCoinsUseCase{
        return GetAllCoinsUseCase(coinRepository)
    }

}