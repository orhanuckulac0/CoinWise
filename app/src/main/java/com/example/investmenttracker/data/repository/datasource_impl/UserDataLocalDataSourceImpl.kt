package com.example.investmenttracker.data.repository.datasource_impl

import com.example.investmenttracker.data.db.UserDataDAO
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.data.repository.datasource.UserDataLocalDataSource
import kotlinx.coroutines.flow.Flow

class UserDataLocalDataSourceImpl(private val userDataDAO: UserDataDAO): UserDataLocalDataSource {
    override suspend fun insertData(data: UserData) {
        return userDataDAO.insertData(data)
    }

    override suspend fun updateData(data: UserData) {
        return userDataDAO.updateData(data)
    }

    override fun getData(id: Int): Flow<UserData> {
        return userDataDAO.getData(id)
    }

    override suspend fun deleteData(data: UserData) {
        return userDataDAO.deleteData(data)
    }
}