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

fun createPieChart(
    pieChart: PieChart,
    percentFormatter: PercentFormatter?,
    context: Context,
    walletCoins: List<CoinModel>,
    theme: Boolean
    ){
    val legend = pieChart.legend

    // set user percent value,
    // setting description as disabled and offset for pie chart
    pieChart.setUsePercentValues(true)
    pieChart.description.isEnabled = false
    pieChart.setDrawEntryLabels(false) // hide label string on chart
    pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

    // set drag
    pieChart.dragDecelerationFrictionCoef = 0.95f

    // set the size of the chart
    val layoutParams = pieChart.layoutParams
    layoutParams.width = 1100 // in pixels
    layoutParams.height = 1100 // in pixels
    pieChart.layoutParams = layoutParams


    // set circle color and alpha
    pieChart.setTransparentCircleColor(Color.WHITE)
    pieChart.setTransparentCircleAlpha(110)

    // set hole radius and add transparent line
    pieChart.holeRadius = 0f
    pieChart.transparentCircleRadius = 0f

    // set rotation of the pie
    pieChart.rotationAngle = 0f

    // enable rotation of the pieChart by touch
    pieChart.isRotationEnabled = true
    pieChart.isHighlightPerTapEnabled = true

    // set animation for the pie chart
    pieChart.animateY(1400, Easing.EaseInOutQuad)

    // update legend data
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

    // on below line we are creating array list and
    // adding data to it to display in pie chart
    val entries: ArrayList<PieEntry> = ArrayList()

    // calculate total investment amount
    val totalInvestment = walletCoins.sumOf { it.totalInvestmentAmount }
    val sortedWallet = walletCoins.sortedByDescending { it.totalInvestmentAmount }

    for (coin in sortedWallet) {
        val percent = (coin.totalInvestmentAmount / totalInvestment) * 100
        // add each coin user holds as PieEntry
        entries.add(
            PieEntry(
            String.format("%.2f", percent).toFloat(),
            formatCoinNameText(coin.symbol) + " " + String.format("%.2f", percent)+"%"
            )
        )
    }

    // set pie data set, set label as empty string
    val dataSet = PieDataSet(entries, "")

    // use percentFormatter obj
    dataSet.valueFormatter = percentFormatter
    dataSet.setDrawValues(true)

    // set Icons to false.
    dataSet.setDrawIcons(false)

    // set slice for pie, make it thinner
    dataSet.sliceSpace = 1f
    dataSet.iconsOffset = MPPointF(0f, 40f)
    dataSet.selectionShift = 5f

    // set 120 colors.
    val colors = ArrayList<Int>()
    for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
    for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
    for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
    for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
    for (c in ColorTemplate.MATERIAL_COLORS) colors.add(c)
    for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)

    dataSet.colors = colors

    // set PieDataSet
    val data = PieData(dataSet)
    data.setValueTextSize(15F)
    data.setValueTypeface(Typeface.DEFAULT_BOLD)
    data.setValueTextColor(Color.WHITE)

    pieChart.data = data

    // undo all highlights
    pieChart.highlightValues(null)

    // loading chart
    pieChart.invalidate()
}