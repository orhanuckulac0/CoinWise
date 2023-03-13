package com.example.investmenttracker.presentation.di

import android.app.Application
import com.example.investmenttracker.domain.use_case.*
import com.example.investmenttracker.domain.use_case.coin.*
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.InsertUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.UpdateUserDataUseCase
import com.example.investmenttracker.presentation.view_model.MainViewModelFactory
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
        getUserDataUseCase: GetUserDataUseCase,
        updateUserDataUseCase: UpdateUserDataUseCase,
        getMultipleCoinsUseCase: GetMultipleCoinsUseCase,
        updateCoinDetailsUseCase: UpdateCoinDetailsUseCase,
    ): MainViewModelFactory {
        return MainViewModelFactory(
            app,
            getAllCoinsUseCase,
            insertUserDataUseCase,
            getUserDataUseCase,
            updateUserDataUseCase,
            getMultipleCoinsUseCase,
            updateCoinDetailsUseCase,
        )
    }

    @Singleton
    @Provides
    fun provideSearchCoinViewModelFactory(
        app: Application,
        getCoinBySlugUseCase: GetCoinBySlugUseCase,
        getCoinBySymbolUseCase: GetCoinBySymbolUseCase,
        saveCoinUseCase: SaveCoinUseCase,
        getAllCoinsUseCase: GetAllCoinsUseCase
    ): SearchCoinViewModelFactory {
        return SearchCoinViewModelFactory(
            app,
            getCoinBySlugUseCase,
            getCoinBySymbolUseCase,
            saveCoinUseCase,
            getAllCoinsUseCase
        )
    }

    @Singleton
    @Provides
    fun provideTokenDetailsViewModelFactory(
        app: Application,
        updateInvestmentUseCase: UpdateInvestmentUseCase,
        deleteCoinUseCase: DeleteCoinUseCase
    ): TokenDetailsViewModelFactory {
        return TokenDetailsViewModelFactory(
            app,
            updateInvestmentUseCase,
            deleteCoinUseCase
        )
    }
}