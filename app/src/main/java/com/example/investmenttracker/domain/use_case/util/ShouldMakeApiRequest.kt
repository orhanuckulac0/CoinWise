package com.example.investmenttracker.domain.use_case.util

fun shouldMakeApiRequest(lastApiRequestTime: Long): Boolean {
    val currentTime = System.currentTimeMillis()
    val timeSinceLastRequest = currentTime - lastApiRequestTime
    val elapsedSeconds = timeSinceLastRequest / 1000
    return (lastApiRequestTime == 0L || elapsedSeconds >= 30)
}