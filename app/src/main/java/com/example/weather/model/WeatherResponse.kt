package com.example.weather.model

import java.io.Serializable

data class WeatherFirstResponse(val latitude: String, val longitude: String, val name: String) : Serializable