package za.co.rundun.openweather.data.database

import za.co.rundun.openweather.common.WeatherResult
import za.co.rundun.openweather.data.api.service.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherDao: WeatherDAO,
    private val weatherService: WeatherService
) {

    suspend fun fetchRecentWeather(latitude: Double, longitude: Double):
            WeatherResult<Exception, List<Weather>> = withContext(Dispatchers.IO) {
        try {
            val weather = weatherService.getWeather(latitude, longitude)
            weatherDao.insertWeather(
                Weather(
                    0L, longitude, latitude,
                    weather.weather[0].description, weather.base, weather.name,
                    weather.wind.speed, weather.wind.degree, weather.weather[0].icon
                )
            )
            WeatherResult.build { weatherDao.queryAllWeather() }
        } catch (e: Exception) {
            WeatherResult.build { throw e }
        }

    }

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: WeatherRepository? = null

        fun getInstance(weatherDao: WeatherDAO, weatherService: WeatherService) =
            instance ?: synchronized(this) {
                instance ?: WeatherRepository(weatherDao, weatherService).also { instance = it }
            }
    }
}