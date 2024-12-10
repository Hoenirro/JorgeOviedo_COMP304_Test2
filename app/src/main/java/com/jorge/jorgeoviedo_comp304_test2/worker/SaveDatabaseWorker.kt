package com.jorge.jorgeoviedo_comp304_test2.worker

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.jorge.jorgeoviedo_comp304_test2.roomDB.StockDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SaveDatabaseWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    private val stockDao = StockDatabase.getDatabase(context).stockDao()

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val stocks = stockDao.getAllStocks()
                val json = Gson().toJson(stocks)
                val file = File(applicationContext.filesDir, "stocks.json")

                file.writeText(json)

                // Show a toast message on the main thread
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, "JSON file created", Toast.LENGTH_SHORT).show()
                }

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }
}
