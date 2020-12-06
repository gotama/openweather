package com.example.openweather.data.api.model

import com.google.gson.annotations.SerializedName

data class Wind(
    @field:SerializedName("speed") val speed: Double,
    @field:SerializedName("deg") val degree: Int
) {
    override fun toString(): String {
        return "Wind {" +
                "speed='" + speed + '\'' +
                "degree='" + degree + '\'' +
                '}'
    }
}