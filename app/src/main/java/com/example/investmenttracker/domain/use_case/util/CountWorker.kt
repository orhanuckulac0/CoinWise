package com.example.investmenttracker.domain.use_case.util

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class CountWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        var count = 0
        while (count <= 99) {
            count++
            Thread.sleep(1000)
        }

        val sharedPrefCount = applicationContext.getSharedPreferences(Constants.PREF_WORKER, Context.MODE_PRIVATE)
        sharedPrefCount.edit().putInt(Constants.WORKER_COUNT, count).apply()

        val sharedPrefIsFinished = applicationContext.getSharedPreferences(Constants.PREF_WORKER_RESULT, Context.MODE_PRIVATE)
        sharedPrefIsFinished.edit().putBoolean(Constants.WORKER_RESULT, true).apply()
        return Result.success()
    }
}
