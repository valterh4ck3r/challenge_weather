package com.example.weather.repository

import com.example.weather.model.WeatherFirstResponse

abstract class WeatherRepository {
    abstract suspend fun searchCity(query: String): WeatherFirstResponse
}