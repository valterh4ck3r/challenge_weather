package com.example.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.weather.repository.WeatherRepositoryImpl
import com.example.weather.service.WeatherService
import com.example.weather.ui.WeatherScreen
import com.example.weather.ui.theme.WeatherTheme
import com.example.weather.viewmodel.WeatherViewModel
import com.example.weather.viewmodel.WeatherViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ── Manual Dependency Injection ──
        // In a production app, this would be handled by Hilt/Dagger.
        val httpClient = WeatherService.createDefaultClient()
        val weatherService = WeatherService(httpClient)
        val repository = WeatherRepositoryImpl(weatherService)
        val viewModelFactory = WeatherViewModelFactory(repository)

        val viewModel = ViewModelProvider(this, viewModelFactory)[WeatherViewModel::class.java]

        setContent {
            WeatherTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }
    }
}