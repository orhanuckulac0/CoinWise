package com.example.investmenttracker.data.model


import com.google.gson.annotations.SerializedName

data class APIResponse(
    @SerializedName("coin")
    val coin: Coin
)