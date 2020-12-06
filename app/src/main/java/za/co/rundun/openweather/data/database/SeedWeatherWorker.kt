package com.example.openweather.data.database

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.coroutineScope

const val WEATHER_DATA_FILENAME = "weather.json"

class SeedWeatherWorker (
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(WEATHER_DATA_FILENAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val weatherType = object : TypeToken<Weather>() {}.type
                    val weather: Weather = Gson().fromJson(jsonReader, weatherType)

                    val database = WeatherDatabase.getInstance(applicationContext)
                    database.weatherDAO().insertWeather(weather)

                    Result.success()
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SeedWeatherWorker"
    }
}