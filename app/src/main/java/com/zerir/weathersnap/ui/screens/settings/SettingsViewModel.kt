package com.zerir.weathersnap.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerir.weathersnap.BuildConfig
import com.zerir.weathersnap.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsState(
            isCelsius = true,
            appVersion = BuildConfig.VERSION_NAME
        )
    )
    val state: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.showCelsiusFlow.collectLatest { showC ->
                _uiState.value = _uiState.value.copy(isCelsius = showC)
            }
        }
    }

    fun toggleTemperatureUnit(newValue: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShowCelsius(newValue)
        }
    }
}
