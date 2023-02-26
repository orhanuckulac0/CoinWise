package com.example.investmenttracker.presentation.di

import android.app.Application
import com.example.investmenttracker.domain.use_case.*
import com.example.investmenttracker.domain.use_case.coin.*
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.InsertUserDataUseCase
import com.example.investmenttracker.presentation.view_model.CoinViewModelFactory
import com.example.investmenttracker.presentation.view_model.SearchCoinViewModelFactory
import com.example.investmenttracker.presentation.view_model.TokenDetailsViewModelFactory
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
        getAllCoinsUseCase: GetAllCoinsUseCase,
        insertUserDataUseCase: InsertUserDataUseCase,
        getUserDataUseCase: GetUserDataUseCase
    ): CoinViewModelFactory {
        return CoinViewModelFactory(
            app,
            getAllCoinsUseCase,
            insertUserDataUseCase,
            getUserDataUseCase
        )
    }

    @Singleton
    @Provides
    fun provideSearchCoinViewModelFactory(
        app: Application,
        getCoinBySlugUseCase: GetCoinBySlugUseCase,
        getCoinBySymbolUseCase: GetCoinBySymbolUseCase,
        saveCoinUseCase: SaveCoinUseCase
    ): SearchCoinViewModelFactory {
        return SearchCoinViewModelFactory(
            app,
            getCoinBySlugUseCase,
            getCoinBySymbolUseCase,
            saveCoinUseCase,
        )
    }

    @Singleton
    @Provides
    fun provideTokenDetailsViewModelFactory(
        app: Application,
        updateCoinUseCase: UpdateCoinUseCase,
        deleteCoinUseCase: DeleteCoinUseCase
    ): TokenDetailsViewModelFactory {
        return TokenDetailsViewModelFactory(
            app,
            updateCoinUseCase,
            deleteCoinUseCase
        )
    }
}