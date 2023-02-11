package com.example.investmenttracker.data.repository.datasource_impl

import com.example.investmenttracker.data.api.CoinSearchAPIService
import com.example.investmenttracker.data.model.ApiResponse
import com.example.investmenttracker.data.repository.datasource.CoinRemoteDataSource
import retrofit2.Response

class CoinRemoteDataSourceImpl(
    private val coinSearchAPIService: CoinSearchAPIService,
): CoinRemoteDataSource {

    override suspend fun getCoin(name: String): Response<ApiResponse> {
        return coinSearchAPIService.getCoinInfo(name)
    }

}