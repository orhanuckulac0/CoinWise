package com.example.investmenttracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("coins")
data class CoinModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val cmcId: Int,
    val name: String,
    val slug: String,
    val symbol: String,
    var price: Double,
    var marketCap: Double,
    var percentChange1h: Double,
    var percentChange24h: Double,
    var percentChange7d: Double,
    var percentChange30d: Double,
    val totalTokenHeldAmount: Double,
    val totalInvestmentAmount: Double,
    val totalInvestmentWorth: Double
): java.io.Serializable