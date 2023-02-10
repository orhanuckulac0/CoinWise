package com.example.investmenttracker.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "coins")
data class Coin(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("currency")
    val currency: Double,
    @SerializedName("coin_24h_change")
    val coin24hChange: Double
): java.io.Serializable