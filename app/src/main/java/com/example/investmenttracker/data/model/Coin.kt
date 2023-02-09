package com.example.investmenttracker.data.model


import com.google.gson.annotations.SerializedName

data class Coin(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("symbol")
    val symbol: String,
): java.io.Serializable