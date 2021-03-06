package za.co.rundun.openweather.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
class Weather(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "weather_id") val weatherId: Long = 0L,
    val longitude: Double,
    val latitude: Double,
    val description: String,
    val base: String,
    val name: String,
    @ColumnInfo(name = "wind_speed") val windSpeed: Double,
    @ColumnInfo(name = "wind_degree") val windDegree: Int,
    val icon: String,
    val main: String,
    val temp: Double,
    @ColumnInfo(name = "feels_like") val feelsLike: Double,
    @ColumnInfo(name = "temp_min") val tempMin: Double,
    @ColumnInfo(name = "temp_max") val tempMax: Double
) {
    override fun toString(): String {
        return name
    }
}