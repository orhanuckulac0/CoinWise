package com.example.investmenttracker.data.repository.datasource

import com.example.investmenttracker.data.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataLocalDataSource {

    suspend fun insertData(data: UserData)

    suspend fun updateData(data: UserData)

    fun getData(id: Int): Flow<UserData>

    suspend fun deleteData(data: UserData)

}