package com.example.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AreaDetails
import com.example.data.model.Attraction
import com.example.data.model.EmergencyContact
import com.example.data.model.GuardianLiveBeacon
import com.example.data.model.ItineraryActivity
import com.example.data.model.LiveWeatherData
import com.example.data.model.NearbyAttractionItem
import com.example.data.model.NearbyPlaceItem
import com.example.data.model.RiskReport
import com.example.data.model.SosEvent
import com.example.data.model.Trip
import com.example.data.model.UserProfile
import com.example.data.model.VerifiedProvider
import com.example.data.repository.AuthRepository
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.LocationTrackingManager
import com.example.data.repository.LocationTrackingState
import com.example.data.service.AreaSafetyDetailsService
import com.example.data.service.GeocodeResult
import com.example.data.service.LivePlacesService
import com.example.data.service.LiveWeatherService
import com.example.data.service.NearbyAttractionsService
import com.example.data.service.NominatimGeocodingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class NearbyActivityStop(
    val dayNumber: Int,
    val activity: ItineraryActivity,
    val distanceKm: Double?
)

data class HomeUiState(
    val userProfile: UserProfile? = null,
    val allUserTrips: List<Trip> = emptyList(),
    val activeTrip: Trip? = null,
    val isOnTripMode: Boolean = false,
    val distanceToDestinationKm: Double? = null,
    val destinationRiskReports: List<RiskReport> = emptyList(),
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val nearbyRiskReports: List<RiskReport> = emptyList(),
    val nearbyProviders: List<VerifiedProvider> = emptyList(),
    val nearbyItineraryActivities: List<NearbyActivityStop> = emptyList(),
    val liveWeather: LiveWeatherData = LiveWeatherData(),
    val nearbyAttractions: List<NearbyAttractionItem> = emptyList(),
    val nearbyPlaces: List<NearbyPlaceItem> = emptyList(),
    val selectedNearbyPlace: NearbyPlaceItem? = null,
    val areaDetails: AreaDetails = AreaDetails(),
    val selectedAttraction: NearbyAttractionItem? = null,
    val activeSosEvent: SosEvent? = null,
    val isTriggeringSos: Boolean = false,
    val showSosObserverDialog: Boolean = false,
    val showGuardianRadarDialog: Boolean = false,
    val sosSuccessMessage: String? = null,
    val errorMessage: String? = null,
    val autoTripDetectedMessage: String? = null,
    // Exact location calibration & places filtering
    val showLocationPickerModal: Boolean = false,
    val locationSearchQuery: String = "",
    val locationSearchResults: List<GeocodeResult> = emptyList(),
    val isSearchingLocation: Boolean = false,
    val placesCategoryFilter: String = "All",
    val placesSearchQuery: String = "",
    val isRefreshingLocation: Boolean = false
)

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
    private val locationTracker: LocationTrackingManager,
    private val weatherService: LiveWeatherService = LiveWeatherService(),
    private val attractionsService: NearbyAttractionsService = NearbyAttractionsService(),
    private val livePlacesService: LivePlacesService = LivePlacesService(),
    private val areaDetailsService: AreaSafetyDetailsService = AreaSafetyDetailsService(),
    private val geocodingService: NominatimGeocodingService = NominatimGeocodingService()
) : ViewModel() {

    private val TAG = "HomeViewModel"
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val locationState: StateFlow<LocationTrackingState> = locationTracker.trackingState

    private var activeSosObserverJob: Job? = null
    private var lastIntelligenceLat = 0.0
    private var lastIntelligenceLng = 0.0
    private var searchJob: Job? = null

    init {
        setupUserListeners()
    }

    private fun setupUserListeners() {
        val userId = authRepository.currentUserId
        locationTracker.setUserId(userId)

        // Observe user profile
        viewModelScope.launch {
            firestoreRepository.observeUserProfile(userId).collect { profile ->
                _uiState.value = _uiState.value.copy(userProfile = profile)
            }
        }

        // Observe user trips with real-time listener (onSnapshot)
        viewModelScope.launch {
            firestoreRepository.observeUserTrips(userId).collect { trips ->
                _uiState.value = _uiState.value.copy(allUserTrips = trips)
                processTripsAndLocation(trips, locationTracker.trackingState.value)
            }
        }

        // Observe emergency contacts
        viewModelScope.launch {
            firestoreRepository.observeEmergencyContacts(userId).collect { contacts ->
                _uiState.value = _uiState.value.copy(emergencyContacts = contacts)
            }
        }

        // Observe user SOS events
        viewModelScope.launch {
            firestoreRepository.observeUserSosEvents(userId).collect { events ->
                val activeSos = events.firstOrNull { it.status == "active" }
                _uiState.value = _uiState.value.copy(activeSosEvent = activeSos)
                if (activeSos != null) {
                    locationTracker.setActiveSosEventId(activeSos.eventId)
                    observeSosDetails(activeSos.eventId)
                } else {
                    locationTracker.setActiveSosEventId(null)
                    activeSosObserverJob?.cancel()
                }
            }
        }

        // Observe Verified Providers (real-time snapshot)
        viewModelScope.launch {
            firestoreRepository.observeVerifiedProviders().collect { providers ->
                _uiState.value = _uiState.value.copy(nearbyProviders = providers)
                recomputeNearbyProviders()
            }
        }

        // Observe Risk Reports (real-time snapshot)
        viewModelScope.launch {
            firestoreRepository.observeRiskReports().collect { reports ->
                _uiState.value = _uiState.value.copy(nearbyRiskReports = reports)
                recomputeDestinationRisks()
            }
        }

        // Listen for continuous GPS live location changes
        viewModelScope.launch {
            locationTracker.trackingState.collect { locState ->
                processTripsAndLocation(_uiState.value.allUserTrips, locState)
                recomputeNearbyProviders()
                checkAndUpdateLocationIntelligence(locState.latitude, locState.longitude)
            }
        }
    }

    private fun checkAndUpdateLocationIntelligence(lat: Double, lng: Double) {
        val distChange = LocationTrackingManager.calculateDistanceKm(lastIntelligenceLat, lastIntelligenceLng, lat, lng)
        if (distChange > 0.3 || lastIntelligenceLat == 0.0) {
            lastIntelligenceLat = lat
            lastIntelligenceLng = lng
            refreshLocationIntelligence(lat, lng)
        }
    }

    fun refreshLocationIntelligence(lat: Double = locationTracker.trackingState.value.latitude, lng: Double = locationTracker.trackingState.value.longitude) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resolvedCity = geocodingService.reverseGeocode(lat, lng)
                val weather = weatherService.getLiveWeather(lat, lng, resolvedCity)
                val attractions = attractionsService.getNearbyAttractions(lat, lng, resolvedCity)
                val areaDetails = areaDetailsService.getAreaDetails(lat, lng, resolvedCity)
                val places = livePlacesService.getNearbyPlaces(lat, lng, resolvedCity)

                _uiState.value = _uiState.value.copy(
                    liveWeather = weather,
                    nearbyAttractions = attractions,
                    nearbyPlaces = places,
                    areaDetails = areaDetails,
                    isRefreshingLocation = false
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing location intelligence: ${e.message}")
                _uiState.value = _uiState.value.copy(isRefreshingLocation = false)
            }
        }
    }

    // =========================================================
    // EXACT LOCATION CALIBRATION & SELECTION METHODS
    // =========================================================

    fun openLocationPicker() {
        _uiState.value = _uiState.value.copy(
            showLocationPickerModal = true,
            locationSearchQuery = "",
            locationSearchResults = geocodingService.getPredefinedHubs().take(8)
        )
    }

    fun closeLocationPicker() {
        _uiState.value = _uiState.value.copy(showLocationPickerModal = false)
    }

    fun onLocationSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            locationSearchQuery = query,
            isSearchingLocation = query.isNotBlank()
        )
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                locationSearchResults = geocodingService.getPredefinedHubs().take(8),
                isSearchingLocation = false
            )
            return
        }
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            val results = geocodingService.searchLocations(query)
            _uiState.value = _uiState.value.copy(
                locationSearchResults = results,
                isSearchingLocation = false
            )
        }
    }

    fun selectLocationResult(result: GeocodeResult) {
        locationTracker.setExactLocation(
            lat = result.latitude,
            lng = result.longitude,
            city = result.city.ifBlank { result.displayName.substringBefore(",") },
            address = result.displayName
        )
        lastIntelligenceLat = result.latitude
        lastIntelligenceLng = result.longitude
        _uiState.value = _uiState.value.copy(
            showLocationPickerModal = false,
            sosSuccessMessage = "📍 Exact location set: ${result.displayName.take(35)}"
        )
        refreshLocationIntelligence(result.latitude, result.longitude)
    }

    fun autoDetectLiveLocation() {
        _uiState.value = _uiState.value.copy(isRefreshingLocation = true)
        locationTracker.autoDetectExactLocation { success, message ->
            _uiState.value = _uiState.value.copy(
                isRefreshingLocation = false,
                showLocationPickerModal = false,
                sosSuccessMessage = message
            )
            val current = locationTracker.trackingState.value
            refreshLocationIntelligence(current.latitude, current.longitude)
        }
    }

    // =========================================================
    // NEARBY PLACES FILTERING & SEARCH
    // =========================================================

    fun setPlacesCategoryFilter(category: String) {
        _uiState.value = _uiState.value.copy(placesCategoryFilter = category)
    }

    fun setPlacesSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(placesSearchQuery = query)
    }

    fun getFilteredNearbyPlaces(): List<NearbyPlaceItem> {
        val all = _uiState.value.nearbyPlaces
        val cat = _uiState.value.placesCategoryFilter
        val q = _uiState.value.placesSearchQuery.trim().lowercase()

        var filtered = when (cat) {
            "Police" -> all.filter { it.category == "Police Station" }
            "Hospitals" -> all.filter { it.category == "Hospital" }
            "Attractions" -> all.filter { it.category == "Attraction" }
            "Cabs" -> all.filter { it.category == "Safe Cab" }
            "Food" -> all.filter { it.category == "Food & Cafe" }
            else -> all
        }

        if (q.isNotBlank()) {
            filtered = filtered.filter {
                it.name.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.address.lowercase().contains(q)
            }
        }

        return filtered
    }

    private fun observeSosDetails(eventId: String) {
        activeSosObserverJob?.cancel()
        activeSosObserverJob = viewModelScope.launch {
            firestoreRepository.observeSosEvent(eventId).collect { event ->
                if (event != null) {
                    _uiState.value = _uiState.value.copy(activeSosEvent = event)
                }
            }
        }
    }

    private fun processTripsAndLocation(trips: List<Trip>, locState: LocationTrackingState) {
        if (trips.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                activeTrip = null,
                isOnTripMode = false,
                distanceToDestinationKm = null
            )
            return
        }

        val userLat = locState.latitude
        val userLng = locState.longitude

        var detectedTrip: Trip? = null
        var minDistanceKm: Double? = null
        var autoActivated = false

        for (trip in trips) {
            if (trip.destinationLat != 0.0 && trip.destinationLng != 0.0) {
                val dist = LocationTrackingManager.calculateDistanceKm(
                    userLat,
                    userLng,
                    trip.destinationLat,
                    trip.destinationLng
                )

                if (dist <= 5.0) {
                    detectedTrip = trip
                    minDistanceKm = dist

                    if (trip.status == "planning") {
                        viewModelScope.launch {
                            firestoreRepository.updateTripStatus(trip.tripId, "active")
                        }
                        autoActivated = true
                    }
                    break
                }
            }
        }

        if (detectedTrip == null) {
            val activeTrip = trips.firstOrNull { it.status == "active" }
            if (activeTrip != null) {
                detectedTrip = activeTrip
                if (activeTrip.destinationLat != 0.0 && activeTrip.destinationLng != 0.0) {
                    minDistanceKm = LocationTrackingManager.calculateDistanceKm(
                        userLat,
                        userLng,
                        activeTrip.destinationLat,
                        activeTrip.destinationLng
                    )
                }
            } else {
                val planningTrip = trips.firstOrNull { it.status == "planning" }
                if (planningTrip != null) {
                    detectedTrip = planningTrip
                    if (planningTrip.destinationLat != 0.0 && planningTrip.destinationLng != 0.0) {
                        minDistanceKm = LocationTrackingManager.calculateDistanceKm(
                            userLat,
                            userLng,
                            planningTrip.destinationLat,
                            planningTrip.destinationLng
                        )
                    }
                }
            }
        }

        val isOnTrip = (detectedTrip?.status == "active") || (minDistanceKm != null && minDistanceKm <= 5.0)

        _uiState.value = _uiState.value.copy(
            activeTrip = detectedTrip,
            isOnTripMode = isOnTrip,
            distanceToDestinationKm = minDistanceKm,
            autoTripDetectedMessage = if (autoActivated && detectedTrip != null) {
                "✨ Welcome to ${detectedTrip.destinationName}! Automatic On-Trip Protection Activated."
            } else _uiState.value.autoTripDetectedMessage
        )

        recomputeDestinationRisks()
        recomputeNearbyActivities()
    }

    private fun recomputeNearbyProviders() {
        val loc = locationTracker.trackingState.value
        val all = _uiState.value.nearbyProviders
        if (all.isEmpty()) return

        val sorted = all.sortedBy { p ->
            LocationTrackingManager.calculateDistanceKm(loc.latitude, loc.longitude, p.lat, p.lng)
        }
        _uiState.value = _uiState.value.copy(nearbyProviders = sorted)
    }

    private fun recomputeNearbyActivities() {
        val trip = _uiState.value.activeTrip ?: return
        val loc = locationTracker.trackingState.value

        val stops = mutableListOf<NearbyActivityStop>()
        trip.itinerary.forEach { dayPlan ->
            dayPlan.activities.forEach { act ->
                val dist = if (trip.destinationLat != 0.0 && trip.destinationLng != 0.0) {
                    LocationTrackingManager.calculateDistanceKm(
                        loc.latitude,
                        loc.longitude,
                        trip.destinationLat,
                        trip.destinationLng
                    )
                } else null
                stops.add(NearbyActivityStop(dayPlan.day, act, dist))
            }
        }
        _uiState.value = _uiState.value.copy(nearbyItineraryActivities = stops)
    }

    private fun recomputeDestinationRisks() {
        val trip = _uiState.value.activeTrip
        val allReports = _uiState.value.nearbyRiskReports

        if (trip != null && trip.destinationLat != 0.0 && trip.destinationLng != 0.0) {
            val destinationReports = allReports.filter { report ->
                val dist = LocationTrackingManager.calculateDistanceKm(
                    trip.destinationLat,
                    trip.destinationLng,
                    report.lat,
                    report.lng
                )
                dist <= 100.0
            }.sortedBy { report ->
                LocationTrackingManager.calculateDistanceKm(
                    trip.destinationLat,
                    trip.destinationLng,
                    report.lat,
                    report.lng
                )
            }

            _uiState.value = _uiState.value.copy(
                destinationRiskReports = if (destinationReports.isNotEmpty()) destinationReports else allReports.take(2)
            )
        } else {
            _uiState.value = _uiState.value.copy(destinationRiskReports = allReports.take(2))
        }
    }

    fun triggerEmergencySos() {
        val userId = authRepository.currentUserId

        _uiState.value = _uiState.value.copy(
            isTriggeringSos = true,
            errorMessage = null,
            sosSuccessMessage = "Acquiring fresh high-accuracy GPS coordinates for SOS..."
        )

        viewModelScope.launch {
            val freshLoc = locationTracker.getCurrentHighAccuracyLocation(timeoutMs = 8000L)
            val lat = freshLoc?.latitude ?: locationTracker.trackingState.value.latitude
            val lng = freshLoc?.longitude ?: locationTracker.trackingState.value.longitude
            val accuracy = freshLoc?.accuracy ?: locationTracker.trackingState.value.accuracyMeters

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
                    observeSosDetails(eventId)
                    _uiState.value = _uiState.value.copy(
                        isTriggeringSos = false,
                        showSosObserverDialog = true,
                        sosSuccessMessage = "EMERGENCY SOS BROADCAST ACTIVE. High-accuracy GPS (±${accuracy.toInt()}m) updating in real-time."
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isTriggeringSos = false,
                        errorMessage = "SOS dispatch failed: ${error.message}"
                    )
                }
            )
        }
    }

    fun resolveActiveSos(eventId: String) {
        locationTracker.setActiveSosEventId(null)
        activeSosObserverJob?.cancel()
        activeSosObserverJob = null
        _uiState.value = _uiState.value.copy(
            activeSosEvent = null,
            showSosObserverDialog = false,
            showGuardianRadarDialog = false,
            sosSuccessMessage = "Emergency SOS resolved. Telemetry broadcast stopped."
        )

        viewModelScope.launch {
            firestoreRepository.resolveSosEvent(eventId)
        }
    }

    fun openSosObserverDialog() {
        _uiState.value = _uiState.value.copy(showSosObserverDialog = true, showGuardianRadarDialog = true)
    }

    fun closeSosObserverDialog() {
        _uiState.value = _uiState.value.copy(showSosObserverDialog = false, showGuardianRadarDialog = false)
    }

    fun openGuardianRadarDialog() {
        _uiState.value = _uiState.value.copy(showGuardianRadarDialog = true)
    }

    fun closeGuardianRadarDialog() {
        _uiState.value = _uiState.value.copy(showGuardianRadarDialog = false)
    }

    fun sendDirectGuardianSms(context: Context) {
        val sos = _uiState.value.activeSosEvent ?: return
        val profile = _uiState.value.userProfile
        val travelerName = profile?.name?.ifBlank { "Traveler" } ?: "SafeYatra Traveler"
        val contacts = _uiState.value.emergencyContacts
        val liveTrackingUrl = "https://safeyatra.app/sos/${sos.eventId}?lat=${sos.lat}&lng=${sos.lng}"
        val googleMapsUrl = "https://maps.google.com/?q=${sos.lat},${sos.lng}"
        val smsMessage = "🚨 EMERGENCY SOS from $travelerName! I need immediate help at coordinates (${String.format("%.5f", sos.lat)}, ${String.format("%.5f", sos.lng)}). Live GPS: $googleMapsUrl (Guardian Radar: $liveTrackingUrl)"

        try {
            val allPhones = contacts.map { it.phone.trim() }.filter { it.isNotBlank() }
            val phoneJoined = if (allPhones.isNotEmpty()) allPhones.joinToString(";") else ""
            val uri = if (phoneJoined.isNotBlank()) Uri.parse("smsto:$phoneJoined") else Uri.parse("smsto:")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", smsMessage)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, smsMessage)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Send Emergency SOS SMS"))
        }
    }

    fun shareSosLink(context: Context, eventId: String, lat: Double, lng: Double) {
        try {
            val shareUrl = "https://safeyatra.app/sos/$eventId?lat=$lat&lng=$lng"
            val message = "🚨 EMERGENCY SOS ALERT: I need immediate assistance! Track my live real-time location here: $shareUrl (SafeYatra Live Guardian Protection)"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "EMERGENCY SOS LIVE TRACKING")
                putExtra(Intent.EXTRA_TEXT, message)
            }
            context.startActivity(Intent.createChooser(intent, "Share Live SOS Tracking Link"))
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing SOS link: ${e.message}")
        }
    }

    fun callProvider(context: Context, phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Could not dial phone: ${e.message}")
        }
    }

    fun selectNearbyPlace(place: NearbyPlaceItem?) {
        _uiState.value = _uiState.value.copy(selectedNearbyPlace = place)
    }

    fun openGoogleMapsSearch(context: Context, category: String) {
        val loc = locationTracker.trackingState.value
        LivePlacesService.openGoogleMapsSearch(context, category, loc.latitude, loc.longitude)
    }

    fun openPlaceInGoogleMaps(context: Context, place: NearbyPlaceItem) {
        LivePlacesService.openGoogleMapsPlace(context, place.name, place.lat, place.lng)
    }

    fun getPlaceDirections(context: Context, place: NearbyPlaceItem) {
        LivePlacesService.openGoogleMapsDirections(context, place.lat, place.lng, place.name)
    }

    fun clearNotifications() {
        _uiState.value = _uiState.value.copy(
            sosSuccessMessage = null,
            errorMessage = null,
            autoTripDetectedMessage = null
        )
    }
}
