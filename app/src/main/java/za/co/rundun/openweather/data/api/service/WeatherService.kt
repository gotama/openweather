package za.co.rundun.openweather.data.api.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import za.co.rundun.openweather.BuildConfig
import za.co.rundun.openweather.data.api.model.WeatherResponse

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String = BuildConfig.WEATHER_ACCESS_KEY,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/"

        fun create(): WeatherService {
            val logger = HttpLoggingInterceptor().apply {
                level =
                    HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherService::class.java)
        }
    }
}