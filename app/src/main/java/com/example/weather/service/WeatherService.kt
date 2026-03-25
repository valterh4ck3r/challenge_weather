package com.example.weather.service

import com.example.weather.model.ForecastResponse
import com.example.weather.model.GeocodingResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Service layer responsible for making HTTP calls using Ktor.
 * Receives an [HttpClient] via constructor injection for testability.
 */
class WeatherService(private val client: HttpClient) {

    companion object {
        private const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/v1/search"
        private const val FORECAST_BASE_URL = "https://api.open-meteo.com/v1/forecast"

        /**
         * Factory to create a default [HttpClient] configured with JSON serialization and logging.
         */
        fun createDefaultClient(): HttpClient = HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                })
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }
    }

    /**
     * Searches for cities matching the given [query].
     *
     * @param query The city name to search for.
     * @return [GeocodingResponse] containing a list of matching locations.
     */
    suspend fun searchCity(query: String): GeocodingResponse {
        return client.get(GEOCODING_BASE_URL) {
            parameter("name", query)
        }.body()
    }

    /**
     * Fetches the current weather for the given coordinates.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return [ForecastResponse] containing the current weather.
     */
    suspend fun getWeather(latitude: Double, longitude: Double): ForecastResponse {
        return client.get(FORECAST_BASE_URL) {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("current_weather", true)
        }.body()
    }
}