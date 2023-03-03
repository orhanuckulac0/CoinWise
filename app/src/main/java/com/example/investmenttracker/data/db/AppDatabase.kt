package com.example.investmenttracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.data.model.CoinModel

@Database(entities = [CoinModel::class, UserData::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getCoinDAO(): CoinDAO
    abstract fun getUserDataDAO(): UserDataDAO
}