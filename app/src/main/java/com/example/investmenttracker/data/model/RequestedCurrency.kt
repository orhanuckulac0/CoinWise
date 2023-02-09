package com.example.investmenttracker.data.model


import com.google.gson.annotations.SerializedName

data class RequestedCurrency(
    @SerializedName("USD")
    val usd: CoinPrice
)