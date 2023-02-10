package com.example.investmenttracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.investmenttracker.data.model.Coin

@Database(entities = [Coin::class], version = 1, exportSchema = false)
abstract class CoinDatabase: RoomDatabase() {
    abstract fun getCoinDAO(): CoinDAO
}