package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LiveWeatherData
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary

@Composable
fun LiveWeatherCard(
    weather: LiveWeatherData,
    modifier: Modifier = Modifier
) {
    val weatherIcon: ImageVector = when (weather.conditionIconCode) {
        "sunny" -> Icons.Default.WbSunny
        "partly_cloudy" -> Icons.Default.WbTwilight
        "cloudy" -> Icons.Default.Cloud
        "rain", "heavy_rain" -> Icons.Default.WaterDrop
        "thunder" -> Icons.Default.Thunderstorm
        else -> Icons.Default.WbSunny
    }

    val gradientColors = when (weather.conditionIconCode) {
        "sunny" -> listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
        "rain", "heavy_rain" -> listOf(Color(0xFF0F2B48), Color(0xFF0B192C))
        "thunder" -> listOf(Color(0xFF2E1065), Color(0xFF0F172A))
        else -> listOf(Navy800, Navy900)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("live_weather_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Navy900),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(gradientColors))
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header: City and Live Condition
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = weather.cityName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = weather.condition,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    // Main Temperature Display
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = weatherIcon,
                                contentDescription = weather.condition,
                                tint = if (weather.conditionIconCode == "sunny") AmberWarning else CyanAccent,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${weather.temperatureC.toInt()}°C",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Feels ${weather.apparentTemperatureC.toInt()}°C",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Weather Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherStatItem(
                        icon = Icons.Default.WaterDrop,
                        label = "Humidity",
                        value = "${weather.humidityPercent}%"
                    )
                    WeatherStatItem(
                        icon = Icons.Default.Air,
                        label = "Wind",
                        value = "${weather.windSpeedKmh.toInt()} km/h"
                    )
                    WeatherStatItem(
                        icon = Icons.Default.WbSunny,
                        label = "UV Index",
                        value = "${weather.uvIndex} (${if (weather.uvIndex > 6) "High" else "Moderate"})"
                    )
                    WeatherStatItem(
                        icon = Icons.Default.Umbrella,
                        label = "Rain",
                        value = "${weather.rainProbabilityPercent}%"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AQI & Safety Advisory
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.25f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (weather.aqi <= 100) EmeraldSafe else AmberWarning
                        ) {
                            Text(
                                text = "AQI ${weather.aqi}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = weather.safetyAdvisory,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherStatItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = CyanAccent,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 11.sp
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 9.sp
            )
        )
    }
}
