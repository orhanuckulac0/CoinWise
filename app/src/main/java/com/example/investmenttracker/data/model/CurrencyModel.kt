package com.example.investmenttracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("currency_table")
data class CurrencyModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var currencyName: String,
    var currencyRate: Float,
): java.io.Serializable
