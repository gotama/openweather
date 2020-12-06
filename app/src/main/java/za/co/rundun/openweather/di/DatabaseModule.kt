package com.example.openweather.di

import android.content.Context
import com.example.openweather.data.database.WeatherDAO
import com.example.openweather.data.database.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return WeatherDatabase.getInstance(context)
    }

    @Provides
    fun provideWeatherDAO(weatherDatabase: WeatherDatabase): WeatherDAO {
        return weatherDatabase.weatherDAO()
    }
}