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
    val price: Double,
    val marketCap: Double,
    val percentChange1h: Double,
    val percentChange24h: Double,
    val percentChange7d: Double,
    val percentChange30d: Double,
    val totalTokenHeldAmount: Double,
    val totalInvestmentAmount: Double
): java.io.Serializable