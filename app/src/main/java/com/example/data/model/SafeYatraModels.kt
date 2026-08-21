package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class UserProfile(
    @DocumentId val uid: String = "",
    val phone: String = "",
    val name: String = "",
    val homeCountry: String = "",
    val preferredLanguage: String = "English",
    val role: String = "traveler", // "traveler" or "provider"
    val providerType: String = "Tourist Police", // "Tourist Police", "Emergency Medical", "Safe Transport", "Certified Guide", "Embassy Help"
    val agencyName: String = "",
    val badgeNumber: String = "",
    val serviceArea: String = "Central Tourist District",
    val dutyStatus: String = "ON_DUTY", // "ON_DUTY", "OFF_DUTY"
    val trustScore: Double = 4.9,
    val ratingCount: Int = 142,
    val isVerifiedProvider: Boolean = false,
    val idProofType: String = "Police / Law Enforcement ID", // "Police / Law Enforcement ID", "Ministry of Tourism Guide License", "Commercial Transport Permit", "Medical Council License", "National Government ID (Aadhaar/Passport)"
    val idProofNumber: String = "",
    val issuingAuthority: String = "",
    val designationRank: String = "",
    val officialEmail: String = "",
    val idProofDocumentName: String = "official_credentials_scan.pdf",
    val isIdProofVerified: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class EmergencyContact(
    @DocumentId val id: String = "",
    val name: String = "",
    val phone: String = "",
    val relationship: String = ""
)

data class LiveLocation(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    @ServerTimestamp val updatedAt: Timestamp? = null,
    val accuracyMeters: Float = 0f
)

data class ItineraryActivity(
    val time: String = "",
    val place: String = "",
    val description: String = ""
)

data class ItineraryDay(
    val day: Int = 1,
    val title: String = "",
    val activities: List<ItineraryActivity> = emptyList()
)

data class Attraction(
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val safetyTip: String = ""
)

data class SafetyAlert(
    val type: String = "",
    val title: String = "",
    val description: String = "",
    val severity: String = "medium" // "high", "medium", "low"
)

data class LocalEtiquette(
    val rule: String = "",
    val reason: String = ""
)

data class Trip(
    @DocumentId val tripId: String = "",
    val userId: String = "",
    val destinationName: String = "",
    val destinationLat: Double = 0.0,
    val destinationLng: Double = 0.0,
    val startDate: String = "",
    val endDate: String = "",
    val status: String = "planning", // "planning", "active", "completed"
    val interests: List<String> = emptyList(),
    val itinerary: List<ItineraryDay> = emptyList(),
    val topAttractions: List<Attraction> = emptyList(),
    val destinationAlerts: List<SafetyAlert> = emptyList(),
    val localEtiquette: List<LocalEtiquette> = emptyList(),
    val generalAdvisory: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class SosEvent(
    @DocumentId val eventId: String = "",
    val userId: String = "",
    val status: String = "active", // "active", "resolved"
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val accuracyMeters: Float = 0f,
    val locationTimestamp: Timestamp? = null,
    val notifiedContacts: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    val resolvedAt: Timestamp? = null
)

data class VerifiedProvider(
    @DocumentId val id: String = "",
    val name: String = "",
    val type: String = "Tourist Police", // "Tourist Police", "Emergency Medical", "Safe Transport", "Embassy Help", "Certified Guide"
    val phone: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val serviceArea: String = "",
    val trustScore: Double = 4.9,
    val totalRatings: Int = 120,
    val verificationStatus: String = "Verified" // "Verified", "Under Review"
)

data class RiskReport(
    @DocumentId val id: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val riskType: String = "General Advisory", // "Scam Alert", "Road Hazard", "Severe Weather", "Unsafe Area", "General Advisory"
    val description: String = "",
    val reporterId: String = "",
    val reportedBy: String = "Traveler Community",
    val severity: String = "medium", // "high", "medium", "low"
    val destinationName: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class LiveWeatherData(
    val cityName: String = "Current Location",
    val temperatureC: Double = 28.0,
    val apparentTemperatureC: Double = 29.5,
    val condition: String = "Sunny & Clear",
    val conditionIconCode: String = "sunny",
    val humidityPercent: Int = 58,
    val windSpeedKmh: Double = 12.0,
    val uvIndex: Int = 6,
    val aqi: Int = 74,
    val aqiStatus: String = "Moderate",
    val rainProbabilityPercent: Int = 10,
    val safetyAdvisory: String = "Comfortable exploring weather. Stay hydrated and use sun protection."
)

data class NearbyAttractionItem(
    val id: String = "",
    val name: String = "",
    val category: String = "Heritage Site", // "Heritage Site", "Temple & Spiritual", "Monument", "Scenic Nature", "Bazaar & Food", "Museum"
    val description: String = "",
    val distanceKm: Double = 0.8,
    val rating: Double = 4.8,
    val totalReviews: Int = 1250,
    val entryFee: String = "₹50 (Indians) / ₹500 (Foreigners)",
    val openHours: String = "09:00 AM - 06:00 PM",
    val crowdLevel: String = "Moderate", // "Low", "Moderate", "High"
    val safetyTip: String = "Only use official ticketing counters at the entrance gate.",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

data class AreaDetails(
    val localityName: String = "Central District",
    val city: String = "New Delhi",
    val stateCountry: String = "Delhi, India",
    val safetyScore: Int = 92, // out of 100
    val safetyRatingLabel: String = "Safe Tourist Zone",
    val soloFemaleRating: Double = 4.7,
    val nightSafetyAdvisory: String = "Well lit tourist corridors with regular police patrolling until 11:00 PM.",
    val topScams: List<String> = emptyList(),
    val localEtiquettes: List<String> = emptyList(),
    val transitTips: List<String> = emptyList(),
    val nearestPoliceOutpost: String = "Tourist Assistance Booth (350m away)",
    val nearestHospital: String = "City Emergency Care (1.1km away)"
)

data class GuardianLiveBeacon(
    val eventId: String = "",
    val travelerName: String = "",
    val travelerPhone: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val accuracyMeters: Float = 10f,
    val speedKmh: Double = 0.0,
    val batteryPercent: Int = 85,
    val status: String = "active",
    val lastPingTime: String = "Just now",
    val mapLink: String = "",
    val notifiedGuardians: List<String> = emptyList()
)

data class NearbyPlaceItem(
    val id: String = "",
    val name: String = "",
    val category: String = "Police Station", // "Police Station", "Hospital", "Attraction", "Safe Cab", "Emergency Haven"
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val distanceKm: Double = 0.0,
    val phone: String = "",
    val rating: Double = 4.8,
    val openStatus: String = "Open 24/7",
    val isVerified: Boolean = true,
    val googleMapsUrl: String = "",
    val directionsUrl: String = ""
)

data class ProviderAssistanceRequest(
    val id: String = "",
    val travelerName: String = "",
    val travelerPhone: String = "",
    val serviceType: String = "Tourist Police Escort", // "Tourist Police Escort", "Emergency Medical", "Safe Cab Dispatch", "Certified Guide"
    val destinationLocality: String = "Connaught Place, Central Delhi",
    val details: String = "",
    val requestedAt: String = "5 mins ago",
    val status: String = "pending", // "pending", "dispatched", "completed"
    val lat: Double = 28.6328,
    val lng: Double = 77.2197,
    val distanceKm: Double = 1.2
)

data class ProviderDutyStats(
    val activeSosCount: Int = 1,
    val resolvedToday: Int = 8,
    val activeDispatches: Int = 3,
    val avgResponseTimeMin: Double = 3.5,
    val satisfactionScore: Double = 4.95
)


