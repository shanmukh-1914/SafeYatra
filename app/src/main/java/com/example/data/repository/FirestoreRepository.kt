package com.example.data.repository

import android.util.Log
import com.example.data.model.EmergencyContact
import com.example.data.model.LiveLocation
import com.example.data.model.RiskReport
import com.example.data.model.SosEvent
import com.example.data.model.Trip
import com.example.data.model.UserProfile
import com.example.data.model.VerifiedProvider
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID

class FirestoreRepository(
    private val localDb: UserLocalDatabaseRepository = UserLocalDatabaseRepository()
) {
    private val TAG = "FirestoreRepository"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            Log.w(TAG, "Firestore instance not available, using in-memory local state: ${e.message}")
            null
        }
    }

    companion object {
        // Local in-memory caches for reliable offline operation and instant fallback
        private val localProfiles = mutableMapOf<String, UserProfile>()
        private val localProfileFlows = mutableMapOf<String, MutableStateFlow<UserProfile?>>()

        private val localEmergencyContacts = mutableMapOf<String, MutableList<EmergencyContact>>()
        private val localContactsFlows = mutableMapOf<String, MutableStateFlow<List<EmergencyContact>>>()

        private val localTrips = mutableMapOf<String, MutableList<Trip>>()
        private val localTripsFlows = mutableMapOf<String, MutableStateFlow<List<Trip>>>()

        private val localSosEvents = mutableMapOf<String, SosEvent>()
        private val localSosFlows = mutableMapOf<String, MutableStateFlow<SosEvent?>>()
        private val localUserSosListFlows = mutableMapOf<String, MutableStateFlow<List<SosEvent>>>()

        private val localProviders = mutableListOf<VerifiedProvider>()
        private val localProvidersFlow = MutableStateFlow<List<VerifiedProvider>>(emptyList())

        private val localRiskReports = mutableListOf<RiskReport>()
        private val localRiskReportsFlow = MutableStateFlow<List<RiskReport>>(emptyList())

        private val localAssistanceRequests = mutableListOf<com.example.data.model.ProviderAssistanceRequest>()
        private val localAssistanceRequestsFlow = MutableStateFlow<List<com.example.data.model.ProviderAssistanceRequest>>(emptyList())

        private val localAllActiveSosFlow = MutableStateFlow<List<SosEvent>>(emptyList())

        private var isSeeded = false
    }

    init {
        ensureInitialSeeds()
    }

    private fun ensureInitialSeeds() {
        if (isSeeded) return
        isSeeded = true

        val initialProviders = listOf(
            VerifiedProvider(
                id = "p1",
                name = "Tourist Police Central Command",
                type = "Tourist Police",
                phone = "+91 11 2346 9526",
                lat = 28.6139,
                lng = 77.2090,
                serviceArea = "Citywide & Heritage Zones",
                trustScore = 4.95,
                totalRatings = 342,
                verificationStatus = "Verified"
            ),
            VerifiedProvider(
                id = "p2",
                name = "Apollo Emergency Medical Rescue",
                type = "Emergency Medical",
                phone = "+91 1066",
                lat = 28.5672,
                lng = 77.2100,
                serviceArea = "24/7 Rapid Ambulance & First Aid",
                trustScore = 4.9,
                totalRatings = 215,
                verificationStatus = "Verified"
            ),
            VerifiedProvider(
                id = "p3",
                name = "SafeWheels Verified Tourist Cabs",
                type = "Safe Transport",
                phone = "+91 98110 54321",
                lat = 28.5562,
                lng = 77.1000,
                serviceArea = "Airport & Intercity Travel",
                trustScore = 4.85,
                totalRatings = 870,
                verificationStatus = "Verified"
            ),
            VerifiedProvider(
                id = "p4",
                name = "Global Consular Emergency Support",
                type = "Embassy Help",
                phone = "+91 11 2419 8000",
                lat = 28.5912,
                lng = 77.1873,
                serviceArea = "Diplomatic Enclave & Consulates",
                trustScore = 5.0,
                totalRatings = 158,
                verificationStatus = "Verified"
            ),
            VerifiedProvider(
                id = "p5",
                name = "Heritage Certified Guides Guild",
                type = "Certified Guide",
                phone = "+91 99100 88221",
                lat = 28.6505,
                lng = 77.2300,
                serviceArea = "Old Delhi & Monuments",
                trustScore = 4.8,
                totalRatings = 194,
                verificationStatus = "Verified"
            )
        )
        localProviders.clear()
        localProviders.addAll(initialProviders)
        localProvidersFlow.value = localProviders.toList()

        val initialRisks = listOf(
            RiskReport(
                id = "r1",
                reportedBy = "SafeYatra Verified Community",
                riskType = "Scam Alert",
                description = "Unauthorized touts around Paharganj offering fake train booking counters.",
                severity = "medium",
                lat = 28.6430,
                lng = 77.2150,
                destinationName = "New Delhi Railway Station Area",
                createdAt = Timestamp.now()
            ),
            RiskReport(
                id = "r2",
                reportedBy = "Local Tour Dispatch",
                riskType = "Overpriced Transport",
                description = "Ensure auto-rickshaws run on meter or use SafeWheels prepaid taxi booths at airport.",
                severity = "low",
                lat = 28.5562,
                lng = 77.1000,
                destinationName = "IGI Airport Terminal 3",
                createdAt = Timestamp.now()
            ),
            RiskReport(
                id = "r3",
                reportedBy = "Tourist Safety Wing",
                riskType = "Crowd Congestion",
                description = "Heavy footfall near Chandni Chowk evening markets; keep valuables in front zippered pockets.",
                severity = "low",
                lat = 28.6505,
                lng = 77.2300,
                destinationName = "Old Delhi Heritage Belt",
                createdAt = Timestamp.now()
            )
        )
        localRiskReports.clear()
        localRiskReports.addAll(initialRisks)
        localRiskReportsFlow.value = localRiskReports.toList()
    }

    private val backgroundScope = CoroutineScope(Dispatchers.IO + Job())

    // -------------------------------------------------------------
    // USERS COLLECTION: users/{uid}
    // -------------------------------------------------------------
    suspend fun getUserProfile(uid: String): UserProfile? {
        val persistentProfile = localDb.getUserProfile(uid)
        if (persistentProfile != null && persistentProfile.name.isNotBlank()) {
            localProfiles[uid] = persistentProfile
            localProfileFlows[uid]?.value = persistentProfile
            return persistentProfile
        }
        val cached = localProfiles[uid]
        if (cached != null && cached.name.isNotBlank() && cached.homeCountry.isNotBlank()) {
            return cached
        }
        val fs = firestore
        if (fs != null) {
            try {
                val doc = withTimeoutOrNull(1500L) {
                    fs.collection("users").document(uid).get().await()
                }
                if (doc != null && doc.exists()) {
                    val p = doc.toObject(UserProfile::class.java)?.copy(uid = doc.id)
                    if (p != null) {
                        localProfiles[uid] = p
                        localDb.saveUserProfile(p)
                        localProfileFlows[uid]?.value = p
                        return p
                    }
                }
            } catch (e: Throwable) {
                Log.w(TAG, "Error fetching user profile from Firestore: ${e.message}")
            }
        }
        return localProfiles[uid] ?: localDb.getUserProfile(uid)
    }

    fun observeUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        // Emit locally cached persistent profile immediately
        val initial = localProfiles[uid] ?: localDb.getUserProfile(uid)
        trySend(initial)

        val fs = firestore
        var listener: ListenerRegistration? = null
        if (fs != null) {
            try {
                listener = fs.collection("users").document(uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.w(TAG, "observeUserProfile Firestore error: ${error.message}")
                            trySend(localProfiles[uid] ?: localDb.getUserProfile(uid))
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            val p = snapshot.toObject(UserProfile::class.java)?.copy(uid = snapshot.id)
                            if (p != null) {
                                localProfiles[uid] = p
                                localDb.saveUserProfile(p)
                                trySend(p)
                            }
                        } else {
                            trySend(localProfiles[uid] ?: localDb.getUserProfile(uid))
                        }
                    }
            } catch (e: Throwable) {
                Log.w(TAG, "Failed to attach snapshot listener: ${e.message}")
                trySend(localProfiles[uid] ?: localDb.getUserProfile(uid))
            }
        }

        awaitClose { listener?.remove() }
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        // Instant optimistic update in local database
        localProfiles[profile.uid] = profile
        localDb.saveUserProfile(profile)
        if (!localProfileFlows.containsKey(profile.uid)) {
            localProfileFlows[profile.uid] = MutableStateFlow(profile)
        } else {
            localProfileFlows[profile.uid]?.value = profile
        }

        // Asynchronous non-blocking cloud synchronization
        backgroundScope.launch {
            val fs = firestore
            if (fs != null) {
                try {
                    val userMap = hashMapOf(
                        "phone" to profile.phone,
                        "name" to profile.name,
                        "homeCountry" to profile.homeCountry,
                        "preferredLanguage" to profile.preferredLanguage,
                        "role" to profile.role,
                        "providerType" to profile.providerType,
                        "agencyName" to profile.agencyName,
                        "badgeNumber" to profile.badgeNumber,
                        "serviceArea" to profile.serviceArea,
                        "dutyStatus" to profile.dutyStatus,
                        "trustScore" to profile.trustScore,
                        "ratingCount" to profile.ratingCount,
                        "isVerifiedProvider" to profile.isVerifiedProvider,
                        "createdAt" to (profile.createdAt ?: Timestamp.now())
                    )
                    fs.collection("users").document(profile.uid)
                        .set(userMap, SetOptions.merge())
                        .await()
                } catch (e: Throwable) {
                    Log.w(TAG, "Firestore saveUserProfile background sync notice: ${e.message}")
                }
            }
        }
        return Result.success(Unit)
    }

    // -------------------------------------------------------------
    // LIVE LOCATION
    // -------------------------------------------------------------
    suspend fun updateLiveLocation(uid: String, lat: Double, lng: Double, accuracy: Float = 0f): Result<Unit> {
        val fs = firestore
        if (fs != null) {
            try {
                val data = hashMapOf(
                    "lat" to lat,
                    "lng" to lng,
                    "updatedAt" to Timestamp.now(),
                    "accuracyMeters" to accuracy
                )
                fs.collection("users").document(uid)
                    .collection("liveLocation").document("current")
                    .set(data, SetOptions.merge())
                    .await()
            } catch (e: Throwable) {
                Log.w(TAG, "Firestore updateLiveLocation failed: ${e.message}")
            }
        }
        return Result.success(Unit)
    }

    // -------------------------------------------------------------
    // EMERGENCY CONTACTS
    // -------------------------------------------------------------
    fun observeEmergencyContacts(uid: String): Flow<List<EmergencyContact>> = callbackFlow {
        // Immediately emit local persistent cache
        val localList = localEmergencyContacts[uid] ?: localDb.getEmergencyContacts(uid)
        localEmergencyContacts[uid] = localList.toMutableList()
        trySend(localList)

        val fs = firestore
        var listener: ListenerRegistration? = null
        if (fs != null) {
            try {
                listener = fs.collection("users").document(uid)
                    .collection("emergencyContacts")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.w(TAG, "observeEmergencyContacts error: ${error.message}")
                            trySend(localEmergencyContacts[uid] ?: localDb.getEmergencyContacts(uid))
                            return@addSnapshotListener
                        }
                        val list = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(EmergencyContact::class.java)?.copy(id = doc.id)
                        } ?: (localEmergencyContacts[uid] ?: localDb.getEmergencyContacts(uid))
                        localEmergencyContacts[uid] = list.toMutableList()
                        localDb.saveEmergencyContacts(uid, list)
                        trySend(list)
                    }
            } catch (e: Throwable) {
                trySend(localEmergencyContacts[uid] ?: localDb.getEmergencyContacts(uid))
            }
        }
        awaitClose { listener?.remove() }
    }

    suspend fun addEmergencyContact(uid: String, contact: EmergencyContact): Result<String> {
        val contactId = contact.id.ifBlank { UUID.randomUUID().toString().take(8) }
        val savedContact = contact.copy(id = contactId)

        val currentList = localEmergencyContacts.getOrPut(uid) { localDb.getEmergencyContacts(uid).toMutableList() }
        currentList.removeAll { it.id == contactId }
        currentList.add(savedContact)
        localDb.saveEmergencyContacts(uid, currentList)

        backgroundScope.launch {
            val fs = firestore
            if (fs != null) {
                try {
                    val data = hashMapOf(
                        "name" to contact.name,
                        "phone" to contact.phone,
                        "relationship" to contact.relationship
                    )
                    fs.collection("users").document(uid)
                        .collection("emergencyContacts")
                        .add(data)
                        .await()
                } catch (e: Throwable) {
                    Log.w(TAG, "addEmergencyContact background Firestore notice: ${e.message}")
                }
            }
        }
        return Result.success(contactId)
    }

    suspend fun deleteEmergencyContact(uid: String, contactId: String): Result<Unit> {
        localEmergencyContacts[uid]?.removeAll { it.id == contactId }
        localDb.deleteEmergencyContact(uid, contactId)
        backgroundScope.launch {
            val fs = firestore
            if (fs != null) {
                try {
                    fs.collection("users").document(uid)
                        .collection("emergencyContacts").document(contactId)
                        .delete()
                        .await()
                } catch (e: Throwable) {
                    Log.w(TAG, "deleteEmergencyContact background Firestore notice: ${e.message}")
                }
            }
        }
        return Result.success(Unit)
    }

    // -------------------------------------------------------------
    // TRIPS
    // -------------------------------------------------------------
    fun observeUserTrips(userId: String): Flow<List<Trip>> = callbackFlow {
        // Immediately emit local persistent cache
        val localList = localTrips[userId] ?: localDb.getTrips(userId)
        localTrips[userId] = localList.toMutableList()
        trySend(localList)

        val fs = firestore
        var listener: ListenerRegistration? = null
        if (fs != null) {
            try {
                listener = fs.collection("trips")
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.w(TAG, "observeUserTrips error: ${error.message}")
                            trySend(localTrips[userId] ?: localDb.getTrips(userId))
                            return@addSnapshotListener
                        }
                        val list = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(Trip::class.java)?.copy(tripId = doc.id)
                        }?.sortedByDescending { it.createdAt } ?: (localTrips[userId] ?: localDb.getTrips(userId))
                        localTrips[userId] = list.toMutableList()
                        localDb.saveTrips(userId, list)
                        trySend(list)
                    }
            } catch (e: Throwable) {
                trySend(localTrips[userId] ?: localDb.getTrips(userId))
            }
        }
        awaitClose { listener?.remove() }
    }

    suspend fun createTrip(trip: Trip): Result<String> {
        val tripId = trip.tripId.ifBlank { "trip_${UUID.randomUUID().toString().take(8)}" }
        val savedTrip = trip.copy(tripId = tripId)

        val currentList = localTrips.getOrPut(trip.userId) { localDb.getTrips(trip.userId).toMutableList() }
        currentList.removeAll { it.tripId == tripId }
        currentList.add(0, savedTrip)
        localDb.saveTrip(trip.userId, savedTrip)

        backgroundScope.launch {
            val fs = firestore
            if (fs != null) {
                try {
                    val itineraryData = trip.itinerary.map { day ->
                        hashMapOf(
                            "day" to day.day,
                            "title" to day.title,
                            "activities" to day.activities.map { act ->
                                hashMapOf(
                                    "time" to act.time,
                                    "place" to act.place,
                                    "description" to act.description
                                )
                            }
                        )
                    }
                    val attractionsData = trip.topAttractions.map { att ->
                        hashMapOf(
                            "name" to att.name,
                            "category" to att.category,
                            "description" to att.description,
                            "safetyTip" to att.safetyTip
                        )
                    }
                    val alertsData = trip.destinationAlerts.map { alert ->
                        hashMapOf(
                            "type" to alert.type,
                            "title" to alert.title,
                            "description" to alert.description,
                            "severity" to alert.severity
                        )
                    }
                    val etiquetteData = trip.localEtiquette.map { eti ->
                        hashMapOf(
                            "rule" to eti.rule,
                            "reason" to eti.reason
                        )
                    }
                    val data = hashMapOf(
                        "userId" to trip.userId,
                        "destinationName" to trip.destinationName,
                        "destinationLat" to trip.destinationLat,
                        "destinationLng" to trip.destinationLng,
                        "startDate" to trip.startDate,
                        "endDate" to trip.endDate,
                        "status" to trip.status,
                        "interests" to trip.interests,
                        "itinerary" to itineraryData,
                        "topAttractions" to attractionsData,
                        "destinationAlerts" to alertsData,
                        "localEtiquette" to etiquetteData,
                        "generalAdvisory" to trip.generalAdvisory,
                        "createdAt" to Timestamp.now()
                    )
                    fs.collection("trips").document(tripId).set(data, SetOptions.merge()).await()
                } catch (e: Throwable) {
                    Log.w(TAG, "createTrip background Firestore notice: ${e.message}")
                }
            }
        }
        return Result.success(tripId)
    }

    suspend fun updateTripStatus(tripId: String, status: String): Result<Unit> {
        localTrips.values.forEach { list ->
            val index = list.indexOfFirst { it.tripId == tripId }
            if (index != -1) {
                val updated = list[index].copy(status = status)
                list[index] = updated
                localDb.saveTrip(updated.userId, updated)
            }
        }
        val fs = firestore
        if (fs != null) {
            try {
                fs.collection("trips").document(tripId)
                    .update("status", status)
                    .await()
            } catch (e: Throwable) {
                Log.w(TAG, "updateTripStatus Firestore failed: ${e.message}")
            }
        }
        return Result.success(Unit)
    }

    suspend fun deleteTrip(tripId: String): Result<Unit> {
        localTrips.forEach { (userId, list) ->
            val removed = list.removeAll { it.tripId == tripId }
            if (removed) {
                localDb.deleteTrip(userId, tripId)
            }
        }
        val fs = firestore
        if (fs != null) {
            try {
                fs.collection("trips").document(tripId)
                    .delete()
                    .await()
            } catch (e: Throwable) {
                Log.w(TAG, "deleteTrip Firestore failed: ${e.message}")
            }
        }
        return Result.success(Unit)
    }

    // -------------------------------------------------------------
    // SOS EVENTS
    // -------------------------------------------------------------
    fun observeUserSosEvents(userId: String): Flow<List<SosEvent>> = callbackFlow {
        // Emit persistent local SOS history first
        val persistentSos = localDb.getSosEvents(userId)
        persistentSos.forEach { localSosEvents[it.eventId] = it }
        trySend(persistentSos)

        val fs = firestore
        var listener: ListenerRegistration? = null
        if (fs != null) {
            try {
                listener = fs.collection("sosEvents")
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.w(TAG, "observeUserSosEvents error: ${error.message}")
                            val events = localSosEvents.values.filter { it.userId == userId }.ifEmpty { localDb.getSosEvents(userId) }
                            trySend(events)
                            return@addSnapshotListener
                        }
                        val list = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(SosEvent::class.java)?.copy(eventId = doc.id)
                        }?.sortedByDescending { it.createdAt } ?: localSosEvents.values.filter { it.userId == userId }.ifEmpty { localDb.getSosEvents(userId) }
                        localDb.saveSosEvents(userId, list)
                        list.forEach { localSosEvents[it.eventId] = it }
                        trySend(list)
                    }
            } catch (e: Throwable) {
                val events = localSosEvents.values.filter { it.userId == userId }.ifEmpty { localDb.getSosEvents(userId) }
                trySend(events)
            }
        } else {
            val events = localSosEvents.values.filter { it.userId == userId }.ifEmpty { localDb.getSosEvents(userId) }
            trySend(events)
        }
        awaitClose { listener?.remove() }
    }

    suspend fun triggerSosEvent(
        userId: String,
        lat: Double,
        lng: Double,
        contacts: List<String>,
        accuracyMeters: Float = 0f
    ): Result<String> {
        val eventId = "sos_${UUID.randomUUID().toString().take(8)}"
        val now = Timestamp.now()
        val event = SosEvent(
            eventId = eventId,
            userId = userId,
            status = "active",
            lat = lat,
            lng = lng,
            accuracyMeters = accuracyMeters,
            locationTimestamp = now,
            notifiedContacts = contacts,
            createdAt = now
        )
        localSosEvents[eventId] = event
        localDb.saveSosEvent(userId, event)

        val fs = firestore
        if (fs != null) {
            try {
                val data = hashMapOf(
                    "userId" to userId,
                    "status" to "active",
                    "lat" to lat,
                    "lng" to lng,
                    "accuracyMeters" to accuracyMeters,
                    "locationTimestamp" to now,
                    "notifiedContacts" to contacts,
                    "createdAt" to now,
                    "resolvedAt" to null
                )
                val ref = fs.collection("sosEvents").add(data).await()
                localSosEvents[ref.id] = event.copy(eventId = ref.id)
                return Result.success(ref.id)
            } catch (e: Throwable) {
                Log.w(TAG, "triggerSosEvent Firestore failed, saved locally: ${e.message}")
            }
        }
        return Result.success(eventId)
    }

    suspend fun updateSosEventLocation(eventId: String, lat: Double, lng: Double, accuracyMeters: Float = 0f): Result<Unit> {
        val existing = localSosEvents[eventId]
        if (existing != null) {
            localSosEvents[eventId] = existing.copy(
                lat = lat,
                lng = lng,
                accuracyMeters = accuracyMeters,
                locationTimestamp = Timestamp.now()
            )
        }
        val fs = firestore
        if (fs != null) {
            try {
                val updates = hashMapOf<String, Any?>(
                    "lat" to lat,
                    "lng" to lng,
                    "accuracyMeters" to accuracyMeters,
                    "locationTimestamp" to Timestamp.now()
                )
                fs.collection("sosEvents").document(eventId)
                    .update(updates)
                    .await()
            } catch (e: Throwable) {
                Log.w(TAG, "updateSosEventLocation Firestore failed: ${e.message}")
            }
        }
        return Result.success(Unit)
    }

    fun observeSosEvent(eventId: String): Flow<SosEvent?> = callbackFlow {
        val fs = firestore
        var listener: ListenerRegistration? = null
        if (fs != null) {
            try {
                listener = fs.collection("sosEvents").document(eventId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(localSosEvents[eventId])
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            val event = snapshot.toObject(SosEvent::class.java)?.copy(eventId = snapshot.id)
                            trySend(event)
                        } else {
                            trySend(localSosEvents[eventId])
                        }
                    }
            } catch (e: Throwable) {
                trySend(localSosEvents[eventId])
            }
        } else {
            trySend(localSosEvents[eventId])
        }
        awaitClose { listener?.remove() }
    }

    suspend fun resolveSosEvent(eventId: String): Result<Unit> {
        val existing = localSosEvents[eventId]
        if (existing != null) {
            val updated = existing.copy(
                status = "resolved",
                resolvedAt = Timestamp.now()
            )
            localSosEvents[eventId] = updated
            localDb.saveSosEvent(updated.userId, updated)
        }
        // Asynchronously update remote Firestore in background without blocking the UI
        backgroundScope.launch {
            val fs = firestore
            if (fs != null) {
                try {
                    val updates = hashMapOf<String, Any?>(
                        "status" to "resolved",
                        "resolvedAt" to Timestamp.now()
                    )
                    fs.collection("sosEvents").document(eventId)
                        .update(updates)
                        .await()
                } catch (e: Throwable) {
                    Log.w(TAG, "resolveSosEvent Firestore failed: ${e.message}")
                }
            }
        }
        return Result.success(Unit)
    }

    // -------------------------------------------------------------
    // VERIFIED PROVIDERS
    // -------------------------------------------------------------
    fun observeVerifiedProviders(): Flow<List<VerifiedProvider>> = callbackFlow {
        // Immediately emit local seeded providers
        trySend(localProviders.toList())

        val fs = firestore
        var listener: ListenerRegistration? = null
        if (fs != null) {
            try {
                listener = fs.collection("verifiedProviders")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(localProviders.toList())
                            return@addSnapshotListener
                        }
                        val list = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(VerifiedProvider::class.java)?.copy(id = doc.id)
                        }
                        if (!list.isNullOrEmpty()) {
                            trySend(list)
                        } else {
                            trySend(localProviders.toList())
                        }
                    }
            } catch (e: Throwable) {
                trySend(localProviders.toList())
            }
        }
        awaitClose { listener?.remove() }
    }

    suspend fun seedInitialProvidersIfEmpty() {
        backgroundScope.launch {
            val fs = firestore ?: return@launch
            try {
                val existing = withTimeoutOrNull(2000L) {
                    fs.collection("verifiedProviders").limit(1).get().await()
                }
                if (existing != null && existing.isEmpty) {
                    val batch = fs.batch()
                    for (p in localProviders) {
                        val docRef = fs.collection("verifiedProviders").document(p.id)
                        val map = hashMapOf(
                            "name" to p.name,
                            "type" to p.type,
                            "phone" to p.phone,
                            "lat" to p.lat,
                            "lng" to p.lng,
                            "serviceArea" to p.serviceArea,
                            "trustScore" to p.trustScore,
                            "totalRatings" to p.totalRatings,
                            "verificationStatus" to p.verificationStatus
                        )
                        batch.set(docRef, map)
                    }
                    batch.commit().await()
                }
            } catch (e: Throwable) {
                Log.w(TAG, "seedInitialProvidersIfEmpty background notice: ${e.message}")
            }
        }
    }

    // -------------------------------------------------------------
    // RISK REPORTS
    // -------------------------------------------------------------
    fun observeRiskReports(): Flow<List<RiskReport>> = callbackFlow {
        // Immediately emit local seeded risk reports
        trySend(localRiskReports.toList())

        val fs = firestore
        var listener: ListenerRegistration? = null
        if (fs != null) {
            try {
                listener = fs.collection("riskReports")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(localRiskReports.toList())
                            return@addSnapshotListener
                        }
                        val list = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(RiskReport::class.java)?.copy(id = doc.id)
                        }
                        if (!list.isNullOrEmpty()) {
                            trySend(list)
                        } else {
                            trySend(localRiskReports.toList())
                        }
                    }
            } catch (e: Throwable) {
                trySend(localRiskReports.toList())
            }
        }
        awaitClose { listener?.remove() }
    }

    suspend fun addRiskReport(report: RiskReport): Result<String> {
        val reportId = report.id.ifBlank { "risk_${UUID.randomUUID().toString().take(8)}" }
        val savedReport = report.copy(id = reportId)
        localRiskReports.add(0, savedReport)

        backgroundScope.launch {
            val fs = firestore
            if (fs != null) {
                try {
                    val data = hashMapOf(
                        "reportedBy" to report.reportedBy,
                        "riskType" to report.riskType,
                        "description" to report.description,
                        "severity" to report.severity,
                        "lat" to report.lat,
                        "lng" to report.lng,
                        "destinationName" to report.destinationName,
                        "createdAt" to (report.createdAt ?: Timestamp.now())
                    )
                    fs.collection("riskReports").document(reportId).set(data, SetOptions.merge()).await()
                } catch (e: Throwable) {
                    Log.w(TAG, "addRiskReport background Firestore notice: ${e.message}")
                }
            }
        }
        return Result.success(reportId)
    }

    suspend fun seedInitialRiskReportsIfEmpty() {
        backgroundScope.launch {
            val fs = firestore ?: return@launch
            try {
                val existing = withTimeoutOrNull(2000L) {
                    fs.collection("riskReports").limit(1).get().await()
                }
                if (existing != null && existing.isEmpty) {
                    val batch = fs.batch()
                    for (r in localRiskReports) {
                        val docRef = fs.collection("riskReports").document(r.id)
                        val map = hashMapOf(
                            "reportedBy" to r.reportedBy,
                            "riskType" to r.riskType,
                            "description" to r.description,
                            "severity" to r.severity,
                            "lat" to r.lat,
                            "lng" to r.lng,
                            "destinationName" to r.destinationName,
                            "createdAt" to (r.createdAt ?: Timestamp.now())
                        )
                        batch.set(docRef, map)
                    }
                    batch.commit().await()
                }
            } catch (e: Throwable) {
                Log.w(TAG, "seedInitialRiskReportsIfEmpty background notice: ${e.message}")
            }
        }
    }

    // -------------------------------------------------------------
    // VERIFIED SERVICE PROVIDER PORTAL API & REAL-TIME DISPATCH
    // -------------------------------------------------------------

    /**
     * Observes all active SOS events in real-time for service providers on patrol / dispatch.
     */
    fun observeAllActiveSosEvents(): Flow<List<SosEvent>> = callbackFlow {
        // Compute initial active list from local cache
        fun getActiveLocal(): List<SosEvent> {
            return localSosEvents.values.filter { it.status == "active" }.toList()
        }

        trySend(getActiveLocal())

        val fs = firestore
        var listener: ListenerRegistration? = null
        if (fs != null) {
            try {
                listener = fs.collection("sosEvents")
                    .whereEqualTo("status", "active")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(getActiveLocal())
                            return@addSnapshotListener
                        }
                        val list = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(SosEvent::class.java)?.copy(eventId = doc.id)
                        } ?: getActiveLocal()
                        trySend(list)
                    }
            } catch (e: Throwable) {
                trySend(getActiveLocal())
            }
        }
        awaitClose { listener?.remove() }
    }

    /**
     * Observes tourist assistance & booking queue for verified destination providers.
     */
    fun observeProviderAssistanceRequests(): Flow<List<com.example.data.model.ProviderAssistanceRequest>> = callbackFlow {
        if (localAssistanceRequests.isEmpty()) {
            localAssistanceRequests.addAll(
                listOf(
                    com.example.data.model.ProviderAssistanceRequest(
                        id = "req_01",
                        travelerName = "Emma Watson (UK)",
                        travelerPhone = "+44 7700 900077",
                        serviceType = "Tourist Police Escort",
                        destinationLocality = "Paharganj Night Market to Hotel",
                        details = "Solo traveler requesting verified tourist escort after train arrival.",
                        requestedAt = "4 mins ago",
                        status = "pending",
                        lat = 28.6430,
                        lng = 77.2150,
                        distanceKm = 0.8
                    ),
                    com.example.data.model.ProviderAssistanceRequest(
                        id = "req_02",
                        travelerName = "Carlos Mendez (Spain)",
                        travelerPhone = "+34 612 345 678",
                        serviceType = "Safe Cab Dispatch",
                        destinationLocality = "Red Fort to Aerocity Hotel",
                        details = "Pre-verified meter cab booking requested with GPS tracking.",
                        requestedAt = "12 mins ago",
                        status = "dispatched",
                        lat = 28.6562,
                        lng = 77.2410,
                        distanceKm = 1.6
                    ),
                    com.example.data.model.ProviderAssistanceRequest(
                        id = "req_03",
                        travelerName = "Yuki Tanaka (Japan)",
                        travelerPhone = "+81 90 1234 5678",
                        serviceType = "Certified Guide",
                        destinationLocality = "Humayun's Tomb Heritage Walk",
                        details = "Official English/Japanese speaking authorized guide verification requested.",
                        requestedAt = "25 mins ago",
                        status = "completed",
                        lat = 28.5933,
                        lng = 77.2507,
                        distanceKm = 3.2
                    )
                )
            )
        }
        trySend(localAssistanceRequests.toList())
        awaitClose { }
    }

    fun updateAssistanceRequestStatus(requestId: String, status: String) {
        val index = localAssistanceRequests.indexOfFirst { it.id == requestId }
        if (index != -1) {
            val updated = localAssistanceRequests[index].copy(status = status)
            localAssistanceRequests[index] = updated
            localAssistanceRequestsFlow.value = localAssistanceRequests.toList()
        }
    }

    fun updateProviderDutyStatus(uid: String, dutyStatus: String) {
        val existing = localProfiles[uid] ?: localDb.getUserProfile(uid)
        if (existing != null) {
            val updated = existing.copy(dutyStatus = dutyStatus)
            localProfiles[uid] = updated
            localDb.saveUserProfile(updated)
            localProfileFlows[uid]?.value = updated
        }
    }

    fun simulateIncomingDistressAlert(
        travelerName: String = "Sarah Jenkins (UK)",
        phone: String = "+44 7911 654321",
        locality: String = "Connaught Place Inner Circle"
    ): SosEvent {
        val eventId = "sos_alert_${System.currentTimeMillis().toString().takeLast(6)}"
        val event = SosEvent(
            eventId = eventId,
            userId = "traveler_sample_distress",
            status = "active",
            lat = 28.6328 + (Math.random() - 0.5) * 0.01,
            lng = 77.2197 + (Math.random() - 0.5) * 0.01,
            accuracyMeters = 8f,
            locationTimestamp = Timestamp.now(),
            notifiedContacts = listOf(phone, "Tourist Police Central Command"),
            createdAt = Timestamp.now()
        )
        localSosEvents[eventId] = event
        localAllActiveSosFlow.value = localSosEvents.values.filter { it.status == "active" }.toList()
        return event
    }
}
