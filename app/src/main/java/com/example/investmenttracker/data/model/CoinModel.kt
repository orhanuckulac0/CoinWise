package com.example.investmenttracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("coins")
data class CoinModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val cmcRank: Int,
    val cmcId: Int,
    val name: String,
    val slug: String,
    val symbol: String,
    val marketCap: Double,
    val percentChange1h: Double,
    val percentChange30d: Double,
    val percentChange24h: Double,
    val percentChange7d: Double,
    val price: Double,
): java.io.Serializable
