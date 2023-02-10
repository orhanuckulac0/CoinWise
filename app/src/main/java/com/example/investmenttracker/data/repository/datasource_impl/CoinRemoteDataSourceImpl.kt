package com.example.investmenttracker.data.repository.datasource_impl

import com.example.investmenttracker.data.api.CoinSearchAPIService
import com.example.investmenttracker.data.model.APIResponse
import com.example.investmenttracker.data.repository.datasource.CoinRemoteDataSource
import retrofit2.Response

class CoinRemoteDataSourceImpl(
    private val coinSearchAPIService: CoinSearchAPIService
): CoinRemoteDataSource {
    override suspend fun getCoin(name: String, currency: String): Response<APIResponse> {
        return coinSearchAPIService.getCoinInfo(name, currency)
    }
}