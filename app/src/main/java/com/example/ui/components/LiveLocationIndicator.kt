package com.example.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.LocationTrackingState
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary

@Composable
fun LiveLocationIndicator(
    state: LocationTrackingState,
    onRequestPermission: () -> Unit,
    onOpenLocationPicker: (() -> Unit)? = null,
    onAutoDetect: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseColor by infiniteTransition.animateColor(
        initialValue = EmeraldSafe,
        targetValue = EmeraldSafe.copy(alpha = 0.25f),
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("live_location_indicator"),
        color = Navy800.copy(alpha = 0.9f),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top row: Status + Accuracy Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.isTracking) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(pulseColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Live GPS Active",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSafe
                            )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.LocationOff,
                            contentDescription = "Location Inactive",
                            tint = AmberWarning,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Location Off",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = AmberWarning
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (state.isTracking) TealPrimary.copy(alpha = 0.25f) else AmberWarning.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (state.accuracyMeters > 0) "±${state.accuracyMeters.toInt()}m Accuracy" else "High Precision GPS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (state.isTracking) CyanAccent else AmberWarning,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle row: City & Coordinates
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.resolvedCity.ifBlank { "Live Location Coordinates" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${String.format("%.4f", state.latitude)}, ${String.format("%.4f", state.longitude)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    )
                }

                // Interactive Change & Detect Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onAutoDetect != null) {
                        IconButton(
                            onClick = onAutoDetect,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = "Auto-detect location",
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (onOpenLocationPicker != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = TealPrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onOpenLocationPicker() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditLocation,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Change",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    } else if (!state.isTracking) {
                        Button(
                            onClick = onRequestPermission,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberWarning)
                        ) {
                            Text("Enable", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
