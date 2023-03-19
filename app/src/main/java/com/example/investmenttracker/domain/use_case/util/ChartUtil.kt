package com.example.investmenttracker.domain.use_case.util

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.formatter.PercentFormatter

// to prevent later memory management issues, create once use everywhere
object ChartUtil {
    fun getPercentFormatter(pieChart: PieChart): PercentFormatter {
        return object : PercentFormatter(pieChart) {
            override fun getFormattedValue(value: Float): String {
                return if (value % 10 == 0f && value <= 100f) {
                    value.toInt().toString() + "%"
                } else {
                    super.getFormattedValue(value)
                }
            }
        }
    }
}
