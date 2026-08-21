package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.LocationTrackingState
import com.example.data.service.GeocodeResult
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchDialog(
    currentLocationState: LocationTrackingState,
    searchQuery: String,
    searchResults: List<GeocodeResult>,
    isSearching: Boolean,
    isRefreshing: Boolean,
    onQueryChange: (String) -> Unit,
    onSelectLocation: (GeocodeResult) -> Unit,
    onAutoDetect: () -> Unit,
    onDismiss: () -> Unit
) {
    val quickCities = listOf(
        "Hyderabad" to (17.3850 to 78.4867),
        "Bengaluru" to (12.9716 to 77.5946),
        "New Delhi" to (28.6139 to 77.2090),
        "Mumbai" to (19.0760 to 72.8777),
        "Jaipur" to (26.9124 to 75.7873),
        "Agra" to (27.1767 to 78.0081),
        "Goa" to (15.2993 to 74.1240),
        "Kolkata" to (22.5726 to 88.3639),
        "Chennai" to (13.0827 to 80.2707),
        "Varanasi" to (25.3176 to 82.9739)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("location_picker_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Navy900)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(TealPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Set Exact Location",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Calibrate GPS & explore places anywhere",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Auto Detect Current GPS Button
                Button(
                    onClick = onAutoDetect,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSafe),
                    enabled = !isRefreshing
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Detecting Live Real-Time GPS...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Navy900
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Auto-Detect My Exact Live GPS",
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("location_search_field"),
                    placeholder = {
                        Text(
                            "Type city, area, or street (e.g. Hyderabad, Banjara Hills)...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = CyanAccent
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = Navy800,
                        focusedContainerColor = Navy800,
                        unfocusedContainerColor = Navy800
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick City Chips
                Text(
                    text = "Quick Popular Destinations:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(quickCities) { (city, coords) ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Navy800,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    onSelectLocation(
                                        GeocodeResult(
                                            displayName = "$city, India",
                                            latitude = coords.first,
                                            longitude = coords.second,
                                            city = city
                                        )
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationCity,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = city,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Results list
                Text(
                    text = if (searchQuery.isNotBlank()) "Search Results" else "Locations & Recommended Hubs",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = CyanAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matching locations found.\nTry a different landmark or city name.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults) { result ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Navy800,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectLocation(result) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = result.displayName.substringBefore(","),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = result.displayName,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White.copy(alpha = 0.65f),
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Coords: ${String.format("%.4f", result.latitude)}, ${String.format("%.4f", result.longitude)}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = CyanAccent.copy(alpha = 0.8f),
                                                fontSize = 10.sp
                                            )
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
