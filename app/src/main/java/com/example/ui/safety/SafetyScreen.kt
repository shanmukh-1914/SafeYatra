package com.example.ui.safety

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyContact
import com.example.data.model.RiskReport
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralSOS
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyScreen(
    viewModel: SafetyViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val officialHelplines = listOf(
        Triple("112", "National Emergency (All-in-One)", CoralSOS),
        Triple("1363", "24x7 Tourist Helpline India (Multi-lang)", TealPrimary),
        Triple("100", "Police Control Room", Navy800),
        Triple("108", "Emergency Ambulance", EmeraldSafe),
        Triple("1091", "Women Safety Helpline", CyanAccent)
    )

    val riskTypes = listOf(
        "Scam Alert", "Road Hazard", "Unsafe Area",
        "Aggressive Vendor", "Severe Weather", "General Advisory"
    )
    val relationships = listOf("Family", "Friend", "Spouse", "Colleague", "Embassy / Consulate")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("safety_screen"),
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
                                text = "Emergency & Guardian Hub",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Real-time emergency broadcast & verified response network",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyanAccent.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Active SOS or Trigger SOS
        item {
            Box(modifier = Modifier.padding(16.dp)) {
                if (uiState.activeSosEvent != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = CoralSOS,
                        shadowElevation = 6.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SOS EMERGENCY BROADCAST ACTIVE",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Your exact location is being pushed to Firestore and dispatched to your guardians.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    uiState.activeSosEvent?.eventId?.let { viewModel.resolveSos(it) }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = CoralSOS
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Resolve SOS • I am Safe", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = { viewModel.triggerSos() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .testTag("trigger_sos_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoralSOS,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        if (uiState.isActionLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = uiState.actionLoadingMessage.ifBlank { "Sending alert to emergency network..." },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(26.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "HOLD / TAP FOR EMERGENCY SOS",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Official Helplines
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text(
                    text = "Official National & Tourist Helplines",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Direct 1-tap connection to law enforcement and tourist helplines",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(officialHelplines) { (number, label, color) ->
                        Surface(
                            modifier = Modifier
                                .width(190.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.dialHelpline(context, number) },
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = color.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = number,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = color
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "Call",
                                        tint = TealPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Emergency Contacts Section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Emergency Guardians (${uiState.emergencyContacts.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Notified instantly during SOS emergencies with live GPS",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { viewModel.openAddContactDialog() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        modifier = Modifier.testTag("add_contact_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Guardian", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        if (uiState.isInitialLoading) {
            items(2) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
                            )
                        }
                    }
                }
            }
        } else if (uiState.emergencyContacts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("empty_guardians_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(TealPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "No Guardians Added Yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Add family members or close contacts so they receive your live GPS coordinates & SMS alerts instantly when SOS is activated.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.openAddContactDialog() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("empty_state_add_guardian_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add Your First Emergency Guardian",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        } else {
            items(uiState.emergencyContacts) { contact ->
                EmergencyContactRow(
                    contact = contact,
                    onDelete = { viewModel.deleteContact(contact.id) },
                    onCall = { viewModel.dialHelpline(context, contact.phone) }
                )
            }
        }

        // Community Risk Radar & Reporting
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
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
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Community Safety Reports",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = { viewModel.openReportRiskDialog() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberWarning, contentColor = Navy900),
                        modifier = Modifier.testTag("report_risk_button")
                    ) {
                        Text("Report Risk", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        items(uiState.riskReports) { report ->
            RiskReportRowItem(report = report)
        }
    }

    // Add Emergency Contact Dialog
    if (uiState.isAddingContact) {
        var expandedRel by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { viewModel.closeAddContactDialog() },
            title = { Text("Add Emergency Guardian", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = uiState.contactName,
                        onValueChange = { viewModel.onContactNameChanged(it) },
                        label = { Text("Guardian Name") },
                        placeholder = { Text("e.g. John Doe") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TealPrimary) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.contactPhone,
                        onValueChange = { viewModel.onContactPhoneChanged(it) },
                        label = { Text("Phone Number (with Country Code)") },
                        placeholder = { Text("+91 9876543210") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TealPrimary) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_phone_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedRel,
                        onExpandedChange = { expandedRel = !expandedRel },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.contactRelation,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Relationship") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRel) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedRel,
                            onDismissRequest = { expandedRel = false }
                        ) {
                            relationships.forEach { rel ->
                                DropdownMenuItem(
                                    text = { Text(rel) },
                                    onClick = {
                                        viewModel.onContactRelationChanged(rel)
                                        expandedRel = false
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
                    onClick = { viewModel.saveContact() },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    enabled = !uiState.isActionLoading && uiState.contactName.isNotBlank() && uiState.contactPhone.isNotBlank(),
                    modifier = Modifier.testTag("save_contact_button")
                ) {
                    if (uiState.isActionLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                    } else {
                        Text("Save Guardian")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeAddContactDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Report Risk Dialog
    if (uiState.isReportingRisk) {
        var expandedRisk by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { viewModel.closeReportRiskDialog() },
            title = { Text("Report Local Hazard or Scam", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Your current GPS location will be attached to help warn other travelers.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedRisk,
                        onExpandedChange = { expandedRisk = !expandedRisk },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.reportType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hazard / Issue Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRisk) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedRisk,
                            onDismissRequest = { expandedRisk = false }
                        ) {
                            riskTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        viewModel.onReportTypeChanged(type)
                                        expandedRisk = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.reportDescription,
                        onValueChange = { viewModel.onReportDescriptionChanged(it) },
                        label = { Text("Description & Warning Details") },
                        placeholder = { Text("e.g. Overcharging auto rickshaws near station, fake ticket sellers...") },
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_desc_input")
                    )

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
                    onClick = { viewModel.submitRiskReport() },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberWarning, contentColor = Navy900),
                    enabled = !uiState.isActionLoading && uiState.reportDescription.isNotBlank(),
                    modifier = Modifier.testTag("submit_report_button")
                ) {
                    if (uiState.isActionLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Navy900)
                    } else {
                        Text("Broadcast Warning", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeReportRiskDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmergencyContactRow(
    contact: EmergencyContact,
    onDelete: () -> Unit,
    onCall: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TealPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${contact.phone} • ${contact.relationship}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Row {
                IconButton(onClick = onCall) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = EmeraldSafe, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun RiskReportRowItem(report: RiskReport) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
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
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                if (report.reporterId == "authority") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = EmeraldSafe, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Official Advisory", style = MaterialTheme.typography.labelSmall.copy(color = EmeraldSafe, fontSize = 10.sp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
