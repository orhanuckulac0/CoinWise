package com.example.investmenttracker.data.db

import androidx.room.*
import com.example.investmenttracker.data.model.Coin
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coin: Coin)

    @Query("SELECT * FROM coins")
    suspend fun getAllCoins(): List<Coin>

    @Delete
    suspend fun deleteCoin(coin: Coin)
}