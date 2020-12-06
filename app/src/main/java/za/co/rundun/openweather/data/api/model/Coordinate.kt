package com.example.openweather.data.api.model

import com.google.gson.annotations.SerializedName

data class Coordinate(
    @field:SerializedName("lon") val longitude: Double,
    @field:SerializedName("lat") val latitude: Double
) {
    override fun toString(): String {
        return "Coordinate {" +
                "longitude='" + longitude + '\'' +
                "latitude='" + latitude + '\'' +
                '}'
    }
}