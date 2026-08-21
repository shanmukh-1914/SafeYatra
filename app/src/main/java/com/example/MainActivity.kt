package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.LocationTrackingManager
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.AuthViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.profile.ProfileScreen
import com.example.ui.profile.ProfileViewModel
import com.example.ui.provider.ProviderMainScreen
import com.example.ui.provider.ProviderViewModel
import com.example.ui.safety.SafetyScreen
import com.example.ui.safety.SafetyViewModel
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary
import com.example.ui.trips.TripsScreen
import com.example.ui.trips.TripsViewModel

enum class MainTab(val title: String, val icon: ImageVector, val tag: String) {
    HOME("Home", Icons.Default.Home, "nav_tab_home"),
    TRIPS("Trips", Icons.Default.FlightTakeoff, "nav_tab_trips"),
    SAFETY("Safety Hub", Icons.Default.Security, "nav_tab_safety"),
    PROFILE("Profile", Icons.Default.Person, "nav_tab_profile")
}

class MainActivity : ComponentActivity() {

    private lateinit var locationTrackingManager: LocationTrackingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        com.example.data.repository.UserLocalDatabaseRepository.initialize(applicationContext)
        locationTrackingManager = LocationTrackingManager(applicationContext)

        setContent {
            MyApplicationTheme {
                SafeYatraApp(locationTrackingManager = locationTrackingManager)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationTrackingManager.stopTracking()
    }
}

@Composable
fun SafeYatraApp(
    locationTrackingManager: LocationTrackingManager,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var activePortalViewMode by remember { mutableStateOf("traveler") }

    // Sync activePortalViewMode when user auth state or role updates
    LaunchedEffect(authState.isUserFullyAuthenticated, authState.userRole) {
        if (authState.isUserFullyAuthenticated) {
            activePortalViewMode = if (authState.userRole == "provider") "provider" else "traveler"
        }
    }

    // Runtime Permission Request Flow
    val permissionsToRequest = remember {
        val list = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        list.toTypedArray()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val fineGranted = permissionsMap[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissionsMap[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            locationTrackingManager.startTracking()
        }
    }

    // Check location permission on start
    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasFine) {
            locationTrackingManager.startTracking()
        } else {
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    if (!authState.isUserFullyAuthenticated) {
        AuthScreen(
            viewModel = authViewModel,
            onAuthenticated = {
                locationTrackingManager.startTracking()
                activePortalViewMode = if (authState.userRole == "provider") "provider" else "traveler"
            }
        )
    } else {
        if (activePortalViewMode == "provider") {
            val providerViewModel: ProviderViewModel = viewModel()
            ProviderMainScreen(
                viewModel = providerViewModel,
                onSwitchToTraveler = {
                    activePortalViewMode = "traveler"
                },
                onSignOut = {
                    locationTrackingManager.stopTracking()
                    authViewModel.checkInitialAuthState()
                }
            )
        } else {
            MainScreenWithTabs(
                locationTrackingManager = locationTrackingManager,
                onRequestLocationPermission = {
                    permissionLauncher.launch(permissionsToRequest)
                },
                onSignedOut = {
                    locationTrackingManager.stopTracking()
                    authViewModel.checkInitialAuthState()
                },
                onSwitchToProvider = {
                    activePortalViewMode = "provider"
                }
            )
        }
    }
}

@Composable
fun MainScreenWithTabs(
    locationTrackingManager: LocationTrackingManager,
    onRequestLocationPermission: () -> Unit,
    onSignedOut: () -> Unit,
    onSwitchToProvider: (() -> Unit)? = null
) {
    var currentTab by remember { mutableStateOf(MainTab.HOME) }

    val homeViewModel = remember {
        HomeViewModel(locationTracker = locationTrackingManager)
    }
    val tripsViewModel = remember {
        TripsViewModel()
    }
    val safetyViewModel = remember {
        SafetyViewModel(locationTracker = locationTrackingManager)
    }
    val profileViewModel = remember {
        ProfileViewModel()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Navy900,
                contentColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                MainTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Navy900,
                            selectedTextColor = CyanAccent,
                            indicatorColor = CyanAccent,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.padding(innerPadding)
        ) { targetTab ->
            when (targetTab) {
                MainTab.HOME -> HomeScreen(
                    viewModel = homeViewModel,
                    onRequestLocationPermission = onRequestLocationPermission,
                    onNavigateToTrips = { currentTab = MainTab.TRIPS },
                    onNavigateToSafety = { currentTab = MainTab.SAFETY }
                )
                MainTab.TRIPS -> TripsScreen(
                    viewModel = tripsViewModel
                )
                MainTab.SAFETY -> SafetyScreen(
                    viewModel = safetyViewModel
                )
                MainTab.PROFILE -> ProfileScreen(
                    viewModel = profileViewModel,
                    onSignedOut = onSignedOut,
                    onSwitchToProvider = onSwitchToProvider
                )
            }
        }
    }
}

