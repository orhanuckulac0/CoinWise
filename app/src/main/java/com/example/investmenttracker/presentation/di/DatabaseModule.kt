package com.example.investmenttracker.presentation.di

import android.app.Application
import androidx.room.Room
import com.example.investmenttracker.data.db.UserDataDAO
import com.example.investmenttracker.data.db.CoinDAO
import com.example.investmenttracker.data.db.AppDatabase
import com.example.investmenttracker.data.db.CurrencyDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideCoinDatabase(app: Application): AppDatabase{
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "app_db")
            .fallbackToDestructiveMigration()
            .build()
    }
    @Singleton
    @Provides
    fun provideCoinDAO(appDatabase: AppDatabase): CoinDAO{
        return appDatabase.getCoinDAO()
    }

    @Singleton
    @Provides
    fun provideUserDataDAO(appDatabase: AppDatabase): UserDataDAO {
        return appDatabase.getUserDataDAO()
    }

    @Singleton
    @Provides
    fun provideCurrencyDAO(appDatabase: AppDatabase): CurrencyDAO {
        return appDatabase.getCurrencyDAO()
    }

}