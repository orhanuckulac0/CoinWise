package com.example.investmenttracker.presentation.di

import com.example.investmenttracker.data.api.CoinSearchAPIService
import com.example.investmenttracker.data.repository.datasource.CoinRemoteDataSource
import com.example.investmenttracker.data.repository.datasource_impl.CoinRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteDataModule {

    @Singleton
    @Provides
    fun provideCoinRemoteDataSource(coinSearchAPIService: CoinSearchAPIService): CoinRemoteDataSource{
        return CoinRemoteDataSourceImpl(coinSearchAPIService)
    }
}