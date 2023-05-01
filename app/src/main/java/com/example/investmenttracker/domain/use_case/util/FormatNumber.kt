package com.example.investmenttracker.domain.use_case.util

import com.example.investmenttracker.data.model.CoinModel
import java.text.DecimalFormat
import java.util.*
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
        val formatter = Formatter(Locale.US)
        formatter.format("%.10f", coefficientWithDecimal.toDouble()).toString()
    } else {
        val decimalPlaces = if (numberAsString.contains(".")) {
            numberAsString.split(".")[1].length
        } else {
            0
        }
        if (decimalPlaces > 4) {
            val formatter = Formatter(Locale.US)
            formatter.format("%.4f", number).toString()
        } else {
            val formatter = Formatter(Locale.US)
            formatter.format("%.${decimalPlaces}f", number).toString()
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
    val totalValue = coinPrice * totalInvestment
    return String.format(Locale.US, "%,.2f", totalValue)
}

fun formatTotalBalanceValue(totalInvestment: Double): String {
    return String.format(Locale.US, "%,.2f", totalInvestment)
}

fun formatTotalProfitAmountUI(currencySymbol: String, currentCoin: CoinModel): String {
    val profitLoss = currentCoin.totalInvestmentWorth - currentCoin.totalInvestmentAmount

    return if (currentCoin.totalInvestmentAmount == 0.0) {
        "$currencySymbol${formatTotalBalanceValue(currentCoin.totalInvestmentAmount)}"
    } else {
        val formattedProfitLoss = String.format(Locale.US, "%,.2f", abs(profitLoss))
        if (profitLoss < 0) "-$currencySymbol$formattedProfitLoss" else "$currencySymbol$formattedProfitLoss"
    }
}

fun calculateProfitLossPercentage(currentWorth: Double, initialInvestment: Double): String {
    val percentage = ((currentWorth - initialInvestment) / initialInvestment) * 100
    val formatter = Formatter(Locale.US)
    val formattedString = formatter.format("%.2f%%", percentage).toString()
    formatter.close()
    return formattedString
}

fun formatToTwoDecimal(number: Double): Double{
    val formatter = Formatter(Locale.US)
    val formattedString = formatter.format("%.2f", number).toString()
    formatter.close()
    return formattedString.toDouble()
}

fun formatToTwoDecimalWithComma(number: Double): String {
    val formatter = Formatter(Locale.US)
    val formattedString = formatter.format("%,.2f", number).toString()
    formatter.close()
    return formattedString
}
