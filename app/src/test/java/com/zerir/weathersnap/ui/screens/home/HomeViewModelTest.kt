package com.zerir.weathersnap.ui.screens.home

import androidx.paging.PagingData
import app.cash.turbine.test
import com.zerir.weathersnap.domain.model.Coordinates
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.model.Weather
import com.zerir.weathersnap.domain.repository.ImageHistoryRepository
import com.zerir.weathersnap.domain.repository.LocationRepository
import com.zerir.weathersnap.domain.repository.SettingsRepository
import com.zerir.weathersnap.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var locationRepository: LocationRepository
    private lateinit var imageHistoryRepository: ImageHistoryRepository
    private lateinit var settingsRepository: SettingsRepository

    private val testDispatcher = StandardTestDispatcher()

    // Test data
    private val mockCoordinates = Coordinates(latitude = 40.7128, longitude = -74.0060)
    private val mockWeather = Weather(
        temperatureCelsius = 25.0,
        temperatureFahrenheit = 77.0,
        description = "Sunny",
        iconUrl = "https://example.com/icon.png",
        windSpeedKph = 15.0,
        humidity = 60,
        locationName = "New York"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Initialize mocks
        weatherRepository = mockk()
        locationRepository = mockk()
        imageHistoryRepository = mockk()
        settingsRepository = mockk()

        // Setup default mock behaviors
        every { imageHistoryRepository.getAllImagesPaginated() } returns flowOf(PagingData.empty())
        every { settingsRepository.showCelsiusFlow } returns flowOf(true)
        coEvery { imageHistoryRepository.deleteImage(any(), any()) } returns UiState.Success(Unit)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(
            weatherRepository = weatherRepository,
            locationRepository = locationRepository,
            imageHistoryRepository = imageHistoryRepository,
            settingsRepository = settingsRepository
        )
    }

    @Test
    fun `when settings flow emits new values should update state properly`() = runTest {
        // Arrange - Create a flow that emits multiple values
        every { settingsRepository.showCelsiusFlow } returns flowOf(true, false, true)

        // Act & Assert - Test each emission using Turbine
        viewModel = createViewModel()

        viewModel.state.test {
            // First emission: true
            val firstState = awaitItem()
            assertTrue(firstState.showCelsius)
            assertTrue(firstState.defaultCelsius)

            // Second emission: false
            val secondState = awaitItem()
            assertFalse(secondState.showCelsius)
            assertFalse(secondState.defaultCelsius)

            // Third emission: true
            val thirdState = awaitItem()
            assertTrue(thirdState.showCelsius)
            assertTrue(thirdState.defaultCelsius)

            // Verify no more emissions
            expectNoEvents()
        }
    }

    @Test
    fun `when permission granted should load location and weather successfully`() = runTest {
        // Arrange
        coEvery {
            locationRepository.getCurrentLocation()
        } returns UiState.Success(mockCoordinates)
        coEvery {
            weatherRepository.getCurrentWeather(any(), any())
        } returns UiState.Success(mockWeather)
        viewModel = createViewModel()

        // Act
        viewModel.onEvent(HomeEvent.LocationPermissionGranted)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertTrue(state.hasLocationPermission)
        assertTrue(state.locationState is UiState.Success)
        assertTrue(state.weatherState is UiState.Success)
        assertTrue(state.canNavigateToCamera) // Both location and weather successful
        assertEquals(mockWeather, (state.weatherState as UiState.Success).data)
    }

    @Test
    fun `when weather API fails should handle error correctly`() = runTest {
        // Arrange
        coEvery {
            locationRepository.getCurrentLocation()
        } returns UiState.Success(mockCoordinates)
        coEvery {
            weatherRepository.getCurrentWeather(any(), any())
        } returns UiState.Error("Network error")
        viewModel = createViewModel()

        // Act
        viewModel.onEvent(HomeEvent.LocationPermissionGranted)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertTrue(state.locationState is UiState.Success) // Location still worked
        assertTrue(state.weatherState is UiState.Error)
        assertEquals("Network error", (state.weatherState as UiState.Error).message)
        assertFalse(state.canNavigateToCamera) // Can't navigate due to weather error
        assertFalse(state.isRefreshingWeather)
    }

    @Test
    fun `when delete image event should call repository with correct parameters`() = runTest {
        // Arrange
        viewModel = createViewModel()
        val imageId = "test-image-123"
        val filePath = "/storage/images/photo.jpg"

        // Act
        viewModel.onEvent(HomeEvent.DeleteImage(imageId, filePath))
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            imageHistoryRepository.deleteImage(imageId, filePath)
        }
    }
}