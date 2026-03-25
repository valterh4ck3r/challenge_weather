package com.example.weather.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Geocoding API Response ──

@Serializable
data class GeocodingResponse(
    val results: List<GeocodingResult>? = null
)

@Serializable
data class GeocodingResult(
    val id: Int? = null,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    val admin1: String? = null
)

// ── Forecast API Response ──

@Serializable
data class ForecastResponse(
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerialName("current_weather") val currentWeather: CurrentWeather
)

@Serializable
data class CurrentWeather(
    val temperature: Double,
    @SerialName("weathercode") val weatherCode: Int,
    val windspeed: Double? = null,
    @SerialName("winddirection") val windDirection: Double? = null,
    @SerialName("is_day") val isDay: Int? = null,
    val time: String? = null
)

// ── Domain model for the UI ──

data class WeatherInfo(
    val cityName: String,
    val country: String?,
    val region: String?,
    val temperature: Double,
    val weatherCode: Int,
    val weatherDescription: String
)