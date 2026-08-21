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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyContact
import com.example.data.model.SosEvent
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralSOS
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardianLiveRadarDialog(
    sosEvent: SosEvent,
    emergencyContacts: List<EmergencyContact>,
    travelerName: String,
    onDismiss: () -> Unit,
    onResolveSos: (String) -> Unit
) {
    val context = LocalContext.current

    // Live pulsing radar ring animation
    val infiniteTransition = rememberInfiniteTransition(label = "beaconPulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = 75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    val liveTrackingUrl = "https://safeyatra.app/sos/${sosEvent.eventId}?lat=${sosEvent.lat}&lng=${sosEvent.lng}"
    val googleMapsUrl = "https://maps.google.com/?q=${sosEvent.lat},${sosEvent.lng}"
    val emergencySmsMessage = "🚨 EMERGENCY SOS from $travelerName! I need immediate help at coordinates (${String.format("%.5f", sosEvent.lat)}, ${String.format("%.5f", sosEvent.lng)}). Live GPS tracking: $googleMapsUrl (SafeYatra Guardian Radar: $liveTrackingUrl)"

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guardian_radar_dialog")
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Navy900,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CoralSOS.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                tint = CoralSOS,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Guardian Live GPS Radar",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Real-time Telemetry Broadcast Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyanAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Radar Beacon Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF070D1E))
                        .border(1.dp, CoralSOS.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(130.dp)) {
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f

                        // Radar concentric rings
                        drawCircle(
                            color = CoralSOS.copy(alpha = 0.15f),
                            radius = 50f,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = CoralSOS.copy(alpha = 0.25f),
                            radius = 80f,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // Pulsing wave
                        drawCircle(
                            color = CoralSOS.copy(alpha = pulseAlpha),
                            radius = pulseRadius * 1.3f,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 2.5.dp.toPx())
                        )

                        // Center beacon
                        drawCircle(
                            color = Color.White,
                            radius = 8f,
                            center = Offset(centerX, centerY)
                        )
                        drawCircle(
                            color = CoralSOS,
                            radius = 6f,
                            center = Offset(centerX, centerY)
                        )
                    }

                    // Live Coordinates Overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Navy900.copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = "Lat: ${String.format("%.5f", sosEvent.lat)}  •  Lng: ${String.format("%.5f", sosEvent.lng)} (±${sosEvent.accuracyMeters.roundToInt()}m)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Guardian Direct Dispatch Action Buttons
                Text(
                    text = "DISPATCH LIVE LOCATION TO GUARDIANS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // SMS to all Guardians button
                    Button(
                        onClick = {
                            try {
                                val allPhones = emergencyContacts.map { it.phone.trim() }.filter { it.isNotBlank() }
                                val phoneJoined = if (allPhones.isNotEmpty()) allPhones.joinToString(";") else ""
                                val uri = if (phoneJoined.isNotBlank()) Uri.parse("smsto:$phoneJoined") else Uri.parse("smsto:")
                                val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                                    putExtra("sms_body", emergencySmsMessage)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, emergencySmsMessage)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Send SOS SMS to Guardians"))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CoralSOS),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sms_guardians_button"),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SMS Guardians", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Share Live GPS Link button
                    Button(
                        onClick = {
                            try {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "EMERGENCY SOS LIVE LOCATION")
                                    putExtra(Intent.EXTRA_TEXT, emergencySmsMessage)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Live SOS Telemetry"))
                            } catch (e: Exception) {
                                // ignore
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_sos_link_button"),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // List of Guardian Contacts with 1-Tap Call & Status
                Text(
                    text = "GUARDIAN NETWORK STATUS (${emergencyContacts.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (emergencyContacts.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.05f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No emergency contacts registered yet. Please add guardian numbers in Safety Hub.",
                            style = MaterialTheme.typography.bodySmall.copy(color = AmberWarning),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        emergencyContacts.take(3).forEach { contact ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.07f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldSafe)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "${contact.name} (${contact.relationship})",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Text(
                                                text = contact.phone,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color.White.copy(alpha = 0.7f),
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = {
                                                try {
                                                    val uri = Uri.parse("smsto:${contact.phone}")
                                                    val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                                                        putExtra("sms_body", emergencySmsMessage)
                                                    }
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    // ignore
                                                }
                                            },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.Message, contentDescription = "SMS", tint = CyanAccent, modifier = Modifier.size(16.dp))
                                        }

                                        IconButton(
                                            onClick = {
                                                try {
                                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    // ignore
                                                }
                                            },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = "Call", tint = EmeraldSafe, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Resolve SOS Button
                Button(
                    onClick = {
                        onResolveSos(sosEvent.eventId)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSafe),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_resolve_sos_button"),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I Am Safe • Resolve Emergency SOS",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
