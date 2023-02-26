package com.example.investmenttracker.data.repository

import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.data.repository.datasource.UserDataLocalDataSource
import com.example.investmenttracker.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow

class UserDataRepositoryImpl(private val userDataLocalDataSource: UserDataLocalDataSource): UserDataRepository {
    override suspend fun insertData(data: UserData) {
        return userDataLocalDataSource.insertData(data)
    }

    override fun getAllData(): Flow<List<UserData>> {
        return userDataLocalDataSource.getAllData()
    }

    override suspend fun deleteData(data: UserData) {
        return userDataLocalDataSource.deleteData(data)
    }
}