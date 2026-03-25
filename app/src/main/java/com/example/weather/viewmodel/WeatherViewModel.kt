package com.example.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.repository.NoResultsException
import com.example.weather.repository.WeatherRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * ViewModel for the Weather Search screen.
 *
 * Architecture decisions:
 * - Uses [StateFlow] for reactive, lifecycle-aware state observation.
 * - Implements debounce (500ms) via [Flow.debounce] to avoid excessive API calls.
 * - Uses [collectLatest] which automatically cancels the previous collector block
 *   when a new value is emitted — this replaces manual coroutine cancellation.
 * - Dependencies are injected via constructor, keeping the VM free of Android
 *   framework code and fully unit-testable.
 * - All coroutines use [viewModelScope] for proper structured concurrency.
 */
class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    /** The current search query text, bound to the TextField. */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /** The current UI state. */
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        observeSearchQuery()
    }

    /**
     * Called by the UI when the search text changes.
     */
    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    /**
     * Sets up the debounced search pipeline.
     *
     * Flow pipeline:
     * 1. [debounce] 500ms — waits for user to stop typing.
     * 2. [distinctUntilChanged] — skips duplicate emissions.
     * 3. [filter] — ignores blank queries.
     * 4. [collectLatest] — cancels the previous in-flight request when a new query arrives.
     *    This is the key mechanism for request cancellation.
     */
    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(DEBOUNCE_DELAY_MS)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _uiState.value = WeatherUiState.Idle
                        return@collectLatest
                    }
                    performSearch(query)
                }
        }
    }

    /**
     * Executes the search, handling all state transitions.
     */
    private suspend fun performSearch(query: String) {
        _uiState.value = WeatherUiState.Loading
        try {
            val weatherInfo = repository.searchCityWeather(query)
            _uiState.value = WeatherUiState.Success(weatherInfo)
        } catch (e: NoResultsException) {
            _uiState.value = WeatherUiState.Empty(query)
        } catch (e: Exception) {
            _uiState.value = WeatherUiState.Error(
                e.message ?: "An unexpected error occurred"
            )
        }
    }

    companion object {
        const val DEBOUNCE_DELAY_MS = 500L
    }
}

/**
 * Factory for creating [WeatherViewModel] with injected dependencies.
 * This replaces the need for Hilt/Dagger for this simple use case.
 */
class WeatherViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}