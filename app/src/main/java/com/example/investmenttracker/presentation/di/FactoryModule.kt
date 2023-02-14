package com.example.investmenttracker.presentation.di

import android.app.Application
import com.example.investmenttracker.domain.use_case.GetCoinBySlugUseCase
import com.example.investmenttracker.presentation.view_model.CoinViewModelFactory
import com.example.investmenttracker.presentation.view_model.SearchCoinViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FactoryModule {

    @Singleton
    @Provides
    fun provideCoinViewModelFactory(
        app: Application,
    ): CoinViewModelFactory {
        return CoinViewModelFactory(app)
    }

    @Singleton
    @Provides
    fun provideSearchCoinViewModelFactory(
        app: Application,
        getCoinBySlugUseCase: GetCoinBySlugUseCase
    ): SearchCoinViewModelFactory {
        return SearchCoinViewModelFactory(app, getCoinBySlugUseCase)
    }
}