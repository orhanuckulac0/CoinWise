package com.example.investmenttracker.domain.use_case.user

import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.domain.repository.UserDataRepository

class UpdateUserDataUseCase(private val userDataRepository: UserDataRepository) {

    suspend fun execute(data: UserData) = userDataRepository.updateData(data)

}
