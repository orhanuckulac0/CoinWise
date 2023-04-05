package com.example.investmenttracker.domain.use_case.util

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import com.example.investmenttracker.R

fun spannableTextRed(fullText: String, string: String, context:Context): SpannableString {
    val spannableString = SpannableString(fullText)
    val startIndex = fullText.indexOf(string)
    val endIndex = startIndex + string.length
    val redColor = context.getColor(R.color.red_color_percentage)
    spannableString.setSpan(
        ForegroundColorSpan(redColor),
        startIndex,
        endIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    spannableString.setSpan(
        StyleSpan(Typeface.BOLD),
        startIndex,
        endIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

fun spannableTextGreen(fullText: String, string: String, context:Context): SpannableString {
    val spannableString = SpannableString(fullText)
    val startIndex = fullText.indexOf(string)
    val endIndex = startIndex + string.length
    val redColor = context.getColor(R.color.green_color_percentage)
    spannableString.setSpan(
        ForegroundColorSpan(redColor),
        startIndex,
        endIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    spannableString.setSpan(
        StyleSpan(Typeface.BOLD),
        startIndex,
        endIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}