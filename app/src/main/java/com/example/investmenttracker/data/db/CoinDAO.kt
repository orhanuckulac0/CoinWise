package com.example.investmenttracker.data.db

import androidx.room.*
import com.example.investmenttracker.data.model.CoinModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoin(coin: CoinModel)

    @Query("SELECT * FROM coins")
    fun getAllCoins(): Flow<List<CoinModel>>

    @Delete
    suspend fun deleteCoin(coin: CoinModel)
}