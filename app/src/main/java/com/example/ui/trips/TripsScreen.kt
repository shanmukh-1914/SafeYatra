package com.example.ui.trips

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Attraction
import com.example.data.model.ItineraryDay
import com.example.data.model.LocalEtiquette
import com.example.data.model.SafetyAlert
import com.example.data.model.Trip
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralSOS
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TripsScreen(
    viewModel: TripsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Required interest categories
    val interestOptions = listOf(
        Pair("culture", "Culture & Heritage"),
        Pair("food", "Food & Dining"),
        Pair("adventure", "Adventure & Treks"),
        Pair("nature", "Nature & Wildlife"),
        Pair("shopping", "Shopping & Bazaars"),
        Pair("nightlife", "Nightlife & Evening"),
        Pair("relaxation", "Relaxation & Wellness")
    )

    if (uiState.selectedTripForDetail != null) {
        TripDetailScreen(
            trip = uiState.selectedTripForDetail!!,
            onBack = { viewModel.selectTripForDetail(null) },
            onActivate = { viewModel.updateStatus(uiState.selectedTripForDetail!!.tripId, "active") },
            onComplete = { viewModel.updateStatus(uiState.selectedTripForDetail!!.tripId, "completed") },
            onDelete = { viewModel.deleteTrip(uiState.selectedTripForDetail!!.tripId) },
            modifier = modifier
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateDialog() },
                containerColor = TealPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("add_trip_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Trip")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("trips_screen"),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header Banner
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI Trip Planner",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                                Text(
                                    text = "Gemini-powered itinerary with localized scam and safety radar",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CyanAccent.copy(alpha = 0.9f)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Filter Chips Row
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val filters = listOf("ALL", "ACTIVE", "PLANNING", "COMPLETED")
                            items(filters) { f ->
                                val isSelected = uiState.filterStatus == f
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable { viewModel.setFilter(f) },
                                    color = if (isSelected) CyanAccent else Navy900.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = f,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Navy900 else Color.White
                                        ),
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val filteredTrips = uiState.trips.filter {
                when (uiState.filterStatus) {
                    "ACTIVE" -> it.status == "active"
                    "PLANNING" -> it.status == "planning"
                    "COMPLETED" -> it.status == "completed"
                    else -> true
                }
            }

            if (uiState.isInitialLoading) {
                items(3) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .height(20.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                                )
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
                            )
                        }
                    }
                }
            } else if (filteredTrips.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(TealPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No trips in this category",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Tap + below to generate a new custom AI safety itinerary for any destination.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                items(filteredTrips) { trip ->
                    TripSummaryCard(
                        trip = trip,
                        onClick = { viewModel.selectTripForDetail(trip) },
                        onActivate = { viewModel.updateStatus(trip.tripId, "active") },
                        onComplete = { viewModel.updateStatus(trip.tripId, "completed") },
                        onDelete = { viewModel.deleteTrip(trip.tripId) }
                    )
                }
            }
        }

        // Add Trip Modal Dialog
        if (uiState.isCreatingTrip) {
            AlertDialog(
                onDismissRequest = {
                    if (!uiState.isLoading) viewModel.closeCreateDialog()
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Trip Planner", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Enter your travel destination and interests. SafeYatra's Gemini AI will craft a custom day-by-day safety itinerary.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = uiState.destinationName,
                            onValueChange = { viewModel.onDestinationChanged(it) },
                            label = { Text("Destination City / Country") },
                            placeholder = { Text("e.g. Jaipur, Goa, Tokyo, Paris") },
                            leadingIcon = {
                                Icon(Icons.Default.Place, contentDescription = null, tint = TealPrimary)
                            },
                            singleLine = true,
                            enabled = !uiState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("trip_destination_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = uiState.startDate,
                                onValueChange = { viewModel.onDatesChanged(it, uiState.endDate) },
                                label = { Text("Start Date") },
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = uiState.endDate,
                                onValueChange = { viewModel.onDatesChanged(uiState.startDate, it) },
                                label = { Text("End Date") },
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Select Traveler Interests:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            interestOptions.forEach { (key, label) ->
                                val isSelected = uiState.selectedInterests.contains(key)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { if (!uiState.isLoading) viewModel.toggleInterest(key) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null
                                )
                            }
                        }

                        if (uiState.isLoading) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Navy900.copy(alpha = 0.1f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = TealPrimary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = uiState.loadingStep.ifBlank { "Generating AI safety itinerary..." },
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                    )
                                }
                            }
                        }

                        if (uiState.errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.errorMessage ?: "",
                                color = CoralSOS,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.createTrip() },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        enabled = !uiState.isLoading && uiState.destinationName.isNotBlank(),
                        modifier = Modifier.testTag("save_trip_button")
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate Itinerary")
                            }
                        }
                    }
                },
                dismissButton = {
                    if (!uiState.isLoading) {
                        TextButton(onClick = { viewModel.closeCreateDialog() }) {
                            Text("Cancel")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun TripSummaryCard(
    trip: Trip,
    onClick: () -> Unit,
    onActivate: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("trip_item_${trip.tripId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FlightTakeoff,
                        contentDescription = null,
                        tint = TealPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = trip.destinationName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (trip.status) {
                        "active" -> EmeraldSafe.copy(alpha = 0.2f)
                        "completed" -> MaterialTheme.colorScheme.surfaceVariant
                        else -> CyanAccent.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = trip.status.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (trip.status) {
                                "active" -> EmeraldSafe
                                "completed" -> MaterialTheme.colorScheme.onSurfaceVariant
                                else -> TealPrimary
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dates: ${trip.startDate} - ${trip.endDate} • ${trip.itinerary.size} Planned Days",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            if (trip.destinationLat != 0.0) {
                Text(
                    text = "Geotagged: ${String.format("%.4f", trip.destinationLat)}, ${String.format("%.4f", trip.destinationLng)}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                )
            }

            if (trip.interests.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(trip.interests) { tag ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = tag.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary.copy(alpha = 0.12f), contentColor = TealPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View AI Itinerary", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (trip.status != "active") {
                        TextButton(onClick = onActivate) {
                            Text("Set Active", color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    if (trip.status != "completed") {
                        TextButton(onClick = onComplete) {
                            Text("Complete", color = EmeraldSafe, fontSize = 12.sp)
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Trip",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TripDetailScreen(
    trip: Trip,
    onBack: () -> Unit,
    onActivate: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Itinerary", "Attractions", "Safety Alerts", "Etiquette")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = Navy900,
                shadowElevation = 4.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Column {
                                Text(
                                    text = trip.destinationName,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "${trip.startDate} - ${trip.endDate}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CyanAccent)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (trip.status) {
                                "active" -> EmeraldSafe
                                "completed" -> MaterialTheme.colorScheme.surfaceVariant
                                else -> CyanAccent
                            }
                        ) {
                            Text(
                                text = trip.status.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (trip.status == "completed") MaterialTheme.colorScheme.onSurfaceVariant else Navy900
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Navy900,
                        contentColor = CyanAccent
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == index) CyanAccent else Color.White.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // General Advisory Box
            if (trip.generalAdvisory.isNotBlank()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Navy900.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = trip.generalAdvisory,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                            )
                        }
                    }
                }
            }

            when (selectedTab) {
                0 -> { // Day by day itinerary
                    if (trip.itinerary.isEmpty()) {
                        item {
                            Text("No day-by-day activities generated.", style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        items(trip.itinerary) { day ->
                            DayItineraryCard(day = day)
                        }
                    }
                }
                1 -> { // Top attractions
                    if (trip.topAttractions.isEmpty()) {
                        item {
                            Text("No attraction list available.", style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        items(trip.topAttractions) { att ->
                            AttractionCard(attraction = att)
                        }
                    }
                }
                2 -> { // Safety & Scam Alerts
                    if (trip.destinationAlerts.isEmpty()) {
                        item {
                            Text("No safety alerts logged for this area.", style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        items(trip.destinationAlerts) { alert ->
                            SafetyAlertCard(alert = alert)
                        }
                    }
                }
                3 -> { // Local Etiquette
                    if (trip.localEtiquette.isEmpty()) {
                        item {
                            Text("No etiquette rules logged.", style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        items(trip.localEtiquette) { eti ->
                            EtiquetteCard(etiquette = eti)
                        }
                    }
                }
            }

            // Bottom Trip Management Actions
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        if (trip.status != "active") {
                            Button(
                                onClick = onActivate,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSafe)
                            ) {
                                Text("Activate Trip")
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (trip.status != "completed") {
                            Button(
                                onClick = onComplete,
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                            ) {
                                Text("Mark Completed")
                            }
                        }
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Trip",
                            tint = CoralSOS
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayItineraryCard(day: ItineraryDay) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TealPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Day ${day.day}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TealPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = day.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            day.activities.forEachIndexed { idx, activity ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Navy800),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${idx + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = activity.place,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = activity.time,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (activity.description.isNotBlank()) {
                            Text(
                                text = activity.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                ),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttractionCard(attraction: Attraction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = attraction.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyanAccent.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = attraction.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TealPrimaryDark
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = attraction.description,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp)
            )

            if (attraction.safetyTip.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldSafe.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = EmeraldSafe,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Safety Tip: ${attraction.safetyTip}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SafetyAlertCard(alert: SafetyAlert) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                    color = when (alert.severity.lowercase()) {
                        "high" -> CoralSOS.copy(alpha = 0.15f)
                        "medium" -> AmberWarning.copy(alpha = 0.15f)
                        else -> TealPrimary.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = alert.type,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (alert.severity.lowercase()) {
                                "high" -> CoralSOS
                                "medium" -> AmberWarning
                                else -> TealPrimary
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "${alert.severity.uppercase()} PRIORITY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = when (alert.severity.lowercase()) {
                            "high" -> CoralSOS
                            "medium" -> AmberWarning
                            else -> TealPrimary
                        },
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = alert.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = alert.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            )
        }
    }
}

@Composable
fun EtiquetteCard(etiquette: LocalEtiquette) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = etiquette.rule,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            if (etiquette.reason.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = etiquette.reason,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}
