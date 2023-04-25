package com.example.investmenttracker.presentation.di

import android.app.Application
import com.example.investmenttracker.domain.use_case.*
import com.example.investmenttracker.domain.use_case.coin.*
import com.example.investmenttracker.domain.use_case.currency.AddCurrencyDataToDBUseCase
import com.example.investmenttracker.domain.use_case.currency.GetAllCurrenciesUseCase
import com.example.investmenttracker.domain.use_case.currency.GetCurrencyValueFromDBUseCase
import com.example.investmenttracker.domain.use_case.currency.GetNewCurrencyValueUseCase
import com.example.investmenttracker.domain.use_case.user.*
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
    fun provideMainViewModelFactory(
        app: Application,
        getAllCoinsUseCase: GetAllCoinsUseCase,
        insertUserDataUseCase: InsertUserDataUseCase,
        getUserDataUseCase: GetUserDataUseCase,
        updateUserDataUseCase: UpdateUserDataUseCase,
        getMultipleCoinsUseCase: GetMultipleCoinsUseCase,
        updateCoinDetailsUseCase: UpdateCoinDetailsUseCase,
        getNewCurrencyValueUseCase: GetNewCurrencyValueUseCase,
        addCurrencyDataToDBUseCase: AddCurrencyDataToDBUseCase,
        getCurrencyValueFromDBUseCase: GetCurrencyValueFromDBUseCase
    ): MainViewModelFactory {
        return MainViewModelFactory(
            app,
            getAllCoinsUseCase,
            insertUserDataUseCase,
            getUserDataUseCase,
            updateUserDataUseCase,
            getMultipleCoinsUseCase,
            updateCoinDetailsUseCase,
            getNewCurrencyValueUseCase,
            addCurrencyDataToDBUseCase,
            getCurrencyValueFromDBUseCase
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
        getAllCoinsUseCase: GetAllCoinsUseCase,
        getAllCurrenciesUseCase: GetAllCurrenciesUseCase
    ): AnalyticsViewModelFactory {
        return AnalyticsViewModelFactory(
            app,
            getUserDataUseCase,
            getAllCoinsUseCase,
            getAllCurrenciesUseCase
        )
    }

    @Singleton
    @Provides
    fun provideSettingsViewModelFactory(
        app: Application,
        changeAppThemeUseCase: ChangeAppThemeUseCase,
        updateUserDataUseCase: UpdateUserDataUseCase
    ): SettingsViewModelFactory {
        return SettingsViewModelFactory(
            app,
            changeAppThemeUseCase,
            updateUserDataUseCase
        )
    }

}