package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AreaDetails
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralSOS
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary

@Composable
fun AreaSafetyDetailsSection(
    areaDetails: AreaDetails,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isScamsExpanded by remember { mutableStateOf(false) }
    var isEtiquetteExpanded by remember { mutableStateOf(false) }
    var isTransitExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("area_safety_details_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(EmeraldSafe.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = EmeraldSafe,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Area Safety & Cultural Intelligence",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${areaDetails.localityName} • ${areaDetails.city}",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }

        // Safety Index Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (areaDetails.safetyScore >= 85) EmeraldSafe else AmberWarning
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${areaDetails.safetyScore}/100",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = areaDetails.safetyRatingLabel,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    // Solo Female rating badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Female, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Solo Female: ${areaDetails.soloFemaleRating} ★",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Night Safety Advisory
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Nightlight,
                        contentDescription = "Night Safety",
                        tint = CyanAccent,
                        modifier = Modifier
                            .size(15.dp)
                            .padding(top = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = areaDetails.nightSafetyAdvisory,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Nearest Emergency Outposts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldSafe.copy(alpha = 0.12f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocalPolice, contentDescription = null, tint = EmeraldSafe, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = areaDetails.nearestPoliceOutpost,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CoralSOS.copy(alpha = 0.12f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = CoralSOS, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = areaDetails.nearestHospital,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Expandable 1: Local Tourist Scams to Beware Of
        if (areaDetails.topScams.isNotEmpty()) {
            ExpandableIntelligenceCard(
                title = "Tourist Scams to Avoid (${areaDetails.topScams.size} Identified)",
                icon = Icons.Default.Warning,
                iconTint = AmberWarning,
                isExpanded = isScamsExpanded,
                onToggle = { isScamsExpanded = !isScamsExpanded },
                items = areaDetails.topScams
            )
        }

        // Expandable 2: Local Cultural Etiquette
        if (areaDetails.localEtiquettes.isNotEmpty()) {
            ExpandableIntelligenceCard(
                title = "Local Cultural Etiquette & Courtesies",
                icon = Icons.Default.Gavel,
                iconTint = TealPrimary,
                isExpanded = isEtiquetteExpanded,
                onToggle = { isEtiquetteExpanded = !isEtiquetteExpanded },
                items = areaDetails.localEtiquettes
            )
        }

        // Expandable 3: Smart Commute & Transit Guide
        if (areaDetails.transitTips.isNotEmpty()) {
            ExpandableIntelligenceCard(
                title = "Smart Commute & Transit Guide",
                icon = Icons.Default.Commute,
                iconTint = CyanAccent,
                isExpanded = isTransitExpanded,
                onToggle = { isTransitExpanded = !isTransitExpanded },
                items = areaDetails.transitTips
            )
        }
    }
}

@Composable
private fun ExpandableIntelligenceCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    items: List<String>
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onToggle() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(iconTint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    )
                }

                IconButton(onClick = onToggle, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items.forEach { text ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(iconTint)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
