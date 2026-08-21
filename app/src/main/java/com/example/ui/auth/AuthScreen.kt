package com.example.ui.auth

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.CoralSOS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    if (uiState.isUserFullyAuthenticated) {
        onAuthenticated()
    }

    val countryCodes = listOf(
        "+91" to "India (+91)",
        "+1" to "USA / Canada (+1)",
        "+44" to "UK (+44)",
        "+61" to "Australia (+61)",
        "+49" to "Germany (+49)",
        "+33" to "France (+33)",
        "+81" to "Japan (+81)",
        "+971" to "UAE (+971)",
        "+65" to "Singapore (+65)",
        "+66" to "Thailand (+66)",
        "+34" to "Spain (+34)",
        "+39" to "Italy (+39)"
    )

    val languages = listOf("English", "Hindi", "Spanish", "French", "German", "Japanese", "Arabic")

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Header Gradient Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Navy900,
                                Navy800,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // Brand Emblem
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CyanAccent, TealPrimary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "SafeYatra Security",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SafeYatra",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                )

                Text(
                    text = "Real-Time Travel Safety & Emergency Network",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = CyanAccent.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Main Auth Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_card"),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (uiState.step) {
                            AuthStep.ENTER_PHONE -> {
                                Text(
                                    text = "Sign In / Register",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                Text(
                                    text = "Enter your mobile number to receive a secure one-time verification code.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
                                )

                                var expandedCountry by remember { mutableStateOf(false) }

                                ExposedDropdownMenuBox(
                                    expanded = expandedCountry,
                                    onExpandedChange = { expandedCountry = !expandedCountry },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = uiState.countryCode,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Country Code") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Public,
                                                contentDescription = null,
                                                tint = TealPrimary
                                            )
                                        },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCountry) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(14.dp)
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expandedCountry,
                                        onDismissRequest = { expandedCountry = false }
                                    ) {
                                        countryCodes.forEach { (code, label) ->
                                            DropdownMenuItem(
                                                text = { Text(label) },
                                                onClick = {
                                                    viewModel.onCountryCodeChanged(code)
                                                    expandedCountry = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = uiState.phoneNumber,
                                    onValueChange = { viewModel.onPhoneNumberChanged(it) },
                                    label = { Text("Phone Number") },
                                    placeholder = { Text("98765 43210") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Phone",
                                            tint = TealPrimary
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Phone,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (activity != null) {
                                                viewModel.sendVerificationCode(activity)
                                            }
                                        }
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("phone_input"),
                                    shape = RoundedCornerShape(14.dp)
                                )

                                // Auto-detected role banner
                                if (uiState.detectedRoleBadge != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        color = EmeraldSafe.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, EmeraldSafe.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Security,
                                                contentDescription = null,
                                                tint = EmeraldSafe,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "✓ Role Identified",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = EmeraldSafe
                                                    )
                                                )
                                                Text(
                                                    text = uiState.detectedRoleBadge ?: "",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Medium,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                if (uiState.isLoading && uiState.loadingStatusMessage.isNotBlank()) {
                                    Surface(
                                        color = TealPrimary.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = TealPrimary
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = uiState.loadingStatusMessage,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = TealPrimary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        if (activity != null) {
                                            viewModel.sendVerificationCode(activity)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("send_otp_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TealPrimary,
                                        contentColor = Color.White
                                    ),
                                    enabled = !uiState.isLoading && uiState.phoneNumber.isNotBlank()
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = Color.White,
                                            strokeWidth = 2.5.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Authenticating...",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    } else {
                                        Text(
                                            text = "Get Verification OTP",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Text(
                                        text = "  1-Tap Instant Portals  ",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = { viewModel.startNewUserProfileRegistration("traveler") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("quick_demo_traveler_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = CyanAccent
                                    ),
                                    border = BorderStroke(1.5.dp, CyanAccent)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "✈️ Register as Traveler (Enter Name & Details)",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CyanAccent
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { viewModel.openProviderVerificationDialog() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("quick_demo_provider_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Navy800,
                                        contentColor = EmeraldSafe
                                    ),
                                    border = BorderStroke(1.5.dp, EmeraldSafe)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = EmeraldSafe,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "🛡️ Register as Verified Provider (ID Verification)",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldSafe
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = TealPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Auto-Detection: Provider phone numbers (e.g. +91 11 2346 9526 / +91 98110 54321) automatically unlock the Verified Provider Dispatch Portal.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }

                            AuthStep.ENTER_OTP -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { viewModel.backToPhoneEntry() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                    Text(
                                        text = "Verify OTP",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }

                                Text(
                                    text = "Enter the 6-digit code sent to ${uiState.countryCode} ${uiState.phoneNumber} (or use 123456)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                                )

                                OutlinedTextField(
                                    value = uiState.otpCode,
                                    onValueChange = {
                                        if (it.length <= 6) {
                                            viewModel.onOtpCodeChanged(it)
                                        }
                                    },
                                    label = { Text("6-Digit OTP Code") },
                                    placeholder = { Text("• • • • • •") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "OTP Code",
                                            tint = CyanAccent
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.NumberPassword,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { viewModel.verifyOtp() }
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("otp_input"),
                                    shape = RoundedCornerShape(14.dp)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (uiState.resendCountdown > 0) {
                                        Text(
                                            text = "Resend OTP in ${uiState.resendCountdown}s",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    } else {
                                        TextButton(
                                            onClick = {
                                                if (activity != null) {
                                                    viewModel.sendVerificationCode(activity)
                                                }
                                            }
                                        ) {
                                            Text("Resend Code", color = TealPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    TextButton(
                                        onClick = { viewModel.useDemoOtp() }
                                    ) {
                                        Text("⚡ Use Code 123456", color = CyanAccent, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { viewModel.verifyOtp() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("verify_otp_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TealPrimary,
                                        contentColor = Color.White
                                    ),
                                    enabled = !uiState.isLoading && uiState.otpCode.length == 6
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = Color.White,
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        Text(
                                            text = "Verify & Continue",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }

                            AuthStep.COMPLETE_PROFILE -> {
                                Text(
                                    text = if (uiState.userRole == "provider") "Destination Provider Onboarding" else "Complete Traveler Profile",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                Text(
                                    text = if (uiState.userRole == "provider")
                                        "Configure your verified service badge and dispatch jurisdiction."
                                    else
                                        "Your emergency safety identity is stored in your personal encrypted cloud record.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                                )

                                // Portal Role Selection Chips
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val isTraveler = uiState.userRole == "traveler"
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.onRoleSelected("traveler") },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isTraveler) TealPrimary else Color.Transparent
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = if (isTraveler) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Traveler",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = if (isTraveler) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isTraveler) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }

                                    val isProvider = uiState.userRole == "provider"
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.onRoleSelected("provider") },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isProvider) EmeraldSafe else Color.Transparent
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Shield,
                                                contentDescription = null,
                                                tint = if (isProvider) Navy900 else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Verified Provider",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = if (isProvider) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isProvider) Navy900 else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = uiState.name,
                                    onValueChange = { viewModel.onNameChanged(it) },
                                    label = { Text(if (uiState.userRole == "provider") "Officer / Provider Name" else "Full Legal Name") },
                                    placeholder = { Text(if (uiState.userRole == "provider") "e.g. Inspector Rajesh Verma" else "e.g. Maya Lin") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Name",
                                            tint = TealPrimary
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("name_input"),
                                    shape = RoundedCornerShape(14.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                if (uiState.userRole == "traveler") {
                                    OutlinedTextField(
                                        value = uiState.homeCountry,
                                        onValueChange = { viewModel.onHomeCountryChanged(it) },
                                        label = { Text("Home Country / Citizenship") },
                                        placeholder = { Text("e.g. India, Germany, USA, Australia") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Public,
                                                contentDescription = "Country",
                                                tint = TealPrimary
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("country_input"),
                                        shape = RoundedCornerShape(14.dp)
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Emergency Contacts Section Surface
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Phone,
                                                    contentDescription = null,
                                                    tint = CoralSOS,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Primary Emergency Contact (Family / Guardian)",
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            OutlinedTextField(
                                                value = uiState.emergencyContactName1,
                                                onValueChange = { viewModel.onEmergencyContact1NameChanged(it) },
                                                label = { Text("Contact Full Name") },
                                                placeholder = { Text("e.g. Sarah Jenkins (Mother)") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("emergency_contact_name_1_input"),
                                                shape = RoundedCornerShape(10.dp)
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            OutlinedTextField(
                                                value = uiState.emergencyContactPhone1,
                                                onValueChange = { viewModel.onEmergencyContact1PhoneChanged(it) },
                                                label = { Text("Contact Phone / WhatsApp") },
                                                placeholder = { Text("e.g. +1 555 019 2834") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("emergency_contact_phone_1_input"),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Medical / Safety Information Surface
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Security,
                                                    contentDescription = null,
                                                    tint = TealPrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Medical & Emergency Health (Optional)",
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            OutlinedTextField(
                                                value = uiState.medicalBloodGroup,
                                                onValueChange = { viewModel.onMedicalBloodGroupChanged(it) },
                                                label = { Text("Blood Group (e.g. O+, A+, B+, AB+)") },
                                                placeholder = { Text("e.g. O+") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("medical_blood_group_input"),
                                                shape = RoundedCornerShape(10.dp)
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            OutlinedTextField(
                                                value = uiState.medicalAllergiesNotes,
                                                onValueChange = { viewModel.onMedicalNotesChanged(it) },
                                                label = { Text("Allergies / Special Medical Notes") },
                                                placeholder = { Text("e.g. Penicillin allergy, carry EpiPen") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("medical_notes_input"),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                        }
                                    }
                                } else {
                                    // Provider category dropdown
                                    val providerCategories = listOf("Tourist Police", "Safe Transport", "Emergency Medical", "Certified Guide", "Embassy Help")
                                    var expandedType by remember { mutableStateOf(false) }

                                    ExposedDropdownMenuBox(
                                        expanded = expandedType,
                                        onExpandedChange = { expandedType = !expandedType },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = uiState.providerType,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Service Department / Type") },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Security,
                                                    contentDescription = null,
                                                    tint = EmeraldSafe
                                                )
                                            },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            shape = RoundedCornerShape(14.dp)
                                        )

                                        ExposedDropdownMenu(
                                            expanded = expandedType,
                                            onDismissRequest = { expandedType = false }
                                        ) {
                                            providerCategories.forEach { type ->
                                                DropdownMenuItem(
                                                    text = { Text(type) },
                                                    onClick = {
                                                        viewModel.onProviderTypeChanged(type)
                                                        expandedType = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = uiState.agencyName,
                                        onValueChange = { viewModel.onAgencyNameChanged(it) },
                                        label = { Text("Agency / Unit Name") },
                                        placeholder = { Text("e.g. Delhi Police Tourist Unit #04") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Shield,
                                                contentDescription = null,
                                                tint = TealPrimary
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // ID Proof Document Type
                                    val idTypes = listOf(
                                        "Police Warrant & Law Enforcement Badge ID",
                                        "Ministry of Tourism Guide Accreditation Card",
                                        "Commercial Transport Permit & PSV Driver Badge",
                                        "State Medical Council / EMS Registration",
                                        "National Government ID (Aadhaar/Passport/Voter ID)"
                                    )
                                    var expandedIdType by remember { mutableStateOf(false) }

                                    ExposedDropdownMenuBox(
                                        expanded = expandedIdType,
                                        onExpandedChange = { expandedIdType = !expandedIdType },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = uiState.idProofType,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Official ID Proof Document Type") },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.AssignmentInd,
                                                    contentDescription = null,
                                                    tint = EmeraldSafe
                                                )
                                            },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedIdType) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            shape = RoundedCornerShape(14.dp)
                                        )

                                        ExposedDropdownMenu(
                                            expanded = expandedIdType,
                                            onDismissRequest = { expandedIdType = false }
                                        ) {
                                            idTypes.forEach { type ->
                                                DropdownMenuItem(
                                                    text = { Text(type, style = MaterialTheme.typography.bodySmall) },
                                                    onClick = {
                                                        viewModel.onIdProofTypeChanged(type)
                                                        expandedIdType = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = uiState.idProofNumber,
                                        onValueChange = { viewModel.onIdProofNumberChanged(it) },
                                        label = { Text("Government / Department ID Proof #") },
                                        placeholder = { Text("e.g. IND-POL-DL-8842-TP") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Badge,
                                                contentDescription = null,
                                                tint = EmeraldSafe
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("id_proof_number_input"),
                                        shape = RoundedCornerShape(14.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = uiState.issuingAuthority,
                                        onValueChange = { viewModel.onIssuingAuthorityChanged(it) },
                                        label = { Text("Issuing Authority / Ministry Name") },
                                        placeholder = { Text("e.g. Delhi Police Dept / Ministry of Tourism") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Security,
                                                contentDescription = null,
                                                tint = TealPrimary
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("issuing_authority_input"),
                                        shape = RoundedCornerShape(14.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = uiState.badgeNumber,
                                        onValueChange = { viewModel.onBadgeNumberChanged(it) },
                                        label = { Text("Official Badge / PSV License ID") },
                                        placeholder = { Text("e.g. DL-TP-8842") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = TealPrimary
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("badge_number_input"),
                                        shape = RoundedCornerShape(14.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = uiState.designationRank,
                                        onValueChange = { viewModel.onDesignationRankChanged(it) },
                                        label = { Text("Designation / Official Rank") },
                                        placeholder = { Text("e.g. Inspector / Senior Escort Lead") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = TealPrimary
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = uiState.officialEmail,
                                        onValueChange = { viewModel.onOfficialEmailChanged(it) },
                                        label = { Text("Department Email / Dispatch Channel") },
                                        placeholder = { Text("e.g. dispatch@delhipolice.gov.in") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = null,
                                                tint = TealPrimary
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = uiState.serviceArea,
                                        onValueChange = { viewModel.onServiceAreaChanged(it) },
                                        label = { Text("Service Jurisdiction / Operating Beat") },
                                        placeholder = { Text("e.g. Central Delhi & Heritage Zone") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Public,
                                                contentDescription = null,
                                                tint = TealPrimary
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp)
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Document Attachment Box
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (uiState.isIdProofAttached) EmeraldSafe.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        border = BorderStroke(1.dp, if (uiState.isIdProofAttached) EmeraldSafe.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (uiState.isIdProofAttached) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                                    contentDescription = null,
                                                    tint = if (uiState.isIdProofAttached) EmeraldSafe else MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = if (uiState.isIdProofAttached) "Official ID Scan Attached" else "ID Document Proof Required",
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (uiState.isIdProofAttached) EmeraldSafe else MaterialTheme.colorScheme.onSurface
                                                        )
                                                    )
                                                    Text(
                                                        text = uiState.idProofDocumentName,
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            fontSize = 11.sp
                                                        )
                                                    )
                                                }
                                                OutlinedButton(
                                                    onClick = {
                                                        viewModel.onIdProofDocumentAttached("official_gov_id_scan_${System.currentTimeMillis().toString().takeLast(4)}.pdf")
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(32.dp)
                                                ) {
                                                    Text("Re-Scan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Legal Declaration Checkbox
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.onDeclarationToggled(!uiState.isDeclarationAccepted) }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Checkbox(
                                            checked = uiState.isDeclarationAccepted,
                                            onCheckedChange = { viewModel.onDeclarationToggled(it) },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = EmeraldSafe,
                                                checkmarkColor = Navy900
                                            ),
                                            modifier = Modifier.testTag("provider_declaration_checkbox")
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "I solemnly affirm and verify under penalty of law that I am an authorized, licensed service provider/officer and all submitted ID proofs are authentic.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp
                                            ),
                                            modifier = Modifier.padding(top = 10.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                var expandedLang by remember { mutableStateOf(false) }

                                ExposedDropdownMenuBox(
                                    expanded = expandedLang,
                                    onExpandedChange = { expandedLang = !expandedLang },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = uiState.preferredLanguage,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Preferred Language") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Language,
                                                contentDescription = null,
                                                tint = TealPrimary
                                            )
                                        },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLang) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(14.dp)
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

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = { viewModel.submitProfile() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("save_profile_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (uiState.userRole == "provider") EmeraldSafe else TealPrimary,
                                        contentColor = if (uiState.userRole == "provider") Navy900 else Color.White
                                    ),
                                    enabled = !uiState.isLoading && uiState.name.isNotBlank() && (uiState.userRole == "provider" || uiState.homeCountry.isNotBlank())
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = if (uiState.userRole == "provider") Navy900 else Color.White,
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        Text(
                                            text = if (uiState.userRole == "provider") "Activate Provider Portal" else "Complete Profile & Start",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Messages Feedback
                        AnimatedVisibility(visible = uiState.errorMessage != null) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                color = CoralSOS.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = uiState.errorMessage ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CoralSOS,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(12.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        AnimatedVisibility(visible = uiState.successMessage != null && uiState.errorMessage == null) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                color = EmeraldSafe.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = uiState.successMessage ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = EmeraldSafe,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(12.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Trust & Security Notice
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldSafe,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Encrypted Firebase Authentication & Real-Time Sync",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (uiState.showProviderVerificationDialog) {
        ProviderVerificationModalDialog(
            uiState = uiState,
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderVerificationModalDialog(
    uiState: AuthUiState,
    viewModel: AuthViewModel
) {
    val scrollState = rememberScrollState()

    val providerCategories = listOf(
        "Tourist Police",
        "Safe Transport",
        "Emergency Medical",
        "Certified Guide",
        "Embassy Help"
    )

    val idProofTypes = listOf(
        "Police Warrant & Law Enforcement Badge ID",
        "Ministry of Tourism Guide Accreditation Card",
        "Commercial Transport Permit & PSV Driver Badge",
        "State Medical Council / EMS Registration",
        "National Government ID (Aadhaar/Passport/Voter ID)"
    )

    Dialog(
        onDismissRequest = { viewModel.dismissProviderVerificationDialog() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Navy900,
            tonalElevation = 12.dp,
            border = BorderStroke(1.5.dp, EmeraldSafe.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldSafe.copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = EmeraldSafe,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Provider Verification",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Government ID & Badge Registration",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = EmeraldSafe,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.dismissProviderVerificationDialog() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text(
                            text = "✕",
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Navy800)
                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Form Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    // Presets Bar
                    Text(
                        text = "Quick Demo Department Presets:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Tourist Police", "Safe Transport", "Emergency Medical", "Certified Guide").forEach { preset ->
                            val isSelected = uiState.providerType == preset
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.applyProviderPreset(preset) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) EmeraldSafe else Navy800,
                                border = BorderStroke(1.dp, if (isSelected) EmeraldSafe else Navy800)
                            ) {
                                Text(
                                    text = when (preset) {
                                        "Tourist Police" -> "👮 Police"
                                        "Safe Transport" -> "🚕 Cab"
                                        "Emergency Medical" -> "🚑 EMS"
                                        else -> "🧭 Guide"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Navy900 else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Officer Name
                    Text(
                        text = "1. Officer / Provider Identification",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = EmeraldSafe,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.onNameChanged(it) },
                        label = { Text("Full Legal / Officer Name *") },
                        placeholder = { Text("e.g. Inspector Rajesh Verma") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldSafe)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_provider_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldSafe,
                            unfocusedBorderColor = Navy800,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = EmeraldSafe,
                            unfocusedLabelColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Department Type Dropdown
                    var expandedType by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = { expandedType = !expandedType },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.providerType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Service Department / Type *") },
                            leadingIcon = {
                                Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldSafe)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldSafe,
                                unfocusedBorderColor = Navy800,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = EmeraldSafe,
                                unfocusedLabelColor = Color.LightGray
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false },
                            modifier = Modifier.background(Navy800)
                        ) {
                            providerCategories.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type, color = Color.White) },
                                    onClick = {
                                        viewModel.onProviderTypeChanged(type)
                                        expandedType = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.agencyName,
                        onValueChange = { viewModel.onAgencyNameChanged(it) },
                        label = { Text("Agency / Unit / Fleet Name *") },
                        placeholder = { Text("e.g. Delhi Police Tourist Safety Wing") },
                        leadingIcon = {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldSafe)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_agency_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldSafe,
                            unfocusedBorderColor = Navy800,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = EmeraldSafe,
                            unfocusedLabelColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ID Proof Credentials
                    Text(
                        text = "2. Government / Accreditation ID Proof",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = EmeraldSafe,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    var expandedIdType by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedIdType,
                        onExpandedChange = { expandedIdType = !expandedIdType },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.idProofType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("ID Proof Document Type *") },
                            leadingIcon = {
                                Icon(Icons.Default.AssignmentInd, contentDescription = null, tint = EmeraldSafe)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedIdType) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldSafe,
                                unfocusedBorderColor = Navy800,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = EmeraldSafe,
                                unfocusedLabelColor = Color.LightGray
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expandedIdType,
                            onDismissRequest = { expandedIdType = false },
                            modifier = Modifier.background(Navy800)
                        ) {
                            idProofTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type, color = Color.White, fontSize = 12.sp) },
                                    onClick = {
                                        viewModel.onIdProofTypeChanged(type)
                                        expandedIdType = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.idProofNumber,
                        onValueChange = { viewModel.onIdProofNumberChanged(it) },
                        label = { Text("Official ID Proof / License # *") },
                        placeholder = { Text("e.g. IND-POL-DL-8842-TP") },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = EmeraldSafe)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_id_proof_number_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldSafe,
                            unfocusedBorderColor = Navy800,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = EmeraldSafe,
                            unfocusedLabelColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.issuingAuthority,
                        onValueChange = { viewModel.onIssuingAuthorityChanged(it) },
                        label = { Text("Issuing Authority / Ministry *") },
                        placeholder = { Text("e.g. Delhi Police Department / Ministry of Tourism") },
                        leadingIcon = {
                            Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldSafe)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_issuing_authority_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldSafe,
                            unfocusedBorderColor = Navy800,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = EmeraldSafe,
                            unfocusedLabelColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.badgeNumber,
                        onValueChange = { viewModel.onBadgeNumberChanged(it) },
                        label = { Text("Badge / PSV Driver ID *") },
                        placeholder = { Text("e.g. DL-TP-8842") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldSafe)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_badge_number_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldSafe,
                            unfocusedBorderColor = Navy800,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = EmeraldSafe,
                            unfocusedLabelColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.designationRank,
                        onValueChange = { viewModel.onDesignationRankChanged(it) },
                        label = { Text("Official Designation / Rank") },
                        placeholder = { Text("e.g. Inspector / Senior Escort Lead") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldSafe)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldSafe,
                            unfocusedBorderColor = Navy800,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = EmeraldSafe,
                            unfocusedLabelColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.officialEmail,
                        onValueChange = { viewModel.onOfficialEmailChanged(it) },
                        label = { Text("Department Official Email") },
                        placeholder = { Text("e.g. rajesh.verma@delhipolice.gov.in") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = EmeraldSafe)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_email_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldSafe,
                            unfocusedBorderColor = Navy800,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = EmeraldSafe,
                            unfocusedLabelColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.serviceArea,
                        onValueChange = { viewModel.onServiceAreaChanged(it) },
                        label = { Text("Jurisdiction / Operating Corridor") },
                        placeholder = { Text("e.g. Central Delhi & Connaught Place Zone") },
                        leadingIcon = {
                            Icon(Icons.Default.Public, contentDescription = null, tint = EmeraldSafe)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldSafe,
                            unfocusedBorderColor = Navy800,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = EmeraldSafe,
                            unfocusedLabelColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Document Scan Attachment Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Navy800,
                        border = BorderStroke(1.dp, if (uiState.isIdProofAttached) EmeraldSafe else Navy800)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (uiState.isIdProofAttached) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = if (uiState.isIdProofAttached) EmeraldSafe else CyanAccent,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (uiState.isIdProofAttached) "ID Proof Document Attached" else "ID Document Required",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.isIdProofAttached) EmeraldSafe else Color.White
                                    )
                                )
                                Text(
                                    text = uiState.idProofDocumentName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.LightGray,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    viewModel.onIdProofDocumentAttached("official_gov_id_scan_${System.currentTimeMillis().toString().takeLast(4)}.pdf")
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldSafe),
                                border = BorderStroke(1.dp, EmeraldSafe)
                            ) {
                                Text("Attach", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Legal Affirmation Checkbox
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onDeclarationToggled(!uiState.isDeclarationAccepted) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = uiState.isDeclarationAccepted,
                            onCheckedChange = { viewModel.onDeclarationToggled(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = EmeraldSafe,
                                checkmarkColor = Navy900,
                                uncheckedColor = Color.LightGray
                            ),
                            modifier = Modifier.testTag("dialog_declaration_checkbox")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "I solemnly affirm and verify under penalty of law that I am an authorized, licensed service provider/officer and all submitted ID proofs are authentic.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    // Error Message within dialog
                    AnimatedVisibility(visible = uiState.errorMessage != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            color = CoralSOS.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CoralSOS.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = uiState.errorMessage ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CoralSOS,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Navy800)
                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.dismissProviderVerificationDialog() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Navy700)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { viewModel.verifyAndLoginAsProvider() },
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp)
                            .testTag("dialog_submit_provider_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldSafe,
                            contentColor = Navy900
                        ),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Navy900,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Submit ID Proof & Open Portal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
