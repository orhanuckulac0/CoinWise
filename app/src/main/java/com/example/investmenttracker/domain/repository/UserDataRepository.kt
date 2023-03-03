package com.example.investmenttracker.domain.repository

import com.example.investmenttracker.data.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    suspend fun insertData(data: UserData)
    suspend fun updateData(data: UserData)

    fun getData(id: Int): Flow<UserData>

    suspend fun deleteData(data: UserData)

}