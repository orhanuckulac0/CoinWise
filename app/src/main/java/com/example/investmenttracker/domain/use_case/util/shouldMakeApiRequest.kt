package com.example.investmenttracker.domain.use_case.util

fun shouldMakeApiRequest(lastApiRequestTime: Long): Boolean {
    val currentTime = System.currentTimeMillis()
    val timeSinceLastRequest = currentTime - lastApiRequestTime
    val elapsedSeconds = timeSinceLastRequest / 1000
    return (lastApiRequestTime == 0L || elapsedSeconds >= 60)
}

fun secondsLeft(lastApiRequestTime: Long): Long {
    val currentTimeMillis = System.currentTimeMillis()
    val timeDifferenceMillis = currentTimeMillis - lastApiRequestTime
    val timeDifferenceSeconds = timeDifferenceMillis / 1000
    return 60 - timeDifferenceSeconds
}
