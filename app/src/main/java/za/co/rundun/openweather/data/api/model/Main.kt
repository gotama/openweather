package za.co.rundun.openweather.data.api.model

import com.google.gson.annotations.SerializedName

data class Main(
    @field:SerializedName("temp") val temp: Double,
    @field:SerializedName("feels_like") val feelsLike: Double,
    @field:SerializedName("temp_min") val tempMin: Double,
    @field:SerializedName("temp_max") val tempMax: Double,
    @field:SerializedName("pressure") val pressure: Int,
    @field:SerializedName("humidity") val humidity: Int
) {
    override fun toString(): String {
        return "Main {" +
                "temp='" + temp + '\'' +
                "feelsLike='" + feelsLike + '\'' +
                "tempMin='" + tempMin + '\'' +
                "tempMax='" + tempMax + '\'' +
                "pressure='" + pressure + '\'' +
                "humidity='" + humidity + '\'' +
                '}'
    }
}