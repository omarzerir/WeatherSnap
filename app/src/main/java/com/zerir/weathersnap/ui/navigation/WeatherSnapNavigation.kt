package com.zerir.weathersnap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zerir.weathersnap.ui.screens.SettingsScreen
import com.zerir.weathersnap.ui.screens.SharedDataViewModel
import com.zerir.weathersnap.ui.screens.camera.CameraScreen
import com.zerir.weathersnap.ui.screens.home.HomeScreen

@Composable
fun WeatherSnapNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    sharedDataViewModel: SharedDataViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = WeatherSnapDestinations.HOME.route,
        modifier = modifier
    ) {
        composable(WeatherSnapDestinations.HOME.route) {
            HomeScreen(
                sharedDataViewModel = sharedDataViewModel,
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
                sharedDataViewModel = sharedDataViewModel,
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