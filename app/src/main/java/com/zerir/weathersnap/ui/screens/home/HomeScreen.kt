package com.zerir.weathersnap.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.zerir.weathersnap.data.entity.CapturedImageEntity
import com.zerir.weathersnap.domain.model.getDataOrNull
import com.zerir.weathersnap.ui.components.DeleteDialog
import com.zerir.weathersnap.ui.components.EmptyHistoryState
import com.zerir.weathersnap.ui.components.ErrorItem
import com.zerir.weathersnap.ui.components.FullImageView
import com.zerir.weathersnap.ui.components.ImageHistoryCard
import com.zerir.weathersnap.ui.components.ImageHistoryCardPlaceholder
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
            sharedDataViewModel.setWeatherData(weather, coordinates, homeState.defaultCelsius)
        }
    }

    // Full image viewer state
    var showFullImage by remember { mutableStateOf(false) }
    var fullImagePath by remember { mutableStateOf<String?>(null) }

    var settingsDialogData by remember { mutableStateOf<PermissionSettingsDialogData?>(null) }
    var deleteDialogData by remember { mutableStateOf<CapturedImageEntity?>(null) }

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

    DeleteDialog(
        showDialog = deleteDialogData != null,
        entity = deleteDialogData,
        onDismiss = { deleteDialogData = null },
        onDelete = { entity ->
            deleteDialogData = null
            homeViewModel.onEvent(
                HomeEvent.DeleteImage(
                    imageId = entity.id,
                    filePath = entity.filePath
                )
            )
        }
    )

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Weather Snap") },
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
                    bottom = 0.dp,
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                )
            ) {
                HomeContentList(
                    imageHistory = imageHistory,
                    homeState = homeState,
                    onRequestLocationPermission = {
                        if(locationPermission.isPermanentlyDenied) {
                            settingsDialogData = PermissionSettingsDialogData.Location
                        } else {
                            locationPermission.request()
                        }
                    },
                    onImageClick = { entity ->
                        fullImagePath = entity.filePath
                        showFullImage = true
                    },
                    onDeleteImage = { entity ->
                        deleteDialogData = entity
                    },
                    onCaptureFirst = { handleCameraNavigation() },
                    onToggleUnits = { homeViewModel.onEvent(HomeEvent.ToggleTemperatureUnit) },
                    onRetryWeather = { homeViewModel.onEvent(HomeEvent.LoadCurrentLocationWeather) }
                )
            }
        }

        // Full image overlay
        if (showFullImage && fullImagePath != null) {
            FullImageView(
                imagePath = fullImagePath,
                onClose = { showFullImage = false }
            )
        }
    }
}

@Composable
private fun HomeContentList(
    imageHistory: LazyPagingItems<CapturedImageEntity>,
    homeState: HomeState,
    onRequestLocationPermission: () -> Unit,
    onImageClick: (CapturedImageEntity) -> Unit,
    onDeleteImage: (CapturedImageEntity) -> Unit,
    onCaptureFirst: () -> Unit,
    onToggleUnits: () -> Unit,
    onRetryWeather: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Weather Section Header - spans full width
        item(
            key = "weather_section",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Column {
                Spacer(modifier = Modifier.height(2.dp))
                WeatherSection(
                    locationState = homeState.locationState,
                    weatherState = homeState.weatherState,
                    hasLocationPermission = homeState.hasLocationPermission,
                    lastUpdated = homeState.lastUpdatedTime,
                    showCelsius = homeState.showCelsius,
                    onToggleUnits = onToggleUnits,
                    onRequestPermission = onRequestLocationPermission,
                    onRetryWeather = onRetryWeather
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // History Section Header
        if (imageHistory.itemCount > 0 || imageHistory.loadState.refresh is LoadState.Loading) {
            item(
                key = "history_header",
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Text(
                    text = "Weather History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // Empty state for images
        if (imageHistory.loadState.refresh is LoadState.NotLoading && imageHistory.itemCount == 0) {
            item(
                key = "empty_state",
                span = { GridItemSpan(maxLineSpan) }
            ) {
                EmptyHistoryState(
                    showCaptureButton = homeState.canNavigateToCamera,
                    onCapturePhotoClick = onCaptureFirst
                )
            }
        }

        // Loading state
        if (imageHistory.loadState.refresh is LoadState.Loading && imageHistory.itemCount == 0) {
            item(
                key = "loading_state",
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Error state
        if (imageHistory.loadState.refresh is LoadState.Error) {
            item(
                key = "error_state",
                span = { GridItemSpan(maxLineSpan) }
            ) {
                val error = imageHistory.loadState.refresh as LoadState.Error
                ErrorItem(
                    message = error.error.localizedMessage ?: "Failed to load images",
                    onRetry = { imageHistory.refresh() }
                )
            }
        }

        // Image grid items - each takes 1 column
        items(
            count = imageHistory.itemCount,
            key = imageHistory.itemKey { it.id }
        ) { index ->
            val item = imageHistory[index]
            if (item != null) {
                ImageHistoryCard(
                    imageEntity = item,
                    onImageClick = onImageClick,
                    onDeleteClick = { onDeleteImage(item) },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Placeholder for loading items
                ImageHistoryCardPlaceholder()
            }
        }

        // Append loading state
        when (val loadState = imageHistory.loadState.append) {
            is LoadState.Loading -> {
                item(
                    key = "append_loading",
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
            is LoadState.Error -> {
                item(
                    key = "append_error",
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    ErrorItem(
                        message = loadState.error.localizedMessage ?: "Loading failed",
                        onRetry = { imageHistory.retry() }
                    )
                }
            }
            else -> {}
        }

        // Bottom spacer
        item(
            key = "bottom_spacer",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
