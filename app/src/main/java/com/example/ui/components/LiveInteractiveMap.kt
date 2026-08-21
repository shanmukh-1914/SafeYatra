package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NearbyAttractionItem
import com.example.data.model.NearbyPlaceItem
import com.example.data.model.RiskReport
import com.example.data.model.VerifiedProvider
import com.example.data.repository.LocationTrackingState
import com.example.data.service.LivePlacesService
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralSOS
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryDark
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

sealed class MapMarkerData(
    val id: String,
    val title: String,
    val subtitle: String,
    val lat: Double,
    val lng: Double,
    val type: String, // "police", "hospital", "attraction", "cab", "risk"
    val color: Color,
    val phone: String = "",
    val distanceKm: Double = 0.0,
    val openStatus: String = ""
) {
    class PlaceMarker(val place: NearbyPlaceItem) : MapMarkerData(
        id = place.id,
        title = place.name,
        subtitle = "${place.category} • ${place.openStatus}",
        lat = place.lat,
        lng = place.lng,
        type = when {
            place.category.contains("Police", true) -> "police"
            place.category.contains("Hospital", true) || place.category.contains("Medical", true) -> "hospital"
            place.category.contains("Cab", true) || place.category.contains("Taxi", true) -> "cab"
            else -> "attraction"
        },
        color = when {
            place.category.contains("Police", true) -> EmeraldSafe
            place.category.contains("Hospital", true) || place.category.contains("Medical", true) -> CoralSOS
            place.category.contains("Cab", true) || place.category.contains("Taxi", true) -> AmberWarning
            else -> CyanAccent
        },
        phone = place.phone,
        distanceKm = place.distanceKm,
        openStatus = place.openStatus
    )

    class AttractionMarker(val item: NearbyAttractionItem) : MapMarkerData(
        id = item.id.ifBlank { item.name },
        title = item.name,
        subtitle = "${item.category} • ${item.distanceKm} km away",
        lat = item.lat,
        lng = item.lng,
        type = "attraction",
        color = CyanAccent,
        distanceKm = item.distanceKm,
        openStatus = item.openHours
    )

    class ProviderMarker(val provider: VerifiedProvider) : MapMarkerData(
        id = provider.id,
        title = provider.name,
        subtitle = "${provider.type} • ${provider.verificationStatus}",
        lat = provider.lat,
        lng = provider.lng,
        type = if (provider.type.contains("Police", true)) "police" else if (provider.type.contains("Medical", true) || provider.type.contains("Hospital", true)) "hospital" else "cab",
        color = if (provider.type.contains("Police", true)) EmeraldSafe else if (provider.type.contains("Medical", true)) CoralSOS else TealPrimary,
        phone = provider.phone,
        openStatus = "Verified Provider"
    )

    class RiskMarker(val report: RiskReport) : MapMarkerData(
        id = report.id,
        title = "${report.riskType} Alert",
        subtitle = report.description,
        lat = report.lat,
        lng = report.lng,
        type = "risk",
        color = CoralSOS,
        openStatus = "Active Alert"
    )
}

enum class MapFilterCategory(val label: String) {
    ALL("All Places"),
    POLICE("Police Stations"),
    HOSPITALS("Hospitals"),
    ATTRACTIONS("Attractions"),
    CABS("Safe Cabs"),
    RISKS("Alerts")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveInteractiveMap(
    locationState: LocationTrackingState,
    nearbyPlaces: List<NearbyPlaceItem> = emptyList(),
    nearbyAttractions: List<NearbyAttractionItem> = emptyList(),
    verifiedProviders: List<VerifiedProvider> = emptyList(),
    riskReports: List<RiskReport> = emptyList(),
    onAttractionClick: (NearbyAttractionItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }
    var selectedFilter by remember { mutableStateOf(MapFilterCategory.ALL) }
    var selectedMarker by remember { mutableStateOf<MapMarkerData?>(null) }

    // Pulsing radar animation
    val infiniteTransition = rememberInfiniteTransition(label = "radarPulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    // Build list of markers based on current location and real-time live data
    val markers = remember(locationState.latitude, locationState.longitude, nearbyPlaces, nearbyAttractions, verifiedProviders, riskReports, selectedFilter) {
        val list = mutableListOf<MapMarkerData>()

        // 1. Live location places (Police, Hospital, Attractions from LivePlacesService)
        nearbyPlaces.forEach { place ->
            val matchesFilter = when (selectedFilter) {
                MapFilterCategory.ALL -> true
                MapFilterCategory.POLICE -> place.category.contains("Police", true)
                MapFilterCategory.HOSPITALS -> place.category.contains("Hospital", true) || place.category.contains("Medical", true)
                MapFilterCategory.ATTRACTIONS -> place.category.contains("Attraction", true) || place.category.contains("Heritage", true)
                MapFilterCategory.CABS -> place.category.contains("Cab", true) || place.category.contains("Taxi", true)
                MapFilterCategory.RISKS -> false
            }
            if (matchesFilter) {
                list.add(MapMarkerData.PlaceMarker(place))
            }
        }

        // 2. Add extra attractions if filter matches
        if (selectedFilter == MapFilterCategory.ALL || selectedFilter == MapFilterCategory.ATTRACTIONS) {
            nearbyAttractions.forEach { item ->
                val targetLat = if (item.lat != 0.0) item.lat else locationState.latitude + 0.005
                val targetLng = if (item.lng != 0.0) item.lng else locationState.longitude + 0.004
                if (list.none { it.title.equals(item.name, ignoreCase = true) }) {
                    list.add(MapMarkerData.AttractionMarker(item.copy(lat = targetLat, lng = targetLng)))
                }
            }
        }

        // 3. Add Verified Providers if filter matches
        if (selectedFilter == MapFilterCategory.ALL || selectedFilter == MapFilterCategory.POLICE || selectedFilter == MapFilterCategory.CABS) {
            verifiedProviders.forEach { prov ->
                val targetLat = if (prov.lat != 0.0) prov.lat else locationState.latitude - 0.004
                val targetLng = if (prov.lng != 0.0) prov.lng else locationState.longitude + 0.006
                val isPolice = prov.type.contains("Police", true) || prov.type.contains("Medical", true)
                val isCab = prov.type.contains("Transport", true) || prov.type.contains("Cab", true) || prov.type.contains("Taxi", true)

                if ((selectedFilter == MapFilterCategory.ALL ||
                    (selectedFilter == MapFilterCategory.POLICE && isPolice) ||
                    (selectedFilter == MapFilterCategory.CABS && isCab)) &&
                    list.none { it.title.equals(prov.name, ignoreCase = true) }
                ) {
                    list.add(MapMarkerData.ProviderMarker(prov.copy(lat = targetLat, lng = targetLng)))
                }
            }
        }

        // 4. Add Risk Alerts if filter matches
        if (selectedFilter == MapFilterCategory.ALL || selectedFilter == MapFilterCategory.RISKS) {
            riskReports.take(3).forEach { risk ->
                val targetLat = if (risk.lat != 0.0) risk.lat else locationState.latitude + 0.007
                val targetLng = if (risk.lng != 0.0) risk.lng else locationState.longitude - 0.005
                list.add(MapMarkerData.RiskMarker(risk.copy(lat = targetLat, lng = targetLng)))
            }
        }

        list
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(6.dp, shape = RoundedCornerShape(20.dp))
            .testTag("live_map_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Navy900)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Live GPS status & Coordinates
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy800)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (locationState.isHighAccuracyLocked) EmeraldSafe else AmberWarning)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Live Location & Safety Map",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.35f)
                ) {
                    Text(
                        text = "${String.format("%.4f", locationState.latitude)}, ${String.format("%.4f", locationState.longitude)} (±${locationState.accuracyMeters.roundToInt()}m)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Interactive Map Canvas Area
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .background(Color(0xFF0F172A))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            panOffsetX += dragAmount.x
                            panOffsetY += dragAmount.y
                        }
                    }
                    .testTag("interactive_map_canvas")
            ) {
                val canvasWidth = constraints.maxWidth.toFloat()
                val canvasHeight = constraints.maxHeight.toFloat()
                val centerCanvasX = canvasWidth / 2f + panOffsetX
                val centerCanvasY = canvasHeight / 2f + panOffsetY

                // Geographic coordinate scale (degree to canvas pixels at current zoom)
                val scaleFactor = 7200f * zoomLevel

                // Render Map Canvas Background, Grid, Roads, and Radar
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val mapBg = Color(0xFF0B132B)
                    drawRect(color = mapBg)

                    // Draw subtle grid lines representing streets & city sectors
                    val gridPaint = Color(0xFF1E293B)
                    val step = 45f * zoomLevel
                    var x = (panOffsetX % step)
                    while (x < size.width) {
                        drawLine(
                            color = gridPaint,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                        x += step
                    }

                    var y = (panOffsetY % step)
                    while (y < size.height) {
                        drawLine(
                            color = gridPaint,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                        y += step
                    }

                    // Draw stylized avenue routes
                    val roadColor = Color(0xFF1C2D4A)
                    val mainHighway = Color(0xFF263C66)

                    drawLine(
                        color = mainHighway,
                        start = Offset(0f, centerCanvasY - 40f),
                        end = Offset(size.width, centerCanvasY + 60f),
                        strokeWidth = 6.dp.toPx()
                    )
                    drawLine(
                        color = roadColor,
                        start = Offset(centerCanvasX - 80f, 0f),
                        end = Offset(centerCanvasX + 110f, size.height),
                        strokeWidth = 4.dp.toPx()
                    )
                    drawLine(
                        color = roadColor,
                        start = Offset(0f, centerCanvasY + 100f),
                        end = Offset(size.width, centerCanvasY - 80f),
                        strokeWidth = 3.dp.toPx()
                    )

                    // Draw Safe Zone Geofence Circle around user
                    drawCircle(
                        color = EmeraldSafe.copy(alpha = 0.08f),
                        radius = 180f * zoomLevel,
                        center = Offset(centerCanvasX, centerCanvasY)
                    )
                    drawCircle(
                        color = EmeraldSafe.copy(alpha = 0.35f),
                        radius = 180f * zoomLevel,
                        center = Offset(centerCanvasX, centerCanvasY),
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                        )
                    )

                    // Draw live radar expanding waves from user center
                    drawCircle(
                        color = CyanAccent.copy(alpha = pulseAlpha * 0.4f),
                        radius = pulseRadius * zoomLevel * 1.5f,
                        center = Offset(centerCanvasX, centerCanvasY),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw GPS accuracy radius halo
                    val accuracyPixels = (locationState.accuracyMeters.coerceIn(8f, 60f) * 1.8f) * zoomLevel
                    drawCircle(
                        color = TealPrimary.copy(alpha = 0.15f),
                        radius = accuracyPixels,
                        center = Offset(centerCanvasX, centerCanvasY)
                    )
                    drawCircle(
                        color = TealPrimary.copy(alpha = 0.6f),
                        radius = accuracyPixels,
                        center = Offset(centerCanvasX, centerCanvasY),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Draw My Live Location Beacon Pin
                    drawCircle(
                        color = Color.White,
                        radius = 11f,
                        center = Offset(centerCanvasX, centerCanvasY)
                    )
                    drawCircle(
                        color = CyanAccent,
                        radius = 8f,
                        center = Offset(centerCanvasX, centerCanvasY)
                    )
                }

                // Interactive Overlay Markers (Placed at relative lat/lng offsets)
                markers.forEach { marker ->
                    val dLat = marker.lat - locationState.latitude
                    val dLng = marker.lng - locationState.longitude

                    val markerX = centerCanvasX + (dLng * scaleFactor).toFloat()
                    val markerY = centerCanvasY - (dLat * scaleFactor).toFloat()

                    if (markerX in 10f..(canvasWidth - 10f) && markerY in 10f..(canvasHeight - 10f)) {
                        Box(
                            modifier = Modifier
                                .offset { IntOffset((markerX - 20f).roundToInt(), (markerY - 20f).roundToInt()) }
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable {
                                    selectedMarker = marker
                                    if (marker is MapMarkerData.AttractionMarker) {
                                        onAttractionClick(marker.item)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = marker.color,
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .size(30.dp)
                                    .border(2.dp, Color.White, CircleShape)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = when (marker.type) {
                                            "police" -> Icons.Default.LocalPolice
                                            "hospital" -> Icons.Default.LocalHospital
                                            "cab" -> Icons.Default.LocalTaxi
                                            "risk" -> Icons.Default.Warning
                                            else -> Icons.Default.Place
                                        },
                                        contentDescription = marker.title,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Map Overlay Controls: Zoom In / Out & Recenter GPS
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900.copy(alpha = 0.9f),
                        shadowElevation = 4.dp,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Column {
                            IconButton(
                                onClick = { if (zoomLevel < 2.5f) zoomLevel += 0.25f },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Surface(
                                color = Color.White.copy(alpha = 0.1f),
                                modifier = Modifier
                                    .width(34.dp)
                                    .height(1.dp)
                            ) {}
                            IconButton(
                                onClick = { if (zoomLevel > 0.6f) zoomLevel -= 0.25f },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Recenter on GPS Button
                    Surface(
                        shape = CircleShape,
                        color = Navy900.copy(alpha = 0.9f),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable {
                                panOffsetX = 0f
                                panOffsetY = 0f
                                zoomLevel = 1.0f
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Recenter GPS",
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Compass & Safe Corridor Badge (Bottom-Left)
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Navy900.copy(alpha = 0.85f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = EmeraldSafe,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Live Location Services Active",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Selected Marker Info Popup (Sliding bottom card over map)
                androidx.compose.animation.AnimatedVisibility(
                    visible = selectedMarker != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    selectedMarker?.let { marker ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Navy900,
                            shadowElevation = 8.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, marker.color.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(marker.color.copy(alpha = 0.25f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = when (marker.type) {
                                                    "police" -> Icons.Default.LocalPolice
                                                    "hospital" -> Icons.Default.LocalHospital
                                                    "cab" -> Icons.Default.LocalTaxi
                                                    "risk" -> Icons.Default.Warning
                                                    else -> Icons.Default.Place
                                                },
                                                contentDescription = null,
                                                tint = marker.color,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = marker.title,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = if (marker.distanceKm > 0.0) "${marker.subtitle} • ${marker.distanceKm} km away" else marker.subtitle,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color.White.copy(alpha = 0.75f),
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { selectedMarker = null },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            LivePlacesService.openGoogleMapsDirections(context, marker.lat, marker.lng, marker.title)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Google Maps Navigation", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    if (marker.phone.isNotBlank()) {
                                        Button(
                                            onClick = {
                                                try {
                                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${marker.phone}"))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    // ignore
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSafe),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(0.7f),
                                            contentPadding = PaddingValues(vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Layer Filter Chips (Below map)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy800)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MapFilterCategory.values()) { cat ->
                    val isSelected = selectedFilter == cat
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) TealPrimary else Color.White.copy(alpha = 0.1f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedFilter = cat }
                    ) {
                        Text(
                            text = cat.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Google Maps 1-Tap Discovery Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldSafe.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSafe.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            LivePlacesService.openGoogleMapsSearch(context, "police", locationState.latitude, locationState.longitude)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.LocalPolice, contentDescription = null, tint = EmeraldSafe, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Google Police", color = EmeraldSafe, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CoralSOS.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CoralSOS.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            LivePlacesService.openGoogleMapsSearch(context, "hospital", locationState.latitude, locationState.longitude)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = CoralSOS, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Google Hospitals", color = CoralSOS, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyanAccent.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            LivePlacesService.openGoogleMapsSearch(context, "attraction", locationState.latitude, locationState.longitude)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Google Sights", color = CyanAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

