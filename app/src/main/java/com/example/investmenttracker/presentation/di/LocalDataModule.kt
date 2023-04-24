package com.example.investmenttracker.presentation.di

import com.example.investmenttracker.data.db.CoinDAO
import com.example.investmenttracker.data.db.CurrencyDAO
import com.example.investmenttracker.data.db.UserDataDAO
import com.example.investmenttracker.data.repository.datasource.CoinLocalDataSource
import com.example.investmenttracker.data.repository.datasource.CurrencyLocalDataSource
import com.example.investmenttracker.data.repository.datasource.UserDataLocalDataSource
import com.example.investmenttracker.data.repository.datasource_impl.CoinLocalDataSourceImpl
import com.example.investmenttracker.data.repository.datasource_impl.CurrencyLocalDataSourceImpl
import com.example.investmenttracker.data.repository.datasource_impl.UserDataLocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalDataModule {

    @Singleton
    @Provides
    fun provideCoinLocalDataSource(coinDAO: CoinDAO): CoinLocalDataSource {
        return CoinLocalDataSourceImpl(coinDAO)
    }

    @Singleton
    @Provides
    fun provideUserDataLocalDataSource(userDataDAO: UserDataDAO): UserDataLocalDataSource {
        return UserDataLocalDataSourceImpl(userDataDAO)
    }

    @Singleton
    @Provides
    fun provideCurrencyLocalDataSource(currencyDAO: CurrencyDAO): CurrencyLocalDataSource {
        return CurrencyLocalDataSourceImpl(currencyDAO)
    }

}