package com.example.openweather.data.api.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @field:SerializedName("coord") val coordinate: Coordinate,
    @field:SerializedName("weather") val weather: List<Weather>,
    @field:SerializedName("base") val base: String,
    @field:SerializedName("main") val main: Main,
    @field:SerializedName("wind") val wind: Wind,
    @field:SerializedName("clouds") val clouds: Clouds,
    @field:SerializedName("dt") val dt: Int,
    @field:SerializedName("sys") val sys: Sys,
    @field:SerializedName("timezone") val timezone: Int,
    @field:SerializedName("id") val id: Int,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("cod") val cod: Int,
) {
    override fun toString(): String {
        return "WeatherResponse {" +
                "coordinate='" + coordinate.toString() + '\'' +
                "weather='" + weather.toString() + '\'' +
                "base='" + base + '\'' +
                "main='" + main.toString() + '\'' +
                "wind='" + wind.toString() + '\'' +
                "clouds='" + clouds.toString() + '\'' +
                "dt='" + dt + '\'' +
                "sys='" + sys.toString() + '\'' +
                "timezone='" + timezone + '\'' +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "cod='" + cod + '\'' +
                '}'
    }
}