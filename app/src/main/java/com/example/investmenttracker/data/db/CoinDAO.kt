package com.example.investmenttracker.data.db

import androidx.room.*
import com.example.investmenttracker.data.model.CoinModel

@Dao
interface CoinDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coin: CoinModel)

    @Query("SELECT * FROM coins")
    suspend fun getAllCoins(): List<CoinModel>

    @Delete
    suspend fun deleteCoin(coin: CoinModel)
}