package com.zerir.weathersnap.data.localDatasource

import kotlinx.coroutines.flow.Flow
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface SettingsLocalDataSource {
    suspend fun setShowCelsius(value: Boolean)
    suspend fun getShowCelsius(): Boolean
    val showCelsiusFlow: Flow<Boolean>
}

private const val SETTINGS_DATASTORE = "settings_datastore"
private val Context.dataStore by preferencesDataStore(name = SETTINGS_DATASTORE)

@Singleton
class SettingsLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsLocalDataSource {

    private object Keys {
        val SHOW_CELSIUS = booleanPreferencesKey("show_celsius")
    }

    override val showCelsiusFlow: Flow<Boolean> =
        context.dataStore.data
            .catch { e ->
                if (e is IOException) emit(emptyPreferences()) else throw e
            }
            .map { prefs -> prefs[Keys.SHOW_CELSIUS] ?: true } // default = Celsius

    override suspend fun setShowCelsius(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_CELSIUS] = value
        }
    }

    override suspend fun getShowCelsius(): Boolean {
        return showCelsiusFlow.first()
    }
}
