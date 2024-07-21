package com.example.investmenttracker.presentation.di

import com.example.investmenttracker.domain.repository.CoinRepository
import com.example.investmenttracker.domain.repository.CurrencyRepository
import com.example.investmenttracker.domain.repository.UserDataRepository
import com.example.investmenttracker.domain.use_case.coin.DeleteCoinUseCase
import com.example.investmenttracker.domain.use_case.coin.GetAllCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.GetCoinBySlugUseCase
import com.example.investmenttracker.domain.use_case.coin.GetCoinBySymbolUseCase
import com.example.investmenttracker.domain.use_case.coin.GetMultipleCoinsUseCase
import com.example.investmenttracker.domain.use_case.coin.GetSingleCoinByIdUseCase
import com.example.investmenttracker.domain.use_case.coin.SaveCoinUseCase
import com.example.investmenttracker.domain.use_case.coin.UpdateCoinDetailsUseCase
import com.example.investmenttracker.domain.use_case.coin.UpdateInvestmentUseCase
import com.example.investmenttracker.domain.use_case.currency.AddCurrencyDataToDBUseCase
import com.example.investmenttracker.domain.use_case.currency.GetAllCurrenciesUseCase
import com.example.investmenttracker.domain.use_case.currency.GetCurrencyValueFromDBUseCase
import com.example.investmenttracker.domain.use_case.currency.GetNewCurrencyValueUseCase
import com.example.investmenttracker.domain.use_case.user.ChangeAppThemeUseCase
import com.example.investmenttracker.domain.use_case.user.DeleteUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.GetUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.InsertUserDataUseCase
import com.example.investmenttracker.domain.use_case.user.UpdateUserDataUseCase
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

    @Singleton
    @Provides
    fun provideGetAllCurrenciesUseCase(currencyRepository: CurrencyRepository): GetAllCurrenciesUseCase {
        return GetAllCurrenciesUseCase(currencyRepository)
    }

}