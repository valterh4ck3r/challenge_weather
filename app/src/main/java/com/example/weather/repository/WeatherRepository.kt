package com.example.weather.repository

import com.example.weather.model.WeatherInfo

/**
 * Repository interface for weather data.
 * Abstracts the data source from the ViewModel, enabling testability.
 */
interface WeatherRepository {

    /**
     * Searches for a city by name and returns its current weather.
     *
     * @param query The city name to search for.
     * @return [WeatherInfo] with city details and current weather.
     * @throws NoResultsException if no cities match the query.
     */
    suspend fun searchCityWeather(query: String): WeatherInfo
}

/**
 * Exception thrown when the geocoding API returns no results.
 */
class NoResultsException(query: String) : Exception("No results found for \"$query\"")