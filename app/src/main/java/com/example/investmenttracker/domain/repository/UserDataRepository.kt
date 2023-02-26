package com.example.investmenttracker.domain.repository

import com.example.investmenttracker.data.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    suspend fun insertData(data: UserData)

    fun getAllData(): Flow<List<UserData>>

    suspend fun deleteData(data: UserData)

}