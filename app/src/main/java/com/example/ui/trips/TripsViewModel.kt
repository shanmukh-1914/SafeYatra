package com.example.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Trip
import com.example.data.repository.AuthRepository
import com.example.data.repository.FirestoreRepository
import com.example.data.service.GeminiTripPlannerService
import com.example.data.service.NominatimGeocodingService
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TripsUiState(
    val trips: List<Trip> = emptyList(),
    val isInitialLoading: Boolean = true,
    val filterStatus: String = "ALL", // "ALL", "ACTIVE", "PLANNING", "COMPLETED"
    val isCreatingTrip: Boolean = false,
    val destinationName: String = "",
    val destinationLat: Double = 28.6139,
    val destinationLng: Double = 77.2090,
    val startDate: String = "2026-09-01",
    val endDate: String = "2026-09-05",
    val selectedInterests: Set<String> = setOf("culture", "food", "adventure"),
    val isLoading: Boolean = false,
    val loadingStep: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val selectedTripForDetail: Trip? = null
)

class TripsViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
    private val geocodingService: NominatimGeocodingService = NominatimGeocodingService(),
    private val geminiTripPlannerService: GeminiTripPlannerService = GeminiTripPlannerService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripsUiState())
    val uiState: StateFlow<TripsUiState> = _uiState.asStateFlow()

    init {
        observeTrips()
    }

    private fun observeTrips() {
        val userId = authRepository.currentUserId
        viewModelScope.launch {
            firestoreRepository.observeUserTrips(userId).collect { list ->
                _uiState.value = _uiState.value.copy(
                    trips = list,
                    isInitialLoading = false
                )

                // If currently viewing a trip detail, keep it updated
                val currentDetailId = _uiState.value.selectedTripForDetail?.tripId
                if (currentDetailId != null) {
                    val updated = list.find { it.tripId == currentDetailId }
                    if (updated != null) {
                        _uiState.value = _uiState.value.copy(selectedTripForDetail = updated)
                    }
                }
            }
        }
    }

    fun setFilter(filter: String) {
        _uiState.value = _uiState.value.copy(filterStatus = filter)
    }

    fun openCreateDialog() {
        _uiState.value = _uiState.value.copy(
            isCreatingTrip = true,
            destinationName = "",
            startDate = "2026-09-01",
            endDate = "2026-09-05",
            selectedInterests = setOf("culture", "food", "adventure"),
            errorMessage = null,
            successMessage = null,
            loadingStep = ""
        )
    }

    fun closeCreateDialog() {
        _uiState.value = _uiState.value.copy(isCreatingTrip = false, isLoading = false, loadingStep = "")
    }

    fun selectTripForDetail(trip: Trip?) {
        _uiState.value = _uiState.value.copy(selectedTripForDetail = trip)
    }

    fun onDestinationChanged(name: String) {
        _uiState.value = _uiState.value.copy(destinationName = name)
    }

    fun onDatesChanged(start: String, end: String) {
        _uiState.value = _uiState.value.copy(startDate = start, endDate = end)
    }

    fun toggleInterest(interestKey: String) {
        val current = _uiState.value.selectedInterests.toMutableSet()
        if (current.contains(interestKey)) {
            current.remove(interestKey)
        } else {
            current.add(interestKey)
        }
        _uiState.value = _uiState.value.copy(selectedInterests = current)
    }

    fun createTrip() {
        val userId = authRepository.currentUserId
        val dest = _uiState.value.destinationName.trim()
        if (dest.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a destination name.")
            return
        }

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            loadingStep = "Stage 1/4: Geocoding $dest GPS coordinates..."
        )

        viewModelScope.launch {
            try {
                // Step 1: Geocode destination using OpenStreetMap Nominatim
                val geocodeResult = geocodingService.geocodeDestination(dest)

                _uiState.value = _uiState.value.copy(
                    loadingStep = "Stage 2/4: Consulting Gemini AI for safety advisories..."
                )

                // Step 2: Call Gemini API for custom day-by-day itinerary, attractions, scam patterns, and etiquette
                val aiPlan = geminiTripPlannerService.generateTripPlan(
                    destinationName = dest,
                    startDate = _uiState.value.startDate.ifBlank { "2026-09-01" },
                    endDate = _uiState.value.endDate.ifBlank { "2026-09-05" },
                    interests = _uiState.value.selectedInterests.toList(),
                    onProgressUpdate = { progressText ->
                        _uiState.value = _uiState.value.copy(loadingStep = progressText)
                    }
                )

                _uiState.value = _uiState.value.copy(
                    loadingStep = "Stage 4/4: Committing verified itinerary to cloud records..."
                )

                // Step 3: Create Firestore document in trips with status "planning"
                val newTrip = Trip(
                    userId = userId,
                    destinationName = dest,
                    destinationLat = geocodeResult.latitude,
                    destinationLng = geocodeResult.longitude,
                    startDate = _uiState.value.startDate.ifBlank { "2026-09-01" },
                    endDate = _uiState.value.endDate.ifBlank { "2026-09-05" },
                    status = "planning",
                    interests = _uiState.value.selectedInterests.toList(),
                    itinerary = aiPlan.itinerary,
                    topAttractions = aiPlan.topAttractions,
                    destinationAlerts = aiPlan.safetyAlerts,
                    localEtiquette = aiPlan.localEtiquette,
                    generalAdvisory = aiPlan.generalAdvisory,
                    createdAt = Timestamp.now()
                )

                val result = firestoreRepository.createTrip(newTrip)
                result.fold(
                    onSuccess = { tripId ->
                        val savedTripWithId = newTrip.copy(tripId = tripId)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isCreatingTrip = false,
                            loadingStep = "",
                            successMessage = "AI Itinerary generated for $dest!",
                            selectedTripForDetail = savedTripWithId
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            loadingStep = "",
                            errorMessage = "Could not save trip: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loadingStep = "",
                    errorMessage = "Error planning trip: ${e.message}"
                )
            }
        }
    }

    fun updateStatus(tripId: String, status: String) {
        viewModelScope.launch {
            firestoreRepository.updateTripStatus(tripId, status)
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            if (_uiState.value.selectedTripForDetail?.tripId == tripId) {
                _uiState.value = _uiState.value.copy(selectedTripForDetail = null)
            }
            firestoreRepository.deleteTrip(tripId)
        }
    }
}
