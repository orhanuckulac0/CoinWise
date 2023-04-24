package com.example.investmenttracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.investmenttracker.data.model.CurrencyModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyData(data: CurrencyModel)

    @Query("SELECT * FROM currency_table where currencyName=:currencyName ")
    fun getUserCurrencyValue(currencyName: String): Flow<CurrencyModel>

}