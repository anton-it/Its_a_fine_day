package com.ak87.itsafineday.adapters

data class WeatherModel (
    val city: String,
    val time: String,
    val condition: String,
    val currentTemp: String,
    val maxTemp: String,
    val minTemp: String,
    val imageUrl: String,
    val dataHours: String
        )