package com.zerir.weathersnap.data.repository

import com.zerir.weathersnap.data.localDatasource.SettingsLocalDataSource
import com.zerir.weathersnap.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val local: SettingsLocalDataSource
) : SettingsRepository {

    override suspend fun setShowCelsius(value: Boolean) = local.setShowCelsius(value)

    override suspend fun getShowCelsius(): Boolean = local.getShowCelsius()

    override val showCelsiusFlow: Flow<Boolean> = local.showCelsiusFlow
}
