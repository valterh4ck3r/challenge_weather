package com.example.weather.repository

import com.example.weather.model.WeatherCodeMapper
import com.example.weather.model.WeatherInfo
import com.example.weather.service.WeatherService

/**
 * Implementation of [WeatherRepository] that orchestrates
 * geocoding + forecast service calls and maps to domain model.
 */
class WeatherRepositoryImpl(
    private val weatherService: WeatherService
) : WeatherRepository {

    override suspend fun searchCityWeather(query: String): WeatherInfo {
        // Step 1: Geocode city name → coordinates
        val geocodingResponse = weatherService.searchCity(query)
        val firstResult = geocodingResponse.results?.firstOrNull()
            ?: throw NoResultsException(query)

        // Step 2: Fetch current weather for the coordinates
        val forecastResponse = weatherService.getWeather(
            latitude = firstResult.latitude,
            longitude = firstResult.longitude
        )

        val currentWeather = forecastResponse.currentWeather

        // Step 3: Map to domain model
        return WeatherInfo(
            cityName = firstResult.name,
            country = firstResult.country,
            region = firstResult.admin1,
            temperature = currentWeather.temperature,
            weatherCode = currentWeather.weatherCode,
            weatherDescription = WeatherCodeMapper.getDescription(currentWeather.weatherCode)
        )
    }
}