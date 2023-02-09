package com.example.investmenttracker.data.model


import com.google.gson.annotations.SerializedName

data class CoinPrice(
    @SerializedName("percent_change_1h")
    val percentChange1h: Double,
    @SerializedName("percent_change_24h")
    val percentChange24h: Double,
    @SerializedName("percent_change_30d")
    val percentChange30d: Double,
    @SerializedName("percent_change_7d")
    val percentChange7d: Double,
    @SerializedName("price")
    val price: Double,
)