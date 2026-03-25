package com.example.weather.viewmodel

import com.example.weather.model.WeatherInfo

/**
 * Sealed class representing all possible UI states for the weather screen.
 * Using a sealed class makes state handling exhaustive and type-safe.
 */
sealed class WeatherUiState {

    /** Initial state — no search has been performed yet. */
    data object Idle : WeatherUiState()

    /** A search is in progress. */
    data object Loading : WeatherUiState()

    /** Successfully retrieved weather data. */
    data class Success(val weatherInfo: WeatherInfo) : WeatherUiState()

    /** No results found for the given query. */
    data class Empty(val query: String) : WeatherUiState()

    /** An error occurred (network failure, parsing error, etc.). */
    data class Error(val message: String) : WeatherUiState()
}
