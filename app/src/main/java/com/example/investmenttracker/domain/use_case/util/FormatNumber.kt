package com.example.investmenttracker.domain.use_case.util

import java.text.DecimalFormat


fun formatPrice(number: Double): String {
    return convertScientificNotationToString(number)
}

fun convertScientificNotationToString(number: Double): String {
    val numberStr = number.toString()
    return if (numberStr.contains('-')) {
        val parts = numberStr.split('E')
        val coeffStr = parts[0].replace(".", "").replace("-", "")
        val expStr = parts[1].replace("-", "")
        val exp = expStr.toInt()
        val coeffWithDecimal = "0." + "0".repeat(exp - 1) + coeffStr
        String.format("%.10f", coeffWithDecimal.toDouble())
    } else {
        val decimalPlaces = if (numberStr.contains(".")) {
            numberStr.split(".")[1].length
        } else {
            0
        }
        if (decimalPlaces > 4) {
            String.format("%.5f", number)
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
    return String.format("%.2f", totalInvestment)
}