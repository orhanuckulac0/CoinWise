package com.example.investmenttracker.presentation.di

import android.app.Application
import com.example.investmenttracker.domain.use_case.*
import com.example.investmenttracker.domain.use_case.coin.*
import com.example.investmenttracker.domain.use_case.user.ChangeAppThemeUseCase
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.InsertUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.UpdateUserDataUseCase
import com.example.investmenttracker.presentation.view_model_factory.*
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
        getAllCoinsUseCase: GetAllCoinsUseCase,
        getUserDataUseCase: GetUserDataUseCase,
        updateUserDataUseCase: UpdateUserDataUseCase
    ): SearchCoinViewModelFactory {
        return SearchCoinViewModelFactory(
            app,
            getCoinBySlugUseCase,
            getCoinBySymbolUseCase,
            saveCoinUseCase,
            getAllCoinsUseCase,
            getUserDataUseCase,
            updateUserDataUseCase
        )
    }

    @Singleton
    @Provides
    fun provideTokenDetailsViewModelFactory(
        app: Application,
        updateInvestmentUseCase: UpdateInvestmentUseCase,
        deleteCoinUseCase: DeleteCoinUseCase,
        getUserDataUseCase: GetUserDataUseCase,
        updateUserDataUseCase: UpdateUserDataUseCase
    ): TokenDetailsViewModelFactory {
        return TokenDetailsViewModelFactory(
            app,
            updateInvestmentUseCase,
            deleteCoinUseCase,
            getUserDataUseCase,
            updateUserDataUseCase
        )
    }

    @Singleton
    @Provides
    fun provideAnalyticsViewModelFactory(
        app: Application,
        getUserDataUseCase: GetUserDataUseCase,
        getAllCoinsUseCase: GetAllCoinsUseCase
    ): AnalyticsViewModelFactory {
        return AnalyticsViewModelFactory(
            app,
            getUserDataUseCase,
            getAllCoinsUseCase
        )
    }

    @Singleton
    @Provides
    fun provideSettingsViewModelFactory(
        app: Application,
        changeAppThemeUseCase: ChangeAppThemeUseCase
    ): SettingsViewModelFactory {
        return SettingsViewModelFactory(
            app,
            changeAppThemeUseCase
        )
    }

}