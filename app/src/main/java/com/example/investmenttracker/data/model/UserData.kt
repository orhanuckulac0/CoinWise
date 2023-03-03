package com.example.investmenttracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("user_data")
data class UserData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var userTotalInvestment: Double,
    var userTotalBalanceWorth: Double,
    var userTotalProfit: Double,
    var userTotalLoss: Double,
    var userBalanceDailyChange: Double,
    var userBalanceWeeklyChange: Double,
    var userBalanceMonthlyChange: Double,
    var userTotalCoinInvestedQuantity: Int
): java.io.Serializable
