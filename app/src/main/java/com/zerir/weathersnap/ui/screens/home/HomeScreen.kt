package com.zerir.weathersnap.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.zerir.weathersnap.domain.model.getDataOrNull
import com.zerir.weathersnap.ui.components.FullImageView
import com.zerir.weathersnap.ui.components.HistorySection
import com.zerir.weathersnap.ui.components.PermissionSettingsDialog
import com.zerir.weathersnap.ui.components.PermissionSettingsDialogData
import com.zerir.weathersnap.ui.components.WeatherSection
import com.zerir.weathersnap.ui.screens.SharedDataViewModel
import com.zerir.weathersnap.utils.openSettings
import com.zerir.weathersnap.utils.rememberCameraPermissionManager
import com.zerir.weathersnap.utils.rememberLocationPermissionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sharedDataViewModel: SharedDataViewModel,
    onNavigateToCamera: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState by homeViewModel.state.collectAsState()
    val imageHistory = homeState.imageHistory.collectAsLazyPagingItems()

    // Update shared data when weather changes
    LaunchedEffect(homeState.weatherState, homeState.locationState) {
        val weather = homeState.weatherState?.getDataOrNull()
        val coordinates = homeState.locationState?.getDataOrNull()
        if (weather != null && coordinates != null) {
            sharedDataViewModel.setWeatherData(weather, coordinates)
        }
    }

    // Full image viewer state
    var showFullImage by remember { mutableStateOf(false) }
    var fullImagePath by remember { mutableStateOf<String?>(null) }

    var settingsDialogData by remember { mutableStateOf<PermissionSettingsDialogData?>(null) }

    // Location Permission
    val locationPermission = rememberLocationPermissionManager()

    locationPermission.Listen { granted ->
        if (granted) {
            if (homeState.hasLocationPermission) return@Listen
            homeViewModel.onEvent(HomeEvent.LocationPermissionGranted)
        } else {
            if (!homeState.hasLocationPermission) return@Listen
            homeViewModel.onEvent(HomeEvent.LocationPermissionDenied)
        }
    }

    val cameraPermission = rememberCameraPermissionManager()

    // Track if we've done the initial location permission request
    var hasRequestedLocationOnce by rememberSaveable { mutableStateOf(false) }

    // One-time location permission request on app start
    LaunchedEffect(hasRequestedLocationOnce) {
        if (!hasRequestedLocationOnce) {
            if (locationPermission.isGranted) {
                homeViewModel.onEvent(HomeEvent.LocationPermissionGranted)
            } else {
                locationPermission.request()
            }
            hasRequestedLocationOnce = true
        }
    }

    fun handleCameraNavigation() {
        if (cameraPermission.isGranted) {
            onNavigateToCamera()
        } else {
            if (cameraPermission.isPermanentlyDenied) {
                settingsDialogData = PermissionSettingsDialogData.Camera
            } else {
                cameraPermission.request(
                    onGranted = { onNavigateToCamera() },
                )
            }
        }
    }

    if (showFullImage && fullImagePath != null) {
        FullImageView(
            imagePath = fullImagePath,
            onClose = { showFullImage = false }
        )
        return
    }

    val context = LocalContext.current
    PermissionSettingsDialog(
        showDialog = settingsDialogData != null,
        data = settingsDialogData,
        onDismiss = { settingsDialogData = null },
        onGoToSettings = {
            context.openSettings()
            settingsDialogData = null
        },
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Weather Snap",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Light
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(
                        onClick = { onNavigateToSettings() },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (homeState.canNavigateToCamera) {
                FloatingActionButton(
                    onClick = { handleCameraNavigation() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Take Weather Photo")
                }
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = homeState.isRefreshingWeather,
            onRefresh = { homeViewModel.onEvent(HomeEvent.RefreshWeather) },
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = 0.dp, // Remove bottom padding
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
            )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(2.dp)) }

                // Weather Section
                item {
                    WeatherSection(
                        locationState = homeState.locationState,
                        weatherState = homeState.weatherState,
                        hasLocationPermission = homeState.hasLocationPermission,
                        lastUpdated = homeState.lastUpdatedTime,
                        showCelsius = homeState.showCelsius,
                        onToggleUnits = { homeViewModel.onEvent(HomeEvent.ToggleTemperatureUnit) },
                        onRequestPermission = {
                            if (locationPermission.isPermanentlyDenied) {
                                settingsDialogData = PermissionSettingsDialogData.Location
                            } else {
                                locationPermission.request()
                            }
                        },
                        onRetryWeather = {
                            homeViewModel.onEvent(HomeEvent.LoadCurrentLocationWeather)
                        }
                    )
                }

                // History Section
                item {
                    HistorySection(
                        imageHistory = imageHistory,
                        canNavigateToCamera = homeState.canNavigateToCamera,
                        onImageClick = { entity ->
                            fullImagePath = entity.filePath
                            showFullImage = true
                        },
                        onDeleteImage = { entity ->
                            homeViewModel.onEvent(
                                HomeEvent.DeleteImage(
                                    imageId = entity.id,
                                    filePath = entity.filePath
                                )
                            )
                        },
                        onCaptureFirst = { handleCameraNavigation() }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}