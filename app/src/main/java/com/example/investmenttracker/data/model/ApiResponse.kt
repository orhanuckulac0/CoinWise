package com.example.investmenttracker.data.model


import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
)

fun ApiResponse.toCoinModel(): CoinModel {
    return CoinModel(
        id = 0,
        cmcRank = data.info.cmcRank,
        cmcId = data.info.cmcId,
        name = data.info.name,
        symbol = data.info.symbol,
        slug = data.info.slug,
        marketCap = data.info.quote.price.marketCap,
        percentChange30d = data.info.quote.price.percentChange30d,
        percentChange1h = data.info.quote.price.percentChange1h,
        percentChange24h = data.info.quote.price.percentChange24h,
        percentChange7d = data.info.quote.price.percentChange7d,
        price = data.info.quote.price.price
    )
}