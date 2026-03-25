package com.example.weather

import com.example.weather.model.WeatherInfo
import com.example.weather.repository.NoResultsException
import com.example.weather.repository.WeatherRepository
import com.example.weather.viewmodel.WeatherUiState
import com.example.weather.viewmodel.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeRepository: FakeWeatherRepository
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeWeatherRepository()
        viewModel = WeatherViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Idle`() = runTest(testDispatcher) {
        assertEquals(WeatherUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `search query updates searchQuery state`() = runTest(testDispatcher) {
        viewModel.onQueryChanged("London")
        assertEquals("London", viewModel.searchQuery.value)
    }

    @Test
    fun `successful search emits Loading then Success`() = runTest(testDispatcher) {
        val expectedWeather = WeatherInfo(
            cityName = "London",
            country = "United Kingdom",
            region = "Greater London",
            temperature = 15.0,
            weatherCode = 2,
            weatherDescription = "Partly cloudy"
        )
        fakeRepository.weatherInfoToReturn = expectedWeather

        viewModel.onQueryChanged("London")

        // Advance past debounce time
        advanceTimeBy(600)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Expected Success but got $state", state is WeatherUiState.Success)
        assertEquals(expectedWeather, (state as WeatherUiState.Success).weatherInfo)
    }

    @Test
    fun `empty results emits Empty state`() = runTest(testDispatcher) {
        fakeRepository.exceptionToThrow = NoResultsException("xyz")

        viewModel.onQueryChanged("xyz")

        advanceTimeBy(600)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Expected Empty but got $state", state is WeatherUiState.Empty)
        assertEquals("xyz", (state as WeatherUiState.Empty).query)
    }

    @Test
    fun `network error emits Error state`() = runTest(testDispatcher) {
        fakeRepository.exceptionToThrow = RuntimeException("Network error")

        viewModel.onQueryChanged("London")

        advanceTimeBy(600)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Expected Error but got $state", state is WeatherUiState.Error)
        assertEquals("Network error", (state as WeatherUiState.Error).message)
    }

    @Test
    fun `blank query resets to Idle`() = runTest(testDispatcher) {
        fakeRepository.weatherInfoToReturn = WeatherInfo(
            cityName = "London",
            country = "UK",
            region = null,
            temperature = 15.0,
            weatherCode = 0,
            weatherDescription = "Clear sky"
        )

        viewModel.onQueryChanged("London")
        advanceTimeBy(600)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is WeatherUiState.Success)

        viewModel.onQueryChanged("")
        advanceTimeBy(600)
        advanceUntilIdle()

        assertEquals(WeatherUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `debounce prevents rapid searches`() = runTest(testDispatcher) {
        fakeRepository.weatherInfoToReturn = WeatherInfo(
            cityName = "Paris",
            country = "France",
            region = null,
            temperature = 20.0,
            weatherCode = 1,
            weatherDescription = "Mainly clear"
        )

        // Type rapidly
        viewModel.onQueryChanged("L")
        advanceTimeBy(100)
        viewModel.onQueryChanged("Lo")
        advanceTimeBy(100)
        viewModel.onQueryChanged("Lon")
        advanceTimeBy(100)
        viewModel.onQueryChanged("Lond")
        advanceTimeBy(100)

        // Should NOT have searched yet (less than 500ms since last change)
        assertEquals(0, fakeRepository.searchCallCount)

        // Wait for debounce to complete
        advanceTimeBy(600)
        advanceUntilIdle()

        // Only one search should have been made with the final query
        assertEquals(1, fakeRepository.searchCallCount)
        assertEquals("Lond", fakeRepository.lastSearchedQuery)
    }
}

/**
 * Fake repository for unit testing.
 * Allows controlling the return value or exception to throw.
 */
class FakeWeatherRepository : WeatherRepository {

    var weatherInfoToReturn: WeatherInfo? = null
    var exceptionToThrow: Exception? = null
    var searchCallCount = 0
    var lastSearchedQuery: String? = null

    override suspend fun searchCityWeather(query: String): WeatherInfo {
        searchCallCount++
        lastSearchedQuery = query

        exceptionToThrow?.let { throw it }
        return weatherInfoToReturn
            ?: throw IllegalStateException("FakeWeatherRepository: No return value configured")
    }
}
