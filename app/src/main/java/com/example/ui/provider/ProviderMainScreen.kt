package com.example.ui.provider

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProviderAssistanceRequest
import com.example.data.model.RiskReport
import com.example.data.model.SosEvent
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralSOS
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary

@Composable
fun ProviderMainScreen(
    viewModel: ProviderViewModel,
    onSwitchToTraveler: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            ProviderBottomNavigation(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.selectTab(it) },
                activeSosCount = uiState.activeSosAlerts.size,
                activeRequestsCount = uiState.assistanceRequests.count { it.status == "pending" }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top Provider Header
            ProviderTopHeader(
                profile = uiState.profile,
                dutyStatus = uiState.dutyStatus,
                onToggleDuty = { viewModel.toggleDutyStatus() }
            )

            // Dynamic Action Feedback Banner
            AnimatedVisibility(
                visible = uiState.actionFeedbackMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = Navy800,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.actionFeedbackMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearFeedbackMessage() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("✕", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Tab Content
            when (uiState.selectedTab) {
                0 -> ProviderSosRadarTab(
                    uiState = uiState,
                    onAcknowledge = { viewModel.acknowledgeSos(it) },
                    onResolve = { viewModel.resolveSos(it) },
                    onCall = { phone -> viewModel.dialPhone(context, phone) },
                    onOpenMap = { lat, lng, label -> viewModel.openMapsCoordinates(context, lat, lng, label) },
                    onSimulateDistress = { viewModel.simulateIncomingDistress() }
                )
                1 -> ProviderAssistanceTab(
                    requests = uiState.assistanceRequests,
                    onUpdateStatus = { id, status -> viewModel.updateRequestStatus(id, status) },
                    onCall = { phone -> viewModel.dialPhone(context, phone) },
                    onOpenMap = { lat, lng, label -> viewModel.openMapsCoordinates(context, lat, lng, label) }
                )
                2 -> ProviderBroadcastAdvisoryTab(
                    uiState = uiState,
                    onTitleChange = { viewModel.onBroadcastTitleChanged(it) },
                    onDetailsChange = { viewModel.onBroadcastDetailsChanged(it) },
                    onCategoryChange = { viewModel.onBroadcastCategoryChanged(it) },
                    onSeverityChange = { viewModel.onBroadcastSeverityChanged(it) },
                    onPublish = { viewModel.publishBroadcastAdvisory() }
                )
                3 -> ProviderProfileTab(
                    uiState = uiState,
                    onSwitchToTraveler = onSwitchToTraveler,
                    onSignOut = { viewModel.logout(onSignOut) }
                )
            }
        }
    }
}

@Composable
private fun ProviderTopHeader(
    profile: com.example.data.model.UserProfile,
    dutyStatus: String,
    onToggleDuty: () -> Unit
) {
    val isOnDuty = dutyStatus == "ON_DUTY"

    // Infinite radar pulse for active duty
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Navy900,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Verified Badge Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = if (isOnDuty) listOf(TealPrimary, EmeraldSafe) else listOf(Navy700, Navy800)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (isOnDuty) Navy900 else Color.LightGray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile.name.ifBlank { "Verified Provider" },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = EmeraldSafe.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "VERIFIED",
                                color = EmeraldSafe,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "${profile.providerType} • ${profile.badgeNumber.ifBlank { "DL-8842" }}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyanAccent,
                            fontSize = 11.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Radar Live Switch
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isOnDuty) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(EmeraldSafe)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = if (isOnDuty) "ON DUTY" else "OFF DUTY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isOnDuty) EmeraldSafe else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Switch(
                        checked = isOnDuty,
                        onCheckedChange = { onToggleDuty() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EmeraldSafe,
                            checkedTrackColor = Navy700,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Navy800
                        ),
                        modifier = Modifier.scale(0.8f).testTag("duty_switch")
                    )
                }
            }
        }
    }
}

@Composable
private fun ProviderSosRadarTab(
    uiState: ProviderUiState,
    onAcknowledge: (String) -> Unit,
    onResolve: (String) -> Unit,
    onCall: (String) -> Unit,
    onOpenMap: (Double, Double, String) -> Unit,
    onSimulateDistress: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // KPI Stats Bar
        item {
            ProviderKpiCard(stats = uiState.stats, dutyStatus = uiState.dutyStatus)
        }

        // Section Title & Simulation Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = null,
                        tint = CoralSOS,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Live SOS Distress Radar (${uiState.activeSosAlerts.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                OutlinedButton(
                    onClick = onSimulateDistress,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralSOS),
                    border = BorderStroke(1.dp, CoralSOS),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("simulate_sos_button")
                ) {
                    Icon(Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simulate Beacon", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active Alerts List or Empty State
        if (uiState.activeSosAlerts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(EmeraldSafe.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldSafe,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Destination Sector Clear",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No active SOS emergencies reported in your jurisdiction right now. Radar scanning continuous satellite GPS feed.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        } else {
            items(uiState.activeSosAlerts, key = { it.eventId }) { event ->
                val isAcknowledged = uiState.acknowledgedSosIds.contains(event.eventId)
                ActiveSosDispatchCard(
                    event = event,
                    isAcknowledged = isAcknowledged,
                    onAcknowledge = { onAcknowledge(event.eventId) },
                    onResolve = { onResolve(event.eventId) },
                    onCall = {
                        val phone = event.notifiedContacts.firstOrNull { it.startsWith("+") } ?: "+91 11 2346 9526"
                        onCall(phone)
                    },
                    onOpenMap = {
                        onOpenMap(event.lat, event.lng, "Distress Beacon #${event.eventId}")
                    }
                )
            }
        }
    }
}

@Composable
private fun ActiveSosDispatchCard(
    event: SosEvent,
    isAcknowledged: Boolean,
    onAcknowledge: () -> Unit,
    onResolve: () -> Unit,
    onCall: () -> Unit,
    onOpenMap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sos_card_${event.eventId}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, if (isAcknowledged) CyanAccent else CoralSOS),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isAcknowledged) CyanAccent.copy(alpha = 0.15f) else CoralSOS.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isAcknowledged) CyanAccent else CoralSOS)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAcknowledged) "UNIT DISPATCHED" else "CRITICAL DISTRESS",
                            color = if (isAcknowledged) CyanAccent else CoralSOS,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }
                }

                Text(
                    text = "ID: #${event.eventId.takeLast(6)}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Traveler & Location Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = CoralSOS,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "GPS Coordinates: ${String.format("%.4f", event.lat)}, ${String.format("%.4f", event.lng)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Accuracy: ±${event.accuracyMeters.toInt()}m • Destination Central Zone",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            if (event.notifiedContacts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Contacts / Escort: ${event.notifiedContacts.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Quick Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCall,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldSafe)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onOpenMap,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyanAccent)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Navigate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                if (!isAcknowledged) {
                    Button(
                        onClick = onAcknowledge,
                        modifier = Modifier.weight(1.4f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CoralSOS, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dispatch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onResolve,
                        modifier = Modifier.weight(1.4f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSafe, contentColor = Navy900),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Resolve", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderKpiCard(stats: com.example.data.model.ProviderDutyStats, dutyStatus: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Navy800)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            KpiItem(
                title = "Active SOS",
                value = "${stats.activeSosCount}",
                highlightColor = if (stats.activeSosCount > 0) CoralSOS else EmeraldSafe
            )
            KpiItem(
                title = "Dispatched",
                value = "${stats.activeDispatches}",
                highlightColor = CyanAccent
            )
            KpiItem(
                title = "Resolved",
                value = "${stats.resolvedToday}",
                highlightColor = EmeraldSafe
            )
            KpiItem(
                title = "Trust Rating",
                value = "${stats.satisfactionScore} ★",
                highlightColor = AmberWarning
            )
        }
    }
}

@Composable
private fun KpiItem(title: String, value: String, highlightColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = highlightColor
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.LightGray,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun ProviderAssistanceTab(
    requests: List<ProviderAssistanceRequest>,
    onUpdateStatus: (String, String) -> Unit,
    onCall: (String) -> Unit,
    onOpenMap: (Double, Double, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Tourist Assistance & Booking Queue (${requests.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Verified tourist requests for escorts, safe transport pickups, and language translation.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        items(requests, key = { it.id }) { req ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("assistance_card_${req.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = when (req.status) {
                                "pending" -> AmberWarning.copy(alpha = 0.15f)
                                "dispatched" -> CyanAccent.copy(alpha = 0.15f)
                                else -> EmeraldSafe.copy(alpha = 0.15f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = req.status.uppercase(),
                                color = when (req.status) {
                                    "pending" -> AmberWarning
                                    "dispatched" -> CyanAccent
                                    else -> EmeraldSafe
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = req.requestedAt,
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = req.travelerName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${req.serviceType} • ${req.destinationLocality}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TealPrimary, fontWeight = FontWeight.Medium)
                    )

                    if (req.details.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = req.details,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onCall(req.travelerPhone) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldSafe)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onOpenMap(req.lat, req.lng, req.destinationLocality) },
                            modifier = Modifier.weight(1.1f),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyanAccent)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Map", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (req.status == "pending") {
                            Button(
                                onClick = { onUpdateStatus(req.id, "dispatched") },
                                modifier = Modifier.weight(1.4f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                            ) {
                                Text("Accept & Go", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (req.status == "dispatched") {
                            Button(
                                onClick = { onUpdateStatus(req.id, "completed") },
                                modifier = Modifier.weight(1.4f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSafe, contentColor = Navy900)
                            ) {
                                Text("Complete", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProviderBroadcastAdvisoryTab(
    uiState: ProviderUiState,
    onTitleChange: (String) -> Unit,
    onDetailsChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSeverityChange: (String) -> Unit,
    onPublish: () -> Unit
) {
    val categories = listOf("Scam Warning", "Traffic & Road Blockage", "Heavy Weather", "Curfew / Area Restriction", "Festive Crowd Notice")
    val severities = listOf("low" to "Low (Informational)", "medium" to "Medium (Caution)", "high" to "High (Urgent Warning)")
    var expandedCategory by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Broadcast Destination Advisory",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text(
                    text = "Publish official safety alerts directly to the public live stream for all travelers visiting your jurisdiction.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.broadcastCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Alert Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    onCategoryChange(cat)
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.broadcastTitle,
                    onValueChange = onTitleChange,
                    label = { Text("Advisory Headline") },
                    placeholder = { Text("e.g. Touting alert near New Delhi Railway Station") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.broadcastDetails,
                    onValueChange = onDetailsChange,
                    label = { Text("Advisory Details & Guidance") },
                    placeholder = { Text("Explain the situation and safe alternative routes or precautions for tourists.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Severity Level",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    severities.forEach { (code, label) ->
                        val isSelected = uiState.broadcastSeverity == code
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSeverityChange(code) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) {
                                when (code) {
                                    "high" -> CoralSOS
                                    "medium" -> AmberWarning
                                    else -> TealPrimary
                                }
                            } else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = code.uppercase(),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onPublish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("publish_advisory_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSafe, contentColor = Navy900),
                    enabled = !uiState.isPublishing && uiState.broadcastTitle.isNotBlank() && uiState.broadcastDetails.isNotBlank()
                ) {
                    if (uiState.isPublishing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Navy900)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Broadcast to Destination Feed", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderProfileTab(
    uiState: ProviderUiState,
    onSwitchToTraveler: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Official Credentials Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Navy800)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null,
                        tint = EmeraldSafe,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Official Service Credentials",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "Ministry of Tourism & City Police Verified ID",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyanAccent)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Navy700)
                Spacer(modifier = Modifier.height(14.dp))

                ProfileItemRow("Provider Name", uiState.profile.name)
                ProfileItemRow("Service Department", uiState.profile.providerType)
                ProfileItemRow("Agency / Unit", uiState.profile.agencyName.ifBlank { "Tourist Safety Wing" })
                ProfileItemRow("Badge / License", uiState.profile.badgeNumber.ifBlank { "DL-TP-8842" })
                ProfileItemRow("ID Proof Document", uiState.profile.idProofType.ifBlank { "Government Warrant / Accreditation Card" })
                ProfileItemRow("ID Proof #", uiState.profile.idProofNumber.ifBlank { "IND-POL-DL-8842-TP" })
                ProfileItemRow("Issuing Authority", uiState.profile.issuingAuthority.ifBlank { "Delhi Police / Ministry of Tourism" })
                ProfileItemRow("Official Rank / Role", uiState.profile.designationRank.ifBlank { "Inspector / Field Lead" })
                ProfileItemRow("Service Jurisdiction", uiState.profile.serviceArea.ifBlank { "Central Tourist Zone" })
                ProfileItemRow("Contact Phone", uiState.profile.phone.ifBlank { "+91 11 2346 9526" })
                ProfileItemRow("Attached ID Scan", uiState.profile.idProofDocumentName.ifBlank { "official_police_id_scan.pdf" })
                ProfileItemRow("Trust Rating", "${uiState.profile.trustScore} ★ (${uiState.profile.ratingCount} reviews)")
            }
        }

        // Portal Switcher Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "Portal Modes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Switch between the Verified Destination Provider Dispatch and the Traveler Tourist view.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )

                OutlinedButton(
                    onClick = onSwitchToTraveler,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("switch_to_traveler_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Switch to Traveler Portal View", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("provider_signout_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralSOS),
                    border = BorderStroke(1.dp, CoralSOS.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out of Provider Portal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ProfileItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
        )
    }
}

@Composable
private fun ProviderBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    activeSosCount: Int,
    activeRequestsCount: Int
) {
    NavigationBar(
        containerColor = Navy900,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Box {
                    Icon(Icons.Default.Radar, contentDescription = "SOS Radar")
                    if (activeSosCount > 0) {
                        Surface(
                            color = CoralSOS,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Text(
                                text = "$activeSosCount",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            label = { Text("SOS Radar", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CoralSOS,
                selectedTextColor = Color.White,
                indicatorColor = CoralSOS.copy(alpha = 0.2f),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("nav_sos_radar")
        )

        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Box {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = "Assistance Queue")
                    if (activeRequestsCount > 0) {
                        Surface(
                            color = AmberWarning,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Text(
                                text = "$activeRequestsCount",
                                color = Navy900,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            label = { Text("Assistance", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TealPrimary,
                selectedTextColor = Color.White,
                indicatorColor = TealPrimary.copy(alpha = 0.2f),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("nav_assistance_queue")
        )

        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Icon(Icons.Default.Campaign, contentDescription = "Advisory Broadcast")
            },
            label = { Text("Advisory", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldSafe,
                selectedTextColor = Color.White,
                indicatorColor = EmeraldSafe.copy(alpha = 0.2f),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("nav_broadcast_advisory")
        )

        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(Icons.Default.Badge, contentDescription = "Duty Credentials")
            },
            label = { Text("Credentials", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyanAccent,
                selectedTextColor = Color.White,
                indicatorColor = CyanAccent.copy(alpha = 0.2f),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("nav_duty_credentials")
        )
    }
}
