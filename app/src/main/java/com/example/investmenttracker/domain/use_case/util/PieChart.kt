package com.example.investmenttracker.domain.use_case.util

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.example.investmenttracker.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF

fun createPieChart(pieChart: PieChart, context: Context){
    val legend = pieChart.legend

    // on below line we are setting user percent value,
    // setting description as disabled and offset for pie chart
    pieChart.setUsePercentValues(true)
    pieChart.description.isEnabled = false
    pieChart.setDrawEntryLabels(false) // hide label string on chart
    pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

    // on below line we are setting drag for our pie chart
    pieChart.dragDecelerationFrictionCoef = 0.95f

    val layoutParams = pieChart.layoutParams
    layoutParams.width = 1100 // in pixels
    layoutParams.height = 1100 // in pixels
    pieChart.layoutParams = layoutParams


    // on below line we are setting circle color and alpha
    pieChart.setTransparentCircleColor(Color.WHITE)
    pieChart.setTransparentCircleAlpha(110)

    // on  below line we are setting hole radius
    pieChart.holeRadius = 48f
    pieChart.transparentCircleRadius = 51f

    // on below line we are setting
    // rotation for our pie chart
    pieChart.rotationAngle = 0f

    // enable rotation of the pieChart by touch
    pieChart.isRotationEnabled = true
    pieChart.isHighlightPerTapEnabled = true

    // on below line we are setting animation for our pie chart
    pieChart.animateY(1400, Easing.EaseInOutQuad)

    // update legend data
    legend.textColor = ContextCompat.getColor(context, R.color.white)
    legend.textSize = 15f
    legend.formSize = 15f
    legend.formToTextSpace = 10f
    legend.form = Legend.LegendForm.CIRCLE
    legend.isWordWrapEnabled = true

    // on below line we are creating array list and
    // adding data to it to display in pie chart
    val entries: ArrayList<PieEntry> = ArrayList()
    entries.add(PieEntry(10f, "BTC"))
    entries.add(PieEntry(10f, "RH"))
    entries.add(PieEntry(10f, "SDF"))
    entries.add(PieEntry(10f, "DFFSF"))
    entries.add(PieEntry(10f, "AF"))
    entries.add(PieEntry(10f, "ADAD"))
    entries.add(PieEntry(10f, "DFFSFS"))
    entries.add(PieEntry(1f, "CCBB"))
    entries.add(PieEntry(7f, "XVXVCV"))
    entries.add(PieEntry(7f, "ZZXC"))


    // on below line we are setting pie data set
    val dataSet = PieDataSet(entries, "")
    val percentFormatter = object : PercentFormatter(pieChart) {
        override fun getFormattedValue(value: Float): String {
            return if (value % 10 == 0f && value <= 100f) {
                value.toInt().toString()+"%"
            } else {
                super.getFormattedValue(value)
            }
        }
    }

    dataSet.valueFormatter = percentFormatter
    dataSet.setDrawValues(true)

    // on below line we are setting icons.
    dataSet.setDrawIcons(false)

    // on below line we are setting slice for pie
    dataSet.sliceSpace = 1f
    dataSet.iconsOffset = MPPointF(0f, 40f)
    dataSet.selectionShift = 5f

    // on below line we are setting colors.
    val colors = ArrayList<Int>()
    for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
    for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
    for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
    for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
    for (c in ColorTemplate.MATERIAL_COLORS) colors.add(c)
    for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)

    dataSet.colors = colors

    // on below line we are setting pie data set
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