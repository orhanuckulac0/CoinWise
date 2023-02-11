package com.example.investmenttracker.data.model


import com.google.gson.annotations.SerializedName

data class Quote(
    @SerializedName("USD")
    val price: Price
)