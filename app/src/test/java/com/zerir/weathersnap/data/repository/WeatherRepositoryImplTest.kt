package com.zerir.weathersnap.data.repository

import com.zerir.weathersnap.data.model.Condition
import com.zerir.weathersnap.data.model.CurrentWeather
import com.zerir.weathersnap.data.model.Location
import com.zerir.weathersnap.data.model.WeatherApiResponse
import com.zerir.weathersnap.data.remoteDatasource.WeatherApiService
import com.zerir.weathersnap.domain.model.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherRepositoryImplTest {

    private val weatherApiService = mockk<WeatherApiService>()
    private val repository = WeatherRepositoryImpl(weatherApiService)

    // Mock API response
    private val mockApiResponse = WeatherApiResponse(
        current = CurrentWeather(
            tempCelsius = 25.0,
            tempFahrenheit = 77.0,
            condition = Condition(
                text = "Sunny",
                icon = "//cdn.weatherapi.com/weather/64x64/day/113.png"
            ),
            windKph = 15.0,
            humidity = 60
        ),
        location = Location(
            name = "New York",
            country = "USA",
            lat = 40.7128,
            lon = -74.0060
        )
    )

    @Test
    fun `when API call succeeds should return success with mapped weather data`() = runTest {
        // Arrange
        val latitude = 40.7128
        val longitude = -74.0060
        val coordinates = "$latitude,$longitude"
        coEvery { weatherApiService.getCurrentWeather(coordinates) } returns mockApiResponse

        // Act
        val result = repository.getCurrentWeather(latitude, longitude)

        // Assert
        assertTrue(result is UiState.Success)
        val weather = result.data
        assertEquals(25.0, weather.temperatureCelsius)
        assertEquals(77.0, weather.temperatureFahrenheit)
        assertEquals("Sunny", weather.description)
        assertEquals("https://cdn.weatherapi.com/weather/64x64/day/113.png", weather.iconUrl)
        assertEquals(15.0, weather.windSpeedKph)
        assertEquals(60, weather.humidity)
        assertEquals("New York", weather.locationName)

        // Verify API was called (don't worry about exact string format)
        coVerify(exactly = 1) { weatherApiService.getCurrentWeather(any()) }
    }

    @Test
    fun `when API call fails should return error`() = runTest {
        // Arrange
        val latitude = 40.7128
        val longitude = -74.0060
        coEvery {
            weatherApiService.getCurrentWeather(any())
        } throws Exception("Network error")

        // Act
        val result = repository.getCurrentWeather(latitude, longitude)

        // Assert
        assertTrue(result is UiState.Error)
        assertEquals("Network error", result.message)
    }
}