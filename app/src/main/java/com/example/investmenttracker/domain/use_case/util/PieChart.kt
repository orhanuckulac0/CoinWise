package com.example.investmenttracker.domain.use_case.util

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import java.util.*
import kotlin.collections.ArrayList

fun createPieChart(
    pieChart: PieChart,
    percentFormatter: PercentFormatter?,
    context: Context,
    walletCoins: List<CoinModel>,
    theme: Boolean
    ){
    val legend = pieChart.legend

    pieChart.setUsePercentValues(true)
    pieChart.description.isEnabled = false
    pieChart.setDrawEntryLabels(false) // hide label string on chart
    pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

    pieChart.dragDecelerationFrictionCoef = 0.95f

    val displayMetrics = context.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels

    val size = (screenWidth * 0.9  ).toInt()
    val layoutParams = pieChart.layoutParams
    layoutParams.width = size
    layoutParams.height = size
    pieChart.layoutParams = layoutParams


    pieChart.setTransparentCircleColor(Color.WHITE)
    pieChart.setTransparentCircleAlpha(110)

    pieChart.holeRadius = 0f
    pieChart.transparentCircleRadius = 0f

    pieChart.rotationAngle = 0f

    pieChart.isRotationEnabled = true
    pieChart.isHighlightPerTapEnabled = true

    pieChart.animateY(1400, Easing.EaseInOutQuad)

    if (theme){
        legend.textColor = ContextCompat.getColor(context, R.color.white)
    }else{
        legend.textColor = ContextCompat.getColor(context, R.color.black)
    }
    legend.textSize = 15f
    legend.formSize = 15f
    legend.formToTextSpace = 8f
    legend.form = Legend.LegendForm.CIRCLE
    legend.isWordWrapEnabled = true
    legend.xEntrySpace = 15f

    val entries: ArrayList<PieEntry> = ArrayList()

    val totalInvestment = walletCoins.sumOf { it.totalInvestmentAmount }
    val sortedWallet = walletCoins.sortedByDescending { it.totalInvestmentAmount }

    for (coin in sortedWallet) {
        val percent = (coin.totalInvestmentAmount / totalInvestment) * 100
        val percentString = String.format(Locale.getDefault(), "%.2f%%", percent)
        entries.add(
            PieEntry(percent.toFloat(), formatCoinNameText(coin.symbol) + " " + percentString)
        )
    }

    val dataSet = PieDataSet(entries, "")

    dataSet.valueFormatter = percentFormatter
    dataSet.setDrawValues(true)

    dataSet.setDrawIcons(false)

    dataSet.sliceSpace = 1f
    dataSet.iconsOffset = MPPointF(0f, 40f)
    dataSet.selectionShift = 5f

    val colors = ArrayList<Int>()
    for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
    for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
    for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
    for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
    for (c in ColorTemplate.MATERIAL_COLORS) colors.add(c)
    for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)

    dataSet.colors = colors

    val data = PieData(dataSet)
    data.setValueTextSize(15F)
    data.setValueTypeface(Typeface.DEFAULT_BOLD)
    data.setValueTextColor(Color.WHITE)

    pieChart.data = data

    pieChart.highlightValues(null)

    pieChart.invalidate()
}