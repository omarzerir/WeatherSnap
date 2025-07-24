package com.zerir.weathersnap.di

import com.zerir.weathersnap.data.camera.CameraService
import com.zerir.weathersnap.data.camera.CameraServiceImpl
import com.zerir.weathersnap.data.remoteDatasource.LocationService
import com.zerir.weathersnap.data.remoteDatasource.LocationServiceImpl
import com.zerir.weathersnap.data.repository.CameraRepositoryImpl
import com.zerir.weathersnap.data.repository.LocationRepositoryImpl
import com.zerir.weathersnap.data.repository.WeatherRepositoryImpl
import com.zerir.weathersnap.domain.repository.CameraRepository
import com.zerir.weathersnap.domain.repository.LocationRepository
import com.zerir.weathersnap.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindLocationService(
        locationServiceImpl: LocationServiceImpl
    ): LocationService

    @Binds
    @Singleton
    abstract fun bindCameraRepository(
        cameraRepositoryImpl: CameraRepositoryImpl
    ): CameraRepository

    @Binds
    @Singleton
    abstract fun bindCameraService(
        cameraServiceImpl: CameraServiceImpl
    ): CameraService
}