package com.zerir.weathersnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.zerir.weathersnap.ui.navigation.WeatherSnapNavigation
import com.zerir.weathersnap.ui.screens.SharedDataViewModel
import com.zerir.weathersnap.ui.theme.WeatherSnapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val sharedDataViewModel: SharedDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherSnapTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    WeatherSnapNavigation(
                        sharedDataViewModel = sharedDataViewModel,
                    )
                }
            }
        }
    }
}