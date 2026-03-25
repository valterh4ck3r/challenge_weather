package com.example.weather.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weather.model.WeatherFirstResponse
import com.example.weather.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

class WeatherViewModel(private val weatherRepository: WeatherRepository, private val viewModelScope: CoroutineScope) : ViewModel(viewModelScope) {

    val flowCitySearch : MutableStateFlow<WeatherFirstResponse?> = MutableStateFlow(null)

    suspend fun searchCity(query: String) {
        flowCitySearch.emit(weatherRepository.searchCity(query = query))
    }

}