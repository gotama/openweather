package com.example.openweather.data.api.model

import com.google.gson.annotations.SerializedName

data class Clouds(
    @field:SerializedName("all") val all: Int
) {
    override fun toString(): String {
        return "Clouds {" +
                "all='" + all.toString() + '\'' +
                '}'
    }
}