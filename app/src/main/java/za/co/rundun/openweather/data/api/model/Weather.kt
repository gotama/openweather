package com.example.openweather.data.api.model

import com.google.gson.annotations.SerializedName

data class Weather(
    @field:SerializedName("id") val id: Int,
    @field:SerializedName("main") val main: String,
    @field:SerializedName("description") val description: String,
    @field:SerializedName("icon") val icon: String
) {
    override fun toString(): String {
        return "Weather {" +
                "id='" + id + '\'' +
                "main='" + main + '\'' +
                "description='" + description + '\'' +
                "icon='" + icon + '\'' +
                '}'
    }
}