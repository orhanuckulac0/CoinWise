package com.example.investmenttracker.data.db

import androidx.room.*
import com.example.investmenttracker.data.model.CoinModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoin(coin: CoinModel)

    @Query(
        "UPDATE coins SET" +
                " totalTokenHeldAmount = :totalTokenHeldAmount," +
                " totalInvestmentAmount = :totalInvestmentAmount," +
                " totalInvestmentWorth = :totalInvestmentWorth" +
                " WHERE id = :id")
    suspend fun updateCoin(id: Int, totalTokenHeldAmount: Double ,totalInvestmentAmount: Double, totalInvestmentWorth: Double)

    @Query("SELECT * FROM coins")
    fun getAllCoins(): Flow<List<CoinModel>>

    @Query("SELECT * FROM coins where id=:id")
    fun getSingleCoinById(id: Int): Flow<CoinModel>

    @Delete
    suspend fun deleteCoin(coin: CoinModel)
}