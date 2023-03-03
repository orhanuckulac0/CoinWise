package com.example.investmenttracker.data.db

import androidx.room.*
import com.example.investmenttracker.data.model.UserData
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

@Dao
interface UserDataDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(data: UserData)
    @Update
    suspend fun updateData(data: UserData)

    @Query("SELECT * FROM user_data where id=:id")
    fun getData(id: Int): Flow<UserData>

    @Delete
    suspend fun deleteData(data: UserData)

}