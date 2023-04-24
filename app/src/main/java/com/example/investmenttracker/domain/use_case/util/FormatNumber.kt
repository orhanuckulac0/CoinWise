package com.example.investmenttracker.domain.use_case.util

import com.example.investmenttracker.data.model.CoinModel
import java.text.DecimalFormat
import kotlin.math.abs


fun formatPrice(number: Double): String {
    return convertScientificNotationToString(number)
}

fun convertScientificNotationToString(number: Double): String {
    val numberAsString = number.toString()
    return if (numberAsString.contains('-')) {
        val parts = numberAsString.split('E')
        val coefficient = parts[0].replace(".", "").replace("-", "")
        val exponent = parts[1].replace("-", "").toInt()
        val coefficientWithDecimal = "0." + "0".repeat(exponent - 1) + coefficient
        String.format("%.10f", coefficientWithDecimal.toDouble())
    } else {
        val decimalPlaces = if (numberAsString.contains(".")) {
            numberAsString.split(".")[1].length
        } else {
            0
        }
        if (decimalPlaces > 4) {
            String.format("%.4f", number)
        } else {
            String.format("%.${decimalPlaces}f", number)
        }
    }
}
fun formatTokenHeldAmount(number: Double): String {
    return if (number < 0 || number.toString().startsWith("0.")) {
        number.toString()
    } else {
        // when user input for token held amount is too much, for example: 2500000000000
        // room converts it to scientific notation
        // this will convert it to a double
        val df = DecimalFormat("#,##0.################")
        df.format(number)
    }
}

fun formatTokenTotalValue(coinPrice: Double, totalInvestment: Double): String {
    return String.format("%,.2f", coinPrice*totalInvestment)
}

fun formatTotalBalanceValue(totalInvestment: Double): String {
    return String.format("%,.2f", totalInvestment)
}

fun formatTotalProfitAmountUI(currencySymbol: String, currentCoin: CoinModel): String{
    val profitLoss = (currentCoin.totalInvestmentWorth-currentCoin.totalInvestmentAmount).toString()

    return if (currentCoin.totalInvestmentAmount == 0.0){
        currencySymbol+currentCoin.totalInvestmentAmount
    }else{
        String.format("$currencySymbol%,.2f", abs(profitLoss.toDouble()))
    }
}

fun calculateProfitLossPercentage(currentWorth: Double, initialInvestment: Double): String {
    val percentage = ((currentWorth - initialInvestment) / initialInvestment) * 100
    return String.format("%.2f", percentage)+"%"
}

fun formatToTwoDecimal(number: Double): Double{
    return String.format("%.2f", number).toDouble()
}

fun formatToTwoDecimalWithComma(number: Double): String{
    return String.format("%,.2f", number)
}