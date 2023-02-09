package com.example.investmenttracker.data.model


import com.google.gson.annotations.SerializedName

data class APIResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
)