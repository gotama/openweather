package za.co.rundun.openweather.data.database

import androidx.room.*

@Dao
interface WeatherDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weather: Weather)

    @Update
    fun updateWeather(vararg weather: Weather)

    @Delete
    fun deleteWeather(vararg weather: Weather)

    @Query("SELECT * FROM weather")
    suspend fun queryAllWeather(): List<Weather>
}