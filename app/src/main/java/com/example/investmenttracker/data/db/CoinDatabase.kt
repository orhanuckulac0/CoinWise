package com.example.investmenttracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.investmenttracker.data.model.CoinModel

@Database(entities = [CoinModel::class], version = 1, exportSchema = false)
abstract class CoinDatabase: RoomDatabase() {
    abstract fun getCoinDAO(): CoinDAO
}