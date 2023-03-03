package com.example.investmenttracker.data.repository

import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.data.repository.datasource.UserDataLocalDataSource
import com.example.investmenttracker.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow

class UserDataRepositoryImpl(private val userDataLocalDataSource: UserDataLocalDataSource): UserDataRepository {
    override suspend fun insertData(data: UserData) {
        return userDataLocalDataSource.insertData(data)
    }

    override suspend fun updateData(data: UserData) {
        return userDataLocalDataSource.updateData(data)
    }

    override fun getData(id: Int): Flow<UserData> {
        return userDataLocalDataSource.getData(id)
    }

    override suspend fun deleteData(data: UserData) {
        return userDataLocalDataSource.deleteData(data)
    }
}