package com.example.investmenttracker.presentation.di

import android.app.Application
import androidx.room.Room
import com.example.investmenttracker.data.db.CoinDAO
import com.example.investmenttracker.data.db.CoinDatabase
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
    fun provideNewsDatabase(app: Application): CoinDatabase{
        return Room.databaseBuilder(
            app,
            CoinDatabase::class.java,
            "coins_db")
            .fallbackToDestructiveMigration()
            .build()
    }
    @Singleton
    @Provides
    fun provideArticleDAO(coinDatabase: CoinDatabase): CoinDAO{
        return coinDatabase.getCoinDAO()
    }

}