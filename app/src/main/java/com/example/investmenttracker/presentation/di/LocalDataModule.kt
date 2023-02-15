package com.example.investmenttracker.presentation.di

import com.example.investmenttracker.data.db.CoinDAO
import com.example.investmenttracker.data.repository.datasource.CoinLocalDataSource
import com.example.investmenttracker.data.repository.datasource_impl.CoinLocalDataSourceImpl
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

}