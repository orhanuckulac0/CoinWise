package com.example.investmenttracker.data.repository.datasource

import com.example.investmenttracker.data.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataLocalDataSource {

    suspend fun insertData(data: UserData)

    fun getAllData(): Flow<List<UserData>>

    suspend fun deleteData(data: UserData)

}