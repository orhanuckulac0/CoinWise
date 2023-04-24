package com.example.investmenttracker.presentation.di

import com.example.investmenttracker.domain.repository.CoinRepository
import com.example.investmenttracker.domain.repository.CurrencyRepository
import com.example.investmenttracker.domain.repository.UserDataRepository
import com.example.investmenttracker.domain.use_case.*
import com.example.investmenttracker.domain.use_case.coin.*
import com.example.investmenttracker.domain.use_case.currency.AddCurrencyDataToDBUseCase
import com.example.investmenttracker.domain.use_case.currency.GetCurrencyValueFromDBUseCase
import com.example.investmenttracker.domain.use_case.currency.GetNewCurrencyValueUseCase
import com.example.investmenttracker.domain.use_case.user.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    // Coin Related UseCases
    @Singleton
    @Provides
    fun provideGetCoinBySlugUseCase(coinRepository: CoinRepository): GetCoinBySlugUseCase {
        return GetCoinBySlugUseCase(coinRepository)
    }

    @Singleton
    @Provides
    fun provideSaveCoinUseCase(coinRepository: CoinRepository): SaveCoinUseCase {
        return SaveCoinUseCase(coinRepository)
    }

    @Singleton
    @Provides
    fun provideDeleteCoinUseCase(coinRepository: CoinRepository): DeleteCoinUseCase {
        return DeleteCoinUseCase(coinRepository)
    }

    @Singleton
    @Provides
    fun provideGetAllCoinsUseCase(coinRepository: CoinRepository): GetAllCoinsUseCase {
        return GetAllCoinsUseCase(coinRepository)
    }

    @Singleton
    @Provides
    fun provideGetCoinBySymbolUseCase(coinRepository: CoinRepository): GetCoinBySymbolUseCase {
        return GetCoinBySymbolUseCase(coinRepository)
    }

    @Singleton
    @Provides
    fun provideGetSingleCoinById(coinRepository: CoinRepository): GetSingleCoinByIdUseCase {
        return GetSingleCoinByIdUseCase(coinRepository)
    }

    @Singleton
    @Provides
    fun provideUpdateInvestment(coinRepository: CoinRepository): UpdateInvestmentUseCase {
        return UpdateInvestmentUseCase(coinRepository)
    }

    @Singleton
    @Provides
    fun provideUpdateCoinDetails(coinRepository: CoinRepository): UpdateCoinDetailsUseCase {
        return UpdateCoinDetailsUseCase(coinRepository)
    }

    @Singleton
    @Provides
    fun provideGetMultipleCoinsUseCase(coinRepository: CoinRepository): GetMultipleCoinsUseCase {
        return GetMultipleCoinsUseCase(coinRepository)
    }


    // User Related UseCases
    @Singleton
    @Provides
    fun provideInsertUserData(userDataRepository: UserDataRepository): InsertUserDataUseCase {
        return InsertUserDataUseCase(userDataRepository)
    }
    @Singleton
    @Provides
    fun provideDeleteUserData(userDataRepository: UserDataRepository): DeleteUserDataUseCase {
        return DeleteUserDataUseCase(userDataRepository)
    }
    @Singleton
    @Provides
    fun provideGetUserData(userDataRepository: UserDataRepository): GetUserDataUseCase {
        return GetUserDataUseCase(userDataRepository)
    }

    @Singleton
    @Provides
    fun provideUpdateUserdata(userDataRepository: UserDataRepository): UpdateUserDataUseCase {
        return UpdateUserDataUseCase(userDataRepository)
    }

    @Singleton
    @Provides
    fun provideChangeAppThemeUserCase(): ChangeAppThemeUseCase {
        return ChangeAppThemeUseCase()
    }

    // currency
    @Singleton
    @Provides
    fun provideGetNewCurrencyValueUseCase(currencyRepository: CurrencyRepository): GetNewCurrencyValueUseCase {
        return GetNewCurrencyValueUseCase(currencyRepository)
    }

    @Singleton
    @Provides
    fun provideGetCurrencyValuesFromDBUseCase(currencyRepository: CurrencyRepository): GetCurrencyValueFromDBUseCase {
        return GetCurrencyValueFromDBUseCase(currencyRepository)
    }
    @Singleton
    @Provides
    fun provideAddCurrencyDataToDBUseCase(currencyRepository: CurrencyRepository): AddCurrencyDataToDBUseCase {
        return AddCurrencyDataToDBUseCase(currencyRepository)
    }

}