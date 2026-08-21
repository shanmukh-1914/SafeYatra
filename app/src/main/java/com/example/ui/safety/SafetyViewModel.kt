package com.example.ui.safety

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EmergencyContact
import com.example.data.model.RiskReport
import com.example.data.model.SosEvent
import com.example.data.repository.AuthRepository
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.LocationTrackingManager
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SafetyUiState(
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val riskReports: List<RiskReport> = emptyList(),
    val activeSosEvent: SosEvent? = null,
    val isInitialLoading: Boolean = true,
    val isAddingContact: Boolean = false,
    val contactName: String = "",
    val contactPhone: String = "",
    val contactRelation: String = "Family",
    val isReportingRisk: Boolean = false,
    val reportType: String = "Scam Alert",
    val reportDescription: String = "",
    val reportSeverity: String = "medium",
    val isActionLoading: Boolean = false,
    val actionLoadingMessage: String = "",
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class SafetyViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
    private val locationTracker: LocationTrackingManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SafetyUiState())
    val uiState: StateFlow<SafetyUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        val userId = authRepository.currentUserId

        viewModelScope.launch {
            firestoreRepository.observeEmergencyContacts(userId).collect { contacts ->
                _uiState.value = _uiState.value.copy(
                    emergencyContacts = contacts,
                    isInitialLoading = false
                )
            }
        }

        viewModelScope.launch {
            firestoreRepository.observeUserSosEvents(userId).collect { events ->
                val active = events.firstOrNull { it.status == "active" }
                _uiState.value = _uiState.value.copy(activeSosEvent = active)
            }
        }

        viewModelScope.launch {
            firestoreRepository.observeRiskReports().collect { reports ->
                _uiState.value = _uiState.value.copy(riskReports = reports)
            }
        }
    }

    fun openAddContactDialog() {
        _uiState.value = _uiState.value.copy(
            isAddingContact = true,
            contactName = "",
            contactPhone = "",
            contactRelation = "Family",
            errorMessage = null
        )
    }

    fun closeAddContactDialog() {
        _uiState.value = _uiState.value.copy(isAddingContact = false)
    }

    fun onContactNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(contactName = name)
    }

    fun onContactPhoneChanged(phone: String) {
        _uiState.value = _uiState.value.copy(contactPhone = phone)
    }

    fun onContactRelationChanged(rel: String) {
        _uiState.value = _uiState.value.copy(contactRelation = rel)
    }

    fun saveContact() {
        val userId = authRepository.currentUserId
        val name = _uiState.value.contactName.trim()
        val phone = _uiState.value.contactPhone.trim()

        if (name.isBlank() || phone.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please provide name and phone number.")
            return
        }

        val contact = EmergencyContact(
            name = name,
            phone = phone,
            relationship = _uiState.value.contactRelation
        )

        // Instant UI update
        _uiState.value = _uiState.value.copy(
            isActionLoading = false,
            isAddingContact = false,
            successMessage = "Added $name to emergency network."
        )

        viewModelScope.launch {
            firestoreRepository.addEmergencyContact(userId, contact)
        }
    }

    fun deleteContact(contactId: String) {
        val userId = authRepository.currentUserId
        viewModelScope.launch {
            firestoreRepository.deleteEmergencyContact(userId, contactId)
        }
    }

    fun openReportRiskDialog() {
        _uiState.value = _uiState.value.copy(
            isReportingRisk = true,
            reportType = "Scam Alert",
            reportDescription = "",
            reportSeverity = "medium",
            errorMessage = null
        )
    }

    fun closeReportRiskDialog() {
        _uiState.value = _uiState.value.copy(isReportingRisk = false)
    }

    fun onReportTypeChanged(type: String) {
        _uiState.value = _uiState.value.copy(reportType = type)
    }

    fun onReportDescriptionChanged(desc: String) {
        _uiState.value = _uiState.value.copy(reportDescription = desc)
    }

    fun onReportSeverityChanged(sev: String) {
        _uiState.value = _uiState.value.copy(reportSeverity = sev)
    }

    fun submitRiskReport() {
        val userId = authRepository.currentUserId
        val desc = _uiState.value.reportDescription.trim()

        if (desc.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please describe the risk or hazard.")
            return
        }

        _uiState.value = _uiState.value.copy(isActionLoading = true, errorMessage = null)

        viewModelScope.launch {
            val loc = locationTracker.trackingState.value
            val report = RiskReport(
                lat = loc.latitude,
                lng = loc.longitude,
                riskType = _uiState.value.reportType,
                description = desc,
                reporterId = userId,
                createdAt = Timestamp.now()
            )

            val result = firestoreRepository.addRiskReport(report)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isActionLoading = false,
                        isReportingRisk = false,
                        successMessage = "Report broadcasted to nearby travelers."
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isActionLoading = false,
                        errorMessage = "Failed to submit risk report: ${error.message}"
                    )
                }
            )
        }
    }

    fun triggerSos() {
        val userId = authRepository.currentUserId

        _uiState.value = _uiState.value.copy(
            isActionLoading = true,
            actionLoadingMessage = "Acquiring fresh high-accuracy GPS lock for first responders...",
            errorMessage = null
        )

        viewModelScope.launch {
            // Force a fresh high-accuracy GPS reading with maxUpdateAge = 0
            val freshLoc = locationTracker.getCurrentHighAccuracyLocation(timeoutMs = 8000L)
            val lat = freshLoc?.latitude ?: locationTracker.trackingState.value.latitude
            val lng = freshLoc?.longitude ?: locationTracker.trackingState.value.longitude
            val accuracy = freshLoc?.accuracy ?: locationTracker.trackingState.value.accuracyMeters

            _uiState.value = _uiState.value.copy(
                actionLoadingMessage = "Transmitting emergency telemetry (accuracy: ±${accuracy.toInt()}m)..."
            )

            val contactPhones = _uiState.value.emergencyContacts.map { "${it.name} (${it.phone})" }
            val result = firestoreRepository.triggerSosEvent(
                userId = userId,
                lat = lat,
                lng = lng,
                contacts = contactPhones,
                accuracyMeters = accuracy
            )
            result.fold(
                onSuccess = { eventId ->
                    locationTracker.setActiveSosEventId(eventId)
                    _uiState.value = _uiState.value.copy(
                        isActionLoading = false,
                        actionLoadingMessage = "",
                        successMessage = "EMERGENCY SOS TRANSMITTED (GPS accuracy: ±${accuracy.toInt()}m)."
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isActionLoading = false,
                        actionLoadingMessage = "",
                        errorMessage = "SOS error: ${error.message}"
                    )
                }
            )
        }
    }

    fun resolveSos(eventId: String) {
        locationTracker.setActiveSosEventId(null)
        _uiState.value = _uiState.value.copy(successMessage = "SOS event marked resolved.")
        viewModelScope.launch {
            firestoreRepository.resolveSosEvent(eventId)
        }
    }

    fun dialHelpline(context: Context, number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("SafetyViewModel", "Could not dial: ${e.message}")
        }
    }
}
