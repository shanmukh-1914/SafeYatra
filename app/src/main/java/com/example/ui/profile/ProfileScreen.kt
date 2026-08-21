package com.example.ui.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralSOS
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onSignedOut: () -> Unit,
    onSwitchToProvider: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val languages = listOf("English", "Hindi", "Spanish", "French", "German", "Japanese", "Arabic")

    if (uiState.isSignedOut) {
        onSignedOut()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Navy900, Navy800)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(CyanAccent, TealPrimary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (uiState.userProfile?.name?.take(1)?.uppercase() ?: "T"),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = uiState.userProfile?.name?.ifBlank { "Traveler" } ?: "Traveler",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSafe,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Verified Phone & Encrypted Record",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldSafe,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }

        // Profile Details Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Traveler Profile Details",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        TextButton(
                            onClick = { viewModel.startEditing() },
                            modifier = Modifier.testTag("edit_profile_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileField(
                        icon = Icons.Default.Phone,
                        label = "Registered Phone",
                        value = uiState.userProfile?.phone?.ifBlank { "Unregistered" } ?: "Unregistered"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileField(
                        icon = Icons.Default.Public,
                        label = "Home Country / Citizenship",
                        value = uiState.userProfile?.homeCountry?.ifBlank { "Not set" } ?: "Not set"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileField(
                        icon = Icons.Default.Language,
                        label = "Preferred Communication Language",
                        value = uiState.userProfile?.preferredLanguage ?: "English"
                    )
                }
            }
        }

        // Security & Encryption Info
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacy & Safety Guarantee",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Your GPS coordinates are securely encrypted. Continuous background location broadcasts only when you activate SOS or enable real-time tracking.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Switch to Provider Portal Card
        if (onSwitchToProvider != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Navy800)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = EmeraldSafe,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Destination Provider Portal",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Access the Verified Provider Dispatch Console for Tourist Police, Safe Transit, and Emergency Responders.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onSwitchToProvider,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("switch_to_provider_portal_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldSafe,
                                contentColor = Navy900
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Open Provider Dispatch Portal", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Sign Out Button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.signOut() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("sign_out_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CoralSOS
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out of SafeYatra", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Edit Profile Modal
    if (uiState.isEditing) {
        var expandedLang by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { viewModel.cancelEditing() },
            title = { Text("Edit Traveler Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = uiState.editName,
                        onValueChange = { viewModel.onNameChanged(it) },
                        label = { Text("Full Legal Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.editHomeCountry,
                        onValueChange = { viewModel.onHomeCountryChanged(it) },
                        label = { Text("Home Country") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_country_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedLang,
                        onExpandedChange = { expandedLang = !expandedLang },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.editLanguage,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Preferred Language") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLang) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedLang,
                            onDismissRequest = { expandedLang = false }
                        ) {
                            languages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang) },
                                    onClick = {
                                        viewModel.onLanguageChanged(lang)
                                        expandedLang = false
                                    }
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
                    onClick = { viewModel.saveProfile() },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    enabled = !uiState.isLoading && uiState.editName.isNotBlank() && uiState.editHomeCountry.isNotBlank(),
                    modifier = Modifier.testTag("save_edit_profile_button")
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                    } else {
                        Text("Save Changes")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelEditing() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileField(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(TealPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
