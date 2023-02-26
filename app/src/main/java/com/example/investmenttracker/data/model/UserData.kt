package com.example.investmenttracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("user_data")
data class UserData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userTotalInvestment: Double,
    val userTotalBalanceWorth: Double,
    val userTotalProfit: Double,
    val userTotalLoss: Double,
    val userBalanceDailyChange: Double,
    val userBalanceWeeklyChange: Double,
    val userBalanceMonthlyChange: Double,
    val userTotalCoinInvestedQuantity: Int
): java.io.Serializable
