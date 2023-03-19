package com.example.investmenttracker.domain.use_case.util

import android.os.Build
import android.os.Bundle
import java.io.Serializable

// up to date approach to get serializable
@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Bundle.customGetSerializable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, T::class.java)
    } else {
        getSerializable(key) as? T
    }
}
