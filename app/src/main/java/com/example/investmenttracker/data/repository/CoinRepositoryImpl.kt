package com.example.investmenttracker.data.repository

import com.example.investmenttracker.data.model.APIResponse
import com.example.investmenttracker.data.repository.datasource.CoinRemoteDataSource
import com.example.investmenttracker.data.util.Resource
import com.example.investmenttracker.domain.repository.CoinRepository
import retrofit2.Response

class CoinRepositoryImpl(
    private val coinRemoteDataSource: CoinRemoteDataSource
): CoinRepository {

    private fun responseToResource(response: Response<APIResponse>) : Resource<APIResponse>{
        if (response.isSuccessful){
            response.body()?.let {
                    result->
                return Resource.Success(result)
            }
        }
        return Resource.Error(response.message())
    }

    override suspend fun getCoin(name: String, currency: String): Resource<APIResponse> {
        return responseToResource(coinRemoteDataSource.getCoin(name, currency))
    }

}