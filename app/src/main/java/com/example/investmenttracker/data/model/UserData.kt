package com.example.investmenttracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("user_data")
data class UserData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var userTotalInvestment: Double,
    var userTotalBalanceWorth: Double,
    var userTotalProfitAndLoss: Double,
    var userTotalProfitAndLossPercentage: Double,
    var userCurrentCurrency: String,
    var userPreviousCurrency: String,
    var userTotalCoinInvestedQuantity: Int,
    var lastApiRequestMade: Long
): java.io.Serializable
