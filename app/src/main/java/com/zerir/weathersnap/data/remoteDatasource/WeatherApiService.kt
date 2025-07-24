package com.zerir.weathersnap.data.remoteDatasource

import com.zerir.weathersnap.data.model.WeatherApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/current.json")
    suspend fun getCurrentWeather(
        @Query("q") coordinates: String
    ): WeatherApiResponse
}