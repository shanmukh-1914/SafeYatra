package com.example.ui.home

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RiskReport
import com.example.data.model.SafetyAlert
import com.example.data.model.SosEvent
import com.example.data.model.Trip
import com.example.data.model.VerifiedProvider
import com.example.data.repository.LocationTrackingManager
import com.example.ui.components.AreaSafetyDetailsSection
import com.example.ui.components.GuardianLiveRadarDialog
import com.example.ui.components.LiveInteractiveMap
import com.example.ui.components.LiveLocationIndicator
import com.example.ui.components.LiveWeatherCard
import com.example.ui.components.LocationSearchDialog
import com.example.ui.components.NearbyAttractionsSection
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralSOS
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onRequestLocationPermission: () -> Unit,
    onNavigateToTrips: () -> Unit,
    onNavigateToSafety: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val locationState by viewModel.locationState.collectAsState()
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("home_screen"),
            contentPadding = PaddingValues(bottom = 95.dp)
        ) {
            // Hero Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Navy900, Navy800)
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Namaste, ${uiState.userProfile?.name?.ifBlank { "Traveler" } ?: "Traveler"}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = EmeraldSafe,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Home: ${uiState.userProfile?.homeCountry?.ifBlank { "Verified Traveler" } ?: "Verified Traveler"}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    )
                                }
                            }

                            // Emergency Contacts Counter Badge
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = TealPrimaryDark,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { onNavigateToSafety() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${uiState.emergencyContacts.size} Guardians",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Real-Time Location Indicator
                        LiveLocationIndicator(
                            state = locationState,
                            onRequestPermission = onRequestLocationPermission,
                            onOpenLocationPicker = { viewModel.openLocationPicker() },
                            onAutoDetect = { viewModel.autoDetectLiveLocation() }
                        )
                    }
                }
            }

            // Notification / Auto Detection Banner
            if (uiState.autoTripDetectedMessage != null || uiState.sosSuccessMessage != null || uiState.errorMessage != null) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            uiState.errorMessage != null -> CoralSOS.copy(alpha = 0.15f)
                            uiState.autoTripDetectedMessage != null -> EmeraldSafe.copy(alpha = 0.15f)
                            else -> TealPrimary.copy(alpha = 0.15f)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = uiState.autoTripDetectedMessage
                                    ?: uiState.sosSuccessMessage
                                    ?: uiState.errorMessage.orEmpty(),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = when {
                                        uiState.errorMessage != null -> CoralSOS
                                        uiState.autoTripDetectedMessage != null -> EmeraldSafe
                                        else -> TealPrimary
                                    }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearNotifications() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================
            // ACTIVE SOS EVENT LIVE CARD (Real-time 15s streaming interval)
            // =========================================================
            if (uiState.activeSosEvent != null) {
                val sos = uiState.activeSosEvent!!
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .testTag("active_sos_banner"),
                        shape = RoundedCornerShape(18.dp),
                        color = CoralSOS,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = "Active SOS",
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "EMERGENCY SOS ACTIVE",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "Broadcasting live coordinates every 15-20s",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White.copy(alpha = 0.9f)
                                            )
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "LIVE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Black
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Live Coordinates Box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.2f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Current GPS Beacon:",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        )
                                        Text(
                                            text = "Lat: ${String.format("%.5f", sos.lat)} • Lng: ${String.format("%.5f", sos.lng)}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    if (sos.notifiedContacts.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Notified ${sos.notifiedContacts.size} Guardians & SafeYatra Emergency Dispatch",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action Buttons: Observer Radar & Share Link
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.sendDirectGuardianSms(context) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = CoralSOS
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.1f)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("SMS Guardians", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Button(
                                    onClick = { viewModel.openGuardianRadarDialog() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.25f),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Radar View", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Button(
                                    onClick = {
                                        viewModel.shareSosLink(context, sos.eventId, sos.lat, sos.lng)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.25f),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(0.9f)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Resolve SOS Button
                            Button(
                                onClick = { viewModel.resolveActiveSos(sos.eventId) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.9f),
                                    contentColor = CoralSOS
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("resolve_sos_button")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "I Am Safe • Resolve & Stop SOS",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Rapid Emergency SOS Button (when SOS is not currently active)
            if (uiState.activeSosEvent == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { viewModel.triggerEmergencySos() }
                                .testTag("rapid_sos_card"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = CoralSOS),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = "SOS",
                                            tint = Color.White,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "1-Tap Emergency SOS",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "Broadcasts live GPS every 12-15s to ${uiState.emergencyContacts.size} guardians & police",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color.White.copy(alpha = 0.9f)
                                            )
                                        )
                                    }
                                }

                                if (uiState.isTriggeringSos) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(26.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Guardian Setup Reminder if no contacts added yet
            if (uiState.emergencyContacts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { onNavigateToSafety() }
                            .testTag("setup_guardians_banner"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(TealPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Add Emergency Guardians",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Get live GPS SMS dispatch during SOS",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = onNavigateToSafety,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // =========================================================
            // LIVE INTERACTIVE MAP OF USER LOCATION & NEARBY PLACES
            // =========================================================
            item {
                LiveInteractiveMap(
                    locationState = locationState,
                    nearbyPlaces = uiState.nearbyPlaces,
                    nearbyAttractions = uiState.nearbyAttractions,
                    verifiedProviders = uiState.nearbyProviders,
                    riskReports = uiState.nearbyRiskReports,
                    onAttractionClick = { attraction ->
                        // Interactive marker selected
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // =========================================================
            // LIVE POLICE, HOSPITALS & ATTRACTIONS (Based on Location)
            // =========================================================
            if (uiState.nearbyPlaces.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Nearby Safety & Essentials (Live GPS)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CyanAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${uiState.nearbyPlaces.size} FOUND",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyanAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Category filter chips
                        val placeCategories = listOf("All", "Police", "Hospitals", "Attractions", "Cabs", "Food")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(bottom = 6.dp)
                        ) {
                            items(placeCategories) { cat ->
                                val isSelected = uiState.placesCategoryFilter == cat
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) TealPrimary else Navy800,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable { viewModel.setPlacesCategoryFilter(cat) }
                                ) {
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        val filteredPlaces = viewModel.getFilteredNearbyPlaces()
                        if (filteredPlaces.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Navy800.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = "No places matching category filter in this radius.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.6f)),
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(vertical = 2.dp)
                            ) {
                                items(filteredPlaces) { place ->
                                Card(
                                    modifier = Modifier
                                        .width(220.dp)
                                        .clickable {
                                            viewModel.getPlaceDirections(context, place)
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Navy800),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = when {
                                                    place.category.contains("Police", true) -> EmeraldSafe.copy(alpha = 0.2f)
                                                    place.category.contains("Hospital", true) || place.category.contains("Medical", true) -> CoralSOS.copy(alpha = 0.2f)
                                                    else -> CyanAccent.copy(alpha = 0.2f)
                                                }
                                            ) {
                                                Text(
                                                    text = place.category,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = when {
                                                            place.category.contains("Police", true) -> EmeraldSafe
                                                            place.category.contains("Hospital", true) || place.category.contains("Medical", true) -> CoralSOS
                                                            else -> CyanAccent
                                                        },
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Text(
                                                text = "${place.distanceKm} km",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color.White.copy(alpha = 0.7f),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = place.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = place.address.ifBlank { place.openStatus },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White.copy(alpha = 0.65f),
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Directions in Maps →",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = TealPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            )

                                            if (place.phone.isNotBlank()) {
                                                IconButton(
                                                    onClick = { viewModel.callProvider(context, place.phone) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Call,
                                                        contentDescription = "Call",
                                                        tint = EmeraldSafe,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    }
                }
            }

            // =========================================================
            // LIVE REAL-TIME WEATHER & COMFORT INDEX CARD
            // =========================================================
            item {
                LiveWeatherCard(
                    weather = uiState.liveWeather,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // =========================================================
            // NEARBY ATTRACTIONS & LOCAL HIGHLIGHTS
            // =========================================================
            item {
                NearbyAttractionsSection(
                    attractions = uiState.nearbyAttractions,
                    onAttractionSelected = { attraction ->
                        // Attraction card selected
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // =========================================================
            // AREA SAFETY & LOCAL DETAIL GUIDE
            // =========================================================
            item {
                AreaSafetyDetailsSection(
                    areaDetails = uiState.areaDetails,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // =========================================================
            // ON TRIP MODE DYNAMIC SECTION (Automatic Geofencing Activated)
            // =========================================================
            if (uiState.isOnTripMode && uiState.activeTrip != null) {
                val trip = uiState.activeTrip!!
                item {
                    OnTripModeBanner(
                        trip = trip,
                        distanceKm = uiState.distanceToDestinationKm,
                        onOpenItinerary = onNavigateToTrips
                    )
                }

                // Nearby Itinerary Stops with Real-Time Proximity Badges
                if (uiState.nearbyItineraryActivities.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Explore,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Nearby Itinerary Stops",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                TextButton(onClick = onNavigateToTrips) {
                                    Text("Full Itinerary", color = TealPrimary, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(uiState.nearbyItineraryActivities.take(5)) { stop ->
                                    NearbyActivityCard(
                                        dayNumber = stop.dayNumber,
                                        activity = stop.activity,
                                        distanceKm = stop.distanceKm
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (uiState.activeTrip != null) {
                // BEFORE YOU GO (PLANNING MODE)
                val trip = uiState.activeTrip!!
                item {
                    BeforeYouGoCard(
                        trip = trip,
                        distanceKm = uiState.distanceToDestinationKm,
                        nearbyRiskReports = uiState.destinationRiskReports,
                        onOpenItinerary = onNavigateToTrips
                    )
                }
            } else {
                // NO TRIPS YET
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigateToTrips() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "No Active Trip Registered",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Plan your destination to enable automatic 5km on-trip detection & AI safety radar.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = onNavigateToTrips,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Plan Trip")
                            }
                        }
                    }
                }
            }

            // =========================================================
            // VERIFIED LOCAL HELP (Real-time Snapshot Listener)
            // =========================================================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldSafe,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isOnTripMode) "Nearby Verified Services (Live)" else "Verified Safety & Support Services",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldSafe.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "ON SNAPSHOT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldSafe,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            items(uiState.nearbyProviders) { provider ->
                val dist = if (locationState.isTracking) {
                    LocationTrackingManager.calculateDistanceKm(
                        locationState.latitude,
                        locationState.longitude,
                        provider.lat,
                        provider.lng
                    )
                } else null

                VerifiedProviderItem(
                    provider = provider,
                    distanceKm = dist,
                    onCall = { viewModel.callProvider(context, provider.phone) }
                )
            }

            // =========================================================
            // LIVE RISK INTELLIGENCE FEED
            // =========================================================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = AmberWarning,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Community Risk Radar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        TextButton(onClick = onNavigateToSafety) {
                            Text("Report Risk", color = TealPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (uiState.nearbyRiskReports.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = "No high-risk advisories in your immediate vicinity. All clear.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(uiState.nearbyRiskReports.take(4)) { report ->
                                RiskReportCard(report = report)
                            }
                        }
                    }
                }
            }
        }

        // =========================================================
        // REAL-TIME LIVE SOS GUARDIAN RADAR DIALOG (Uses onSnapshot)
        // =========================================================
        if ((uiState.showSosObserverDialog || uiState.showGuardianRadarDialog) && uiState.activeSosEvent != null) {
            val sos = uiState.activeSosEvent!!
            val travelerName = uiState.userProfile?.name?.ifBlank { "Traveler" } ?: "SafeYatra Traveler"
            GuardianLiveRadarDialog(
                sosEvent = sos,
                emergencyContacts = uiState.emergencyContacts,
                travelerName = travelerName,
                onDismiss = {
                    viewModel.closeSosObserverDialog()
                    viewModel.closeGuardianRadarDialog()
                },
                onResolveSos = {
                    viewModel.resolveActiveSos(sos.eventId)
                }
            )
        }

        // =========================================================
        // EXACT LOCATION CALIBRATION & SEARCH DIALOG
        // =========================================================
        if (uiState.showLocationPickerModal) {
            LocationSearchDialog(
                currentLocationState = locationState,
                searchQuery = uiState.locationSearchQuery,
                searchResults = uiState.locationSearchResults,
                isSearching = uiState.isSearchingLocation,
                isRefreshing = uiState.isRefreshingLocation,
                onQueryChange = { viewModel.onLocationSearchQueryChanged(it) },
                onSelectLocation = { viewModel.selectLocationResult(it) },
                onAutoDetect = { viewModel.autoDetectLiveLocation() },
                onDismiss = { viewModel.closeLocationPicker() }
            )
        }
    }
}

@Composable
fun OnTripModeBanner(
    trip: Trip,
    distanceKm: Double?,
    onOpenItinerary: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("on_trip_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Navy900
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Navy900, TealPrimaryDark)
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EmeraldSafe.copy(alpha = 0.25f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldSafe)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ON TRIP MODE • ACTIVE GEOFENCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldSafe,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                if (distanceKm != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${String.format("%.1f", distanceKm)} km to center",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = trip.destinationName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            )

            Text(
                text = "Dates: ${trip.startDate} - ${trip.endDate} • ${trip.itinerary.size} Day Itinerary Protected",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.85f)
                )
            )

            if (trip.generalAdvisory.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.25f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = trip.generalAdvisory,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onOpenItinerary,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = Navy900
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Explore Full Day-by-Day Itinerary & Guide", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun NearbyActivityCard(
    dayNumber: Int,
    activity: com.example.data.model.ItineraryActivity,
    distanceKm: Double?
) {
    Surface(
        modifier = Modifier
            .width(240.dp)
            .clip(RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TealPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Day $dayNumber • ${activity.time}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (distanceKm != null) {
                    Text(
                        text = "${String.format("%.1f", distanceKm)} km",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = activity.place,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BeforeYouGoCard(
    trip: Trip,
    distanceKm: Double?,
    nearbyRiskReports: List<RiskReport>,
    onOpenItinerary: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("before_you_go_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Before You Go • ${trip.destinationName}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TealPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "AI SAFETY RADAR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TealPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (trip.generalAdvisory.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = trip.generalAdvisory,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                )
            }

            // AI Generated Safety & Scam Alerts
            if (trip.destinationAlerts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Key Scam & Safety Patterns (${trip.destinationName}):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))

                trip.destinationAlerts.take(2).forEach { alert ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = when (alert.severity.lowercase()) {
                            "high" -> CoralSOS.copy(alpha = 0.08f)
                            else -> AmberWarning.copy(alpha = 0.08f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (alert.severity.lowercase() == "high") CoralSOS else AmberWarning,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = alert.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (alert.severity.lowercase() == "high") CoralSOS else AmberWarning
                                    )
                                )
                                Text(
                                    text = alert.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Cultural Etiquette snippet
            if (trip.localEtiquette.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                val firstEti = trip.localEtiquette.first()
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TealPrimary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cultural Etiquette: ${firstEti.rule}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onOpenItinerary,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Explore Full AI Itinerary & Guide")
            }
        }
    }
}

@Composable
fun LiveSosObserverModal(
    sosEvent: SosEvent,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onResolve: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onResolve,
                colors = ButtonDefaults.buttonColors(containerColor = CoralSOS)
            ) {
                Text("Resolve SOS Alert")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onShare) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share Link")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Radar, contentDescription = null, tint = CoralSOS)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Live SOS Observer Monitor", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Listening via real-time onSnapshot listener. Live position marker moves every 15-20s interval.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Navy900,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Radar rings representation
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(CyanAccent.copy(alpha = 0.15f))
                        )
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(CyanAccent.copy(alpha = 0.25f))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = CoralSOS,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "${String.format("%.4f", sosEvent.lat)}, ${String.format("%.4f", sosEvent.lng)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Status: ${sosEvent.status.uppercase()} • Notified: ${sosEvent.notifiedContacts.size} Guardians",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CoralSOS
                    )
                )
            }
        }
    )
}

@Composable
fun RiskReportCard(report: RiskReport) {
    Surface(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (report.riskType) {
                        "Scam Alert" -> CoralSOS.copy(alpha = 0.15f)
                        "Road Hazard" -> AmberWarning.copy(alpha = 0.15f)
                        else -> TealPrimary.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = report.riskType,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (report.riskType) {
                                "Scam Alert" -> CoralSOS
                                "Road Hazard" -> AmberWarning
                                else -> TealPrimary
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "Live Snapshot",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun VerifiedProviderItem(
    provider: VerifiedProvider,
    distanceKm: Double?,
    onCall: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            when (provider.type) {
                                "Tourist Police" -> Navy800
                                "Emergency Medical" -> CoralSOS.copy(alpha = 0.85f)
                                "Safe Transport" -> TealPrimary
                                else -> EmeraldSafe
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (provider.type) {
                            "Tourist Police" -> Icons.Default.LocalPolice
                            "Emergency Medical" -> Icons.Default.LocalHospital
                            "Safe Transport" -> Icons.Default.LocalTaxi
                            else -> Icons.Default.Shield
                        },
                        contentDescription = provider.type,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = provider.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = EmeraldSafe,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = provider.serviceArea,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${provider.trustScore} (${provider.totalRatings})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )

                        if (distanceKm != null) {
                            Text(
                                text = " • ${String.format("%.1f", distanceKm)} km away",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onCall,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(TealPrimary.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call Provider",
                    tint = TealPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
