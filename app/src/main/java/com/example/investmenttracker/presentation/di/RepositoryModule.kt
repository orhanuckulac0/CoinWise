package com.example.investmenttracker.presentation.di

import com.example.investmenttracker.data.repository.CoinRepositoryImpl
import com.example.investmenttracker.data.repository.datasource.CoinRemoteDataSource
import com.example.investmenttracker.domain.repository.CoinRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideCoinRepository(coinRemoteDataSource: CoinRemoteDataSource): CoinRepository{
        return CoinRepositoryImpl(coinRemoteDataSource)
    }
}