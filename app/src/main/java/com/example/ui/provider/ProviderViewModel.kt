package com.example.ui.provider

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ProviderAssistanceRequest
import com.example.data.model.ProviderDutyStats
import com.example.data.model.RiskReport
import com.example.data.model.SosEvent
import com.example.data.model.UserProfile
import com.example.data.repository.AuthRepository
import com.example.data.repository.FirestoreRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ProviderUiState(
    val currentUserId: String = "",
    val profile: UserProfile = UserProfile(
        name = "Inspector Rajesh Verma",
        role = "provider",
        providerType = "Tourist Police",
        agencyName = "Delhi Police - Tourist Safety Command (Station #04)",
        badgeNumber = "DL-TP-8842",
        serviceArea = "Central Delhi & Heritage Zone",
        dutyStatus = "ON_DUTY",
        trustScore = 4.98,
        ratingCount = 340,
        isVerifiedProvider = true
    ),
    val dutyStatus: String = "ON_DUTY", // "ON_DUTY", "OFF_DUTY"
    val activeSosAlerts: List<SosEvent> = emptyList(),
    val assistanceRequests: List<ProviderAssistanceRequest> = emptyList(),
    val riskReports: List<RiskReport> = emptyList(),
    val stats: ProviderDutyStats = ProviderDutyStats(),
    val selectedTab: Int = 0,
    val isPublishing: Boolean = false,
    val broadcastTitle: String = "",
    val broadcastDetails: String = "",
    val broadcastCategory: String = "Scam Warning",
    val broadcastSeverity: String = "medium",
    val actionFeedbackMessage: String? = null,
    val acknowledgedSosIds: Set<String> = emptySet()
)

class ProviderViewModel(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val TAG = "ProviderViewModel"
    private val _uiState = MutableStateFlow(ProviderUiState())
    val uiState: StateFlow<ProviderUiState> = _uiState.asStateFlow()

    init {
        loadProviderData()
        observeActiveSosEvents()
        observeAssistanceRequests()
        observeRiskReports()
    }

    fun loadProviderData() {
        val uid = authRepository.currentUserId
        if (uid.isNotBlank()) {
            _uiState.value = _uiState.value.copy(currentUserId = uid)
            viewModelScope.launch {
                val profile = firestoreRepository.getUserProfile(uid)
                if (profile != null) {
                    _uiState.value = _uiState.value.copy(
                        profile = profile,
                        dutyStatus = profile.dutyStatus
                    )
                }
            }
        }
    }

    private fun observeActiveSosEvents() {
        viewModelScope.launch {
            firestoreRepository.observeAllActiveSosEvents()
                .catch { e -> Log.w(TAG, "observeAllActiveSosEvents error: ${e.message}") }
                .collectLatest { events ->
                    _uiState.value = _uiState.value.copy(
                        activeSosAlerts = events,
                        stats = _uiState.value.stats.copy(
                            activeSosCount = events.size
                        )
                    )
                }
        }
    }

    private fun observeAssistanceRequests() {
        viewModelScope.launch {
            firestoreRepository.observeProviderAssistanceRequests()
                .catch { e -> Log.w(TAG, "observeAssistanceRequests error: ${e.message}") }
                .collectLatest { requests ->
                    val activeDispatches = requests.count { it.status == "dispatched" || it.status == "pending" }
                    _uiState.value = _uiState.value.copy(
                        assistanceRequests = requests,
                        stats = _uiState.value.stats.copy(
                            activeDispatches = activeDispatches
                        )
                    )
                }
        }
    }

    private fun observeRiskReports() {
        viewModelScope.launch {
            firestoreRepository.observeRiskReports()
                .catch { e -> Log.w(TAG, "observeRiskReports error: ${e.message}") }
                .collectLatest { reports ->
                    _uiState.value = _uiState.value.copy(riskReports = reports)
                }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tabIndex)
    }

    fun toggleDutyStatus() {
        val newStatus = if (_uiState.value.dutyStatus == "ON_DUTY") "OFF_DUTY" else "ON_DUTY"
        _uiState.value = _uiState.value.copy(
            dutyStatus = newStatus,
            profile = _uiState.value.profile.copy(dutyStatus = newStatus),
            actionFeedbackMessage = if (newStatus == "ON_DUTY") "🟢 Radar Active: You are now ON DUTY receiving live alerts." else "⚪ Duty Paused: Radar set to OFF DUTY."
        )
        viewModelScope.launch {
            if (_uiState.value.currentUserId.isNotBlank()) {
                firestoreRepository.updateProviderDutyStatus(_uiState.value.currentUserId, newStatus)
            }
        }
    }

    fun acknowledgeSos(eventId: String) {
        val updatedSet = _uiState.value.acknowledgedSosIds + eventId
        _uiState.value = _uiState.value.copy(
            acknowledgedSosIds = updatedSet,
            actionFeedbackMessage = "🚔 Distress Beacon #$eventId acknowledged. Unit dispatched to coordinates."
        )
    }

    fun resolveSos(eventId: String) {
        viewModelScope.launch {
            firestoreRepository.resolveSosEvent(eventId)
            _uiState.value = _uiState.value.copy(
                activeSosAlerts = _uiState.value.activeSosAlerts.filter { it.eventId != eventId },
                acknowledgedSosIds = _uiState.value.acknowledgedSosIds - eventId,
                stats = _uiState.value.stats.copy(
                    resolvedToday = _uiState.value.stats.resolvedToday + 1
                ),
                actionFeedbackMessage = "✅ Emergency Beacon #$eventId marked resolved and safe."
            )
        }
    }

    fun updateRequestStatus(requestId: String, status: String) {
        firestoreRepository.updateAssistanceRequestStatus(requestId, status)
        val updatedList = _uiState.value.assistanceRequests.map {
            if (it.id == requestId) it.copy(status = status) else it
        }
        _uiState.value = _uiState.value.copy(
            assistanceRequests = updatedList,
            actionFeedbackMessage = "Request #$requestId updated to '${status.uppercase()}'."
        )
    }

    fun simulateIncomingDistress() {
        val event = firestoreRepository.simulateIncomingDistressAlert()
        _uiState.value = _uiState.value.copy(
            activeSosAlerts = listOf(event) + _uiState.value.activeSosAlerts.filter { it.eventId != event.eventId },
            actionFeedbackMessage = "🚨 LIVE ALERT: Incoming Traveler Distress Beacon received from Connaught Place!"
        )
    }

    fun onBroadcastTitleChanged(title: String) {
        _uiState.value = _uiState.value.copy(broadcastTitle = title)
    }

    fun onBroadcastDetailsChanged(details: String) {
        _uiState.value = _uiState.value.copy(broadcastDetails = details)
    }

    fun onBroadcastCategoryChanged(category: String) {
        _uiState.value = _uiState.value.copy(broadcastCategory = category)
    }

    fun onBroadcastSeverityChanged(severity: String) {
        _uiState.value = _uiState.value.copy(broadcastSeverity = severity)
    }

    fun publishBroadcastAdvisory() {
        val title = _uiState.value.broadcastTitle.trim()
        val details = _uiState.value.broadcastDetails.trim()
        if (title.isBlank() || details.isBlank()) {
            _uiState.value = _uiState.value.copy(actionFeedbackMessage = "Please enter both an alert headline and advisory details.")
            return
        }

        _uiState.value = _uiState.value.copy(isPublishing = true)

        val report = RiskReport(
            id = "advisory_${System.currentTimeMillis().toString().takeLast(6)}",
            reportedBy = "${_uiState.value.profile.providerType} - ${_uiState.value.profile.agencyName.ifBlank { "Verified Authority" }}",
            riskType = _uiState.value.broadcastCategory,
            description = "$title: $details",
            severity = _uiState.value.broadcastSeverity,
            lat = 28.6139,
            lng = 77.2090,
            destinationName = _uiState.value.profile.serviceArea,
            createdAt = Timestamp.now()
        )

        viewModelScope.launch {
            firestoreRepository.addRiskReport(report)
            _uiState.value = _uiState.value.copy(
                isPublishing = false,
                broadcastTitle = "",
                broadcastDetails = "",
                actionFeedbackMessage = "📢 Verified Advisory published to all active travelers at destination."
            )
        }
    }

    fun openMapsCoordinates(context: Context, lat: Double, lng: Double, label: String = "Traveler Location") {
        try {
            val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    fun dialPhone(context: Context, phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.w(TAG, "Failed dialing phone: $phone", e)
        }
    }

    fun clearFeedbackMessage() {
        _uiState.value = _uiState.value.copy(actionFeedbackMessage = null)
    }

    fun logout(onLoggedOut: () -> Unit) {
        authRepository.signOut()
        onLoggedOut()
    }
}
