package com.example.investmenttracker.data.util


fun formatPrice(number: Double): String {
    return when {
        number % 1 == 0.0 -> number.toInt().toString()
        else -> convertScientificNotationToString(number)
    }
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
        String.format("%.2f", number)
    }
}
