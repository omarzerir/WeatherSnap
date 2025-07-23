package com.zerir.weathersnap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zerir.weathersnap.ui.screens.CameraScreen
import com.zerir.weathersnap.ui.screens.HomeScreen
import com.zerir.weathersnap.ui.screens.SettingsScreen

@Composable
fun WeatherSnapNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = WeatherSnapDestinations.HOME.route,
        modifier = modifier
    ) {
        composable(WeatherSnapDestinations.HOME.route) {
            HomeScreen(
                onNavigateToCamera = {
                    navController.navigate(WeatherSnapDestinations.CAMERA.route)
                },
                onNavigateToSettings = {
                    navController.navigate(WeatherSnapDestinations.SETTINGS.route)
                }
            )
        }

        composable(WeatherSnapDestinations.CAMERA.route) {
            CameraScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(WeatherSnapDestinations.SETTINGS.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}