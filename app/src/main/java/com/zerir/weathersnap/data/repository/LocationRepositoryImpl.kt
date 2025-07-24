package com.zerir.weathersnap.data.repository

import com.zerir.weathersnap.data.remoteDatasource.LocationService
import com.zerir.weathersnap.domain.model.Coordinates
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.repository.LocationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationService: LocationService
) : LocationRepository {

    override suspend fun getCurrentLocation(): UiState<Coordinates> {
        return try {
            val androidLocation = locationService.getCurrentLocation()
            val coordinates = Coordinates(
                latitude = androidLocation.latitude,
                longitude = androidLocation.longitude
            )
            UiState.Success(coordinates)
        } catch (e: SecurityException) {
            UiState.Error("Location permission denied")
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to get current location")
        }
    }
}