package com.example.investmenttracker.data.model


import com.google.gson.annotations.SerializedName

data class Info(
    @SerializedName("cmc_rank")
    val cmcRank: Int,
    @SerializedName("id")
    val cmcId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("quote")
    val quote: Quote,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("total_supply")
    val totalSupply: Int
)