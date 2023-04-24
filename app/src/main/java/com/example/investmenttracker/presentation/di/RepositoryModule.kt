package com.example.investmenttracker.presentation.di

import com.example.investmenttracker.data.repository.CoinRepositoryImpl
import com.example.investmenttracker.data.repository.CurrencyRepositoryImpl
import com.example.investmenttracker.data.repository.UserDataRepositoryImpl
import com.example.investmenttracker.data.repository.datasource.*
import com.example.investmenttracker.domain.repository.CoinRepository
import com.example.investmenttracker.domain.repository.CurrencyRepository
import com.example.investmenttracker.domain.repository.UserDataRepository
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
    fun provideCoinRepository(
        coinRemoteDataSource: CoinRemoteDataSource,
        coinLocalDataSource: CoinLocalDataSource
    ): CoinRepository{
        return CoinRepositoryImpl(coinRemoteDataSource, coinLocalDataSource)
    }

    @Singleton
    @Provides
    fun provideUserDataRepository(
        userDataLocalDataSource: UserDataLocalDataSource
    ): UserDataRepository {
        return UserDataRepositoryImpl(userDataLocalDataSource)
    }

    @Singleton
    @Provides
    fun funProvideCurrencyRepository(
        currencyLocalDataSource: CurrencyLocalDataSource,
        currencyRemoteDataSource: CurrencyRemoteDataSource
    ): CurrencyRepository {
        return CurrencyRepositoryImpl(currencyLocalDataSource, currencyRemoteDataSource)
    }
}