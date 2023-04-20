package com.example.investmenttracker.presentation.di

import com.example.investmenttracker.data.api.CoinSearchAPIService
import com.example.investmenttracker.data.api.CurrencyConverterAPIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create())
            .baseUrl(com.example.investmenttracker.BuildConfig.BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun provideCoinSearchAPIService(retrofit: Retrofit): CoinSearchAPIService{
        return retrofit.create(CoinSearchAPIService::class.java)
    }

    @Singleton
    @Provides
    fun provideCurrencyConverterAPIService(retrofit: Retrofit): CurrencyConverterAPIService{
        return retrofit.create(CurrencyConverterAPIService::class.java)
    }
}