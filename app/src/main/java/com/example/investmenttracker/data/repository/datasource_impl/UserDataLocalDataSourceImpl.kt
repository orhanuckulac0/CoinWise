package com.example.investmenttracker.data.repository.datasource_impl

import com.example.investmenttracker.data.db.UserDataDAO
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.data.repository.datasource.UserDataLocalDataSource
import kotlinx.coroutines.flow.Flow

class UserDataLocalDataSourceImpl(private val userDataDAO: UserDataDAO): UserDataLocalDataSource {
    override suspend fun insertData(data: UserData) {
        return userDataDAO.insertData(data)
    }

    override fun getAllData(): Flow<List<UserData>> {
        return userDataDAO.getAllData()
    }

    override suspend fun deleteData(data: UserData) {
        return userDataDAO.deleteData(data)
    }
}