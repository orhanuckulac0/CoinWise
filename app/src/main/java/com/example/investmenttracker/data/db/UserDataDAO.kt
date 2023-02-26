package com.example.investmenttracker.data.db

import androidx.room.*
import com.example.investmenttracker.data.model.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(data: UserData)

    @Query("SELECT * FROM user_data")
    fun getAllData(): Flow<List<UserData>>

    @Delete
    suspend fun deleteData(data: UserData)

}