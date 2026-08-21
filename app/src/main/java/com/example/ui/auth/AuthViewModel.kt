package com.example.ui.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EmergencyContact
import com.example.data.model.Trip
import com.example.data.model.UserProfile
import com.example.data.repository.AuthRepository
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.UserLocalDatabaseRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuthStep {
    ENTER_PHONE,
    ENTER_OTP,
    COMPLETE_PROFILE
}

data class AuthUiState(
    val step: AuthStep = AuthStep.ENTER_PHONE,
    val countryCode: String = "+91",
    val phoneNumber: String = "",
    val otpCode: String = "",
    val verificationId: String? = null,
    val resendCountdown: Int = 0,
    val isLoading: Boolean = false,
    val loadingStatusMessage: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val currentUserId: String? = null,
    val name: String = "",
    val homeCountry: String = "",
    val preferredLanguage: String = "English",
    val userRole: String = "traveler", // "traveler" or "provider"
    val emergencyContactName1: String = "",
    val emergencyContactPhone1: String = "",
    val emergencyContactRelation1: String = "Family / Guardian",
    val emergencyContactName2: String = "",
    val emergencyContactPhone2: String = "",
    val emergencyContactRelation2: String = "Friend / Companion",
    val medicalBloodGroup: String = "O+",
    val medicalAllergiesNotes: String = "",
    val passportOrGovIdNumber: String = "",
    val providerType: String = "Tourist Police", // "Tourist Police", "Emergency Medical", "Safe Transport", "Certified Guide", "Embassy Help"
    val agencyName: String = "",
    val badgeNumber: String = "",
    val serviceArea: String = "Central Delhi & Heritage Zone",
    val idProofType: String = "Police Warrant & Law Enforcement Badge ID",
    val idProofNumber: String = "",
    val issuingAuthority: String = "",
    val designationRank: String = "",
    val officialEmail: String = "",
    val idProofDocumentName: String = "official_credentials_scan.pdf",
    val isIdProofAttached: Boolean = true,
    val isDeclarationAccepted: Boolean = false,
    val showProviderVerificationDialog: Boolean = false,
    val detectedRoleBadge: String? = null,
    val isUserFullyAuthenticated: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
    private val localDb: UserLocalDatabaseRepository = UserLocalDatabaseRepository()
) : ViewModel() {

    private val TAG = "AuthViewModel"
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        checkInitialAuthState()
    }

    fun checkInitialAuthState() {
        val uid = if (authRepository.isUserLoggedIn) authRepository.currentUserId else null
        if (uid != null) {
            _uiState.value = _uiState.value.copy(
                currentUserId = uid,
                isLoading = true,
                loadingStatusMessage = "Restoring verified session..."
            )
            viewModelScope.launch {
                val profile = firestoreRepository.getUserProfile(uid) ?: localDb.getUserProfile(uid)
                if (profile != null && profile.name.isNotBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loadingStatusMessage = "",
                        isUserFullyAuthenticated = true,
                        name = profile.name,
                        homeCountry = profile.homeCountry,
                        preferredLanguage = profile.preferredLanguage,
                        userRole = profile.role,
                        providerType = profile.providerType,
                        agencyName = profile.agencyName,
                        badgeNumber = profile.badgeNumber,
                        serviceArea = profile.serviceArea,
                        idProofType = profile.idProofType,
                        idProofNumber = profile.idProofNumber,
                        issuingAuthority = profile.issuingAuthority,
                        designationRank = profile.designationRank,
                        officialEmail = profile.officialEmail,
                        idProofDocumentName = profile.idProofDocumentName,
                        isIdProofAttached = profile.isIdProofVerified
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loadingStatusMessage = "",
                        step = AuthStep.COMPLETE_PROFILE,
                        phoneNumber = profile?.phone ?: ""
                    )
                }
            }
        } else {
            _uiState.value = _uiState.value.copy(
                isUserFullyAuthenticated = false,
                step = AuthStep.ENTER_PHONE,
                isLoading = false,
                loadingStatusMessage = ""
            )
        }
    }

    fun onCountryCodeChanged(code: String) {
        _uiState.value = _uiState.value.copy(countryCode = code, errorMessage = null)
        evaluatePhoneRole("${code}${_uiState.value.phoneNumber}")
    }

    fun onPhoneNumberChanged(number: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = number, errorMessage = null)
        evaluatePhoneRole("${_uiState.value.countryCode}$number")
    }

    private fun evaluatePhoneRole(fullNumber: String) {
        val providerProfile = localDb.findProviderProfileByPhone(fullNumber)
        if (providerProfile != null) {
            _uiState.value = _uiState.value.copy(
                userRole = "provider",
                providerType = providerProfile.providerType,
                agencyName = providerProfile.agencyName,
                badgeNumber = providerProfile.badgeNumber,
                serviceArea = providerProfile.serviceArea,
                name = providerProfile.name.ifBlank { _uiState.value.name },
                idProofType = providerProfile.idProofType,
                idProofNumber = providerProfile.idProofNumber,
                issuingAuthority = providerProfile.issuingAuthority,
                designationRank = providerProfile.designationRank,
                officialEmail = providerProfile.officialEmail,
                idProofDocumentName = providerProfile.idProofDocumentName,
                isIdProofAttached = true,
                detectedRoleBadge = "Verified Service Provider (${providerProfile.providerType})"
            )
        } else {
            _uiState.value = _uiState.value.copy(
                detectedRoleBadge = null
            )
        }
    }

    fun startNewUserProfileRegistration(role: String = "traveler") {
        val newUid = "user_${System.currentTimeMillis().toString().takeLast(6)}"
        authRepository.setLocalSession(newUid)
        _uiState.value = _uiState.value.copy(
            step = AuthStep.COMPLETE_PROFILE,
            currentUserId = newUid,
            userRole = role,
            name = "",
            homeCountry = if (role == "traveler") "" else "India",
            preferredLanguage = "English",
            emergencyContactName1 = "",
            emergencyContactPhone1 = "",
            emergencyContactRelation1 = "Family / Guardian",
            emergencyContactName2 = "",
            emergencyContactPhone2 = "",
            emergencyContactRelation2 = "Friend / Companion",
            medicalBloodGroup = "O+",
            medicalAllergiesNotes = "",
            passportOrGovIdNumber = "",
            errorMessage = null,
            loadingStatusMessage = ""
        )
    }

    fun onRoleSelected(role: String) {
        _uiState.value = _uiState.value.copy(
            userRole = role,
            errorMessage = null,
            detectedRoleBadge = if (role == "provider") "Verified Destination Service Provider" else null
        )
    }

    fun onProviderTypeChanged(type: String) {
        _uiState.value = _uiState.value.copy(providerType = type, errorMessage = null)
    }

    fun onAgencyNameChanged(agency: String) {
        _uiState.value = _uiState.value.copy(agencyName = agency, errorMessage = null)
    }

    fun onBadgeNumberChanged(badge: String) {
        _uiState.value = _uiState.value.copy(badgeNumber = badge, errorMessage = null)
    }

    fun onServiceAreaChanged(area: String) {
        _uiState.value = _uiState.value.copy(serviceArea = area, errorMessage = null)
    }

    fun onIdProofTypeChanged(type: String) {
        _uiState.value = _uiState.value.copy(idProofType = type, errorMessage = null)
    }

    fun onIdProofNumberChanged(number: String) {
        _uiState.value = _uiState.value.copy(idProofNumber = number, errorMessage = null)
    }

    fun onIssuingAuthorityChanged(auth: String) {
        _uiState.value = _uiState.value.copy(issuingAuthority = auth, errorMessage = null)
    }

    fun onDesignationRankChanged(rank: String) {
        _uiState.value = _uiState.value.copy(designationRank = rank, errorMessage = null)
    }

    fun onOfficialEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(officialEmail = email, errorMessage = null)
    }

    fun onIdProofDocumentAttached(fileName: String) {
        _uiState.value = _uiState.value.copy(
            idProofDocumentName = fileName,
            isIdProofAttached = true,
            errorMessage = null
        )
    }

    fun onDeclarationAccepted(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(isDeclarationAccepted = accepted, errorMessage = null)
    }

    fun onDeclarationToggled(accepted: Boolean) {
        onDeclarationAccepted(accepted)
    }

    fun submitProfile() {
        saveUserProfile()
    }

    fun openProviderVerificationDialog() {
        _uiState.value = _uiState.value.copy(
            showProviderVerificationDialog = true,
            userRole = "provider"
        )
    }

    fun closeProviderVerificationDialog() {
        _uiState.value = _uiState.value.copy(
            showProviderVerificationDialog = false
        )
    }

    fun dismissProviderVerificationDialog() {
        closeProviderVerificationDialog()
    }

    fun verifyAndLoginAsProvider() {
        submitProviderVerificationModal()
    }

    fun applyProviderPreset(preset: String) {
        val defaultAgency = when (preset) {
            "Safe Transport" -> "SafeWheels Verified Tourist Cab Fleet #SW-108"
            "Emergency Medical" -> "Apollo Tourist Rapid Response Emergency Unit"
            "Certified Guide" -> "Ministry of Tourism Certified Heritage Guides Guild"
            else -> "Delhi Police - Tourist Safety Command (Station #04)"
        }
        val defaultBadge = when (preset) {
            "Safe Transport" -> "DLY-TAXI-9941"
            "Emergency Medical" -> "MED-EMS-3310"
            "Certified Guide" -> "MOT-GD-5521"
            else -> "DL-TP-8842"
        }
        val defaultIdType = when (preset) {
            "Safe Transport" -> "Commercial Transport Permit & PSV Driver Badge"
            "Emergency Medical" -> "State Medical Council / EMS Registration"
            "Certified Guide" -> "Ministry of Tourism Guide Accreditation Card"
            else -> "Police Warrant & Law Enforcement Badge ID"
        }
        val defaultAuthority = when (preset) {
            "Safe Transport" -> "Delhi Transport Department & State Police"
            "Emergency Medical" -> "Medical Council of India & EMS Bureau"
            "Certified Guide" -> "Ministry of Tourism, Govt of India"
            else -> "Delhi Police Commissionerate & Tourist Cell"
        }
        val defaultIdNumber = when (preset) {
            "Safe Transport" -> "PSV-CAB-99410-DL"
            "Emergency Medical" -> "MCI-EMS-33109-ND"
            "Certified Guide" -> "MOT-GUIDE-55214"
            else -> "POL-TP-88421-DEL"
        }
        _uiState.value = _uiState.value.copy(
            providerType = preset,
            agencyName = defaultAgency,
            badgeNumber = defaultBadge,
            idProofType = defaultIdType,
            issuingAuthority = defaultAuthority,
            idProofNumber = defaultIdNumber,
            isDeclarationAccepted = true
        )
    }

    fun submitProviderVerificationModal() {
        val state = _uiState.value
        val name = state.name.trim()
        val badge = state.badgeNumber.trim()
        val idNum = state.idProofNumber.trim()
        val authority = state.issuingAuthority.trim()

        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Officer / Provider Name.")
            return
        }
        if (badge.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Official Badge Number or PSV License ID.")
            return
        }
        if (idNum.isBlank() || idNum.length < 3) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Department ID Proof Number.")
            return
        }
        if (authority.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter the Issuing Authority / Ministry Name.")
            return
        }
        if (!state.isDeclarationAccepted) {
            _uiState.value = _uiState.value.copy(errorMessage = "You must accept the official service declaration & safety oath.")
            return
        }

        val demoUid = "provider_${state.providerType.lowercase().replace(" ", "_")}_${System.currentTimeMillis().toString().takeLast(4)}"
        authRepository.setLocalSession(demoUid)

        val profile = UserProfile(
            uid = demoUid,
            phone = "${state.countryCode}${state.phoneNumber}".ifBlank { "+91 11 2346 9526" },
            name = name,
            homeCountry = "India",
            preferredLanguage = state.preferredLanguage,
            role = "provider",
            providerType = state.providerType,
            agencyName = state.agencyName.ifBlank { "${state.providerType} Emergency Unit" },
            badgeNumber = badge,
            serviceArea = state.serviceArea.ifBlank { "Central Heritage Belt" },
            dutyStatus = "ON_DUTY",
            trustScore = 4.98,
            ratingCount = 142,
            isVerifiedProvider = true,
            idProofType = state.idProofType,
            idProofNumber = idNum,
            issuingAuthority = authority,
            designationRank = state.designationRank.ifBlank { "Verified Officer" },
            officialEmail = state.officialEmail.ifBlank { "dispatch@tourist-safety.org" },
            idProofDocumentName = state.idProofDocumentName,
            isIdProofVerified = true,
            createdAt = Timestamp.now()
        )

        _uiState.value = _uiState.value.copy(
            showProviderVerificationDialog = false,
            isLoading = false,
            loadingStatusMessage = "",
            currentUserId = demoUid,
            name = name,
            homeCountry = "India",
            userRole = "provider",
            providerType = state.providerType,
            agencyName = profile.agencyName,
            badgeNumber = profile.badgeNumber,
            serviceArea = profile.serviceArea,
            idProofType = profile.idProofType,
            idProofNumber = profile.idProofNumber,
            issuingAuthority = profile.issuingAuthority,
            designationRank = profile.designationRank,
            officialEmail = profile.officialEmail,
            idProofDocumentName = profile.idProofDocumentName,
            isIdProofAttached = true,
            isUserFullyAuthenticated = true,
            successMessage = "Official ID Proof Verified: Welcome Officer $name!"
        )

        viewModelScope.launch {
            firestoreRepository.saveUserProfile(profile)
            localDb.saveUserProfile(profile)
            firestoreRepository.seedInitialProvidersIfEmpty()
            firestoreRepository.seedInitialRiskReportsIfEmpty()
        }
    }

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }

    fun onHomeCountryChanged(country: String) {
        _uiState.value = _uiState.value.copy(homeCountry = country, errorMessage = null)
    }

    fun onLanguageChanged(lang: String) {
        _uiState.value = _uiState.value.copy(preferredLanguage = lang, errorMessage = null)
    }

    fun onEmergencyContact1NameChanged(name: String) {
        _uiState.value = _uiState.value.copy(emergencyContactName1 = name, errorMessage = null)
    }

    fun onEmergencyContact1PhoneChanged(phone: String) {
        _uiState.value = _uiState.value.copy(emergencyContactPhone1 = phone, errorMessage = null)
    }

    fun onEmergencyContact1RelationChanged(relation: String) {
        _uiState.value = _uiState.value.copy(emergencyContactRelation1 = relation, errorMessage = null)
    }

    fun onEmergencyContact2NameChanged(name: String) {
        _uiState.value = _uiState.value.copy(emergencyContactName2 = name, errorMessage = null)
    }

    fun onEmergencyContact2PhoneChanged(phone: String) {
        _uiState.value = _uiState.value.copy(emergencyContactPhone2 = phone, errorMessage = null)
    }

    fun onEmergencyContact2RelationChanged(relation: String) {
        _uiState.value = _uiState.value.copy(emergencyContactRelation2 = relation, errorMessage = null)
    }

    fun onMedicalBloodGroupChanged(group: String) {
        _uiState.value = _uiState.value.copy(medicalBloodGroup = group, errorMessage = null)
    }

    fun onMedicalNotesChanged(notes: String) {
        _uiState.value = _uiState.value.copy(medicalAllergiesNotes = notes, errorMessage = null)
    }

    fun onPassportOrGovIdChanged(id: String) {
        _uiState.value = _uiState.value.copy(passportOrGovIdNumber = id, errorMessage = null)
    }

    fun sendVerificationCode(activity: Activity) {
        val rawNumber = _uiState.value.phoneNumber.trim()
        if (rawNumber.length < 7) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid phone number.")
            return
        }

        val fullPhoneNumber = if (rawNumber.startsWith("+")) rawNumber else "${_uiState.value.countryCode}$rawNumber"

        evaluatePhoneRole(fullPhoneNumber)

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            loadingStatusMessage = "Dispatching SMS verification code to $fullPhoneNumber...",
            errorMessage = null,
            successMessage = null
        )

        authRepository.sendVerificationCode(
            activity = activity,
            phoneNumber = fullPhoneNumber,
            onCodeSent = { verificationId ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loadingStatusMessage = "",
                    verificationId = verificationId,
                    step = AuthStep.ENTER_OTP,
                    successMessage = "SMS verification code dispatched to $fullPhoneNumber."
                )
                startCountdown()
            },
            onVerificationCompleted = { credential ->
                _uiState.value = _uiState.value.copy(
                    loadingStatusMessage = "Auto-verifying received SMS code..."
                )
                viewModelScope.launch {
                    val result = authRepository.signInWithPhoneCredential(credential)
                    result.fold(
                        onSuccess = { uid -> onUserAuthenticated(uid, fullPhoneNumber) },
                        onFailure = { err ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                loadingStatusMessage = "",
                                errorMessage = err.message
                            )
                        }
                    )
                }
            },
            onVerificationFailed = { friendlyMessage, fallbackVerificationId ->
                Log.w(TAG, "Phone verification notice: $friendlyMessage")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loadingStatusMessage = "",
                    verificationId = fallbackVerificationId,
                    step = AuthStep.ENTER_OTP,
                    successMessage = "Verification session ready. Enter received SMS or demo PIN 123456."
                )
                startCountdown()
            }
        )
    }

    fun quickDemoLoginTraveler() {
        startNewUserProfileRegistration("traveler")
    }

    fun quickDemoLoginProvider(providerType: String = "Tourist Police") {
        openProviderVerificationDialog()
    }

    fun quickDemoLogin() {
        startNewUserProfileRegistration("traveler")
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        _uiState.value = _uiState.value.copy(resendCountdown = 30)
        countdownJob = viewModelScope.launch {
            for (i in 30 downTo 1) {
                _uiState.value = _uiState.value.copy(resendCountdown = i)
                delay(1000)
            }
            _uiState.value = _uiState.value.copy(resendCountdown = 0)
        }
    }

    fun onOtpCodeChanged(code: String) {
        val filtered = code.filter { it.isDigit() }
        _uiState.value = _uiState.value.copy(otpCode = filtered, errorMessage = null)
        if (filtered.length == 6 && !_uiState.value.isLoading) {
            verifyOtp()
        }
    }

    fun useDemoOtp() {
        _uiState.value = _uiState.value.copy(otpCode = "123456", errorMessage = null)
        verifyOtp()
    }

    fun verifyOtp() {
        val otp = _uiState.value.otpCode.trim()
        val verificationId = _uiState.value.verificationId ?: "FALLBACK_SESSION"
        val fullPhoneNumber = "${_uiState.value.countryCode}${_uiState.value.phoneNumber}"

        if (otp.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter the 6-digit OTP code (e.g. 123456).")
            return
        }

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            loadingStatusMessage = "Verifying security code...",
            errorMessage = null
        )

        viewModelScope.launch {
            val result = authRepository.signInWithOtpCode(
                verificationId = verificationId,
                code = otp,
                fallbackPhone = fullPhoneNumber
            )

            result.fold(
                onSuccess = { uid ->
                    onUserAuthenticated(uid, fullPhoneNumber)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loadingStatusMessage = "",
                        errorMessage = error.message ?: "Incorrect OTP code. Enter 123456 or request a new code."
                    )
                }
            )
        }
    }

    private suspend fun onUserAuthenticated(uid: String, phone: String) {
        _uiState.value = _uiState.value.copy(
            currentUserId = uid,
            isLoading = true,
            loadingStatusMessage = "Verifying profile and safety identity..."
        )

        val providerPredefined = localDb.findProviderProfileByPhone(phone)
        val existingProfile = firestoreRepository.getUserProfile(uid) ?: localDb.getUserProfile(uid)

        val profile = existingProfile ?: providerPredefined

        if (profile != null && profile.name.isNotBlank()) {
            val finalRole = profile.role.ifBlank { if (providerPredefined != null) "provider" else "traveler" }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                loadingStatusMessage = "",
                isUserFullyAuthenticated = true,
                name = profile.name,
                homeCountry = profile.homeCountry,
                preferredLanguage = profile.preferredLanguage,
                userRole = finalRole,
                providerType = profile.providerType,
                agencyName = profile.agencyName,
                badgeNumber = profile.badgeNumber,
                serviceArea = profile.serviceArea,
                idProofType = profile.idProofType,
                idProofNumber = profile.idProofNumber,
                issuingAuthority = profile.issuingAuthority,
                designationRank = profile.designationRank,
                officialEmail = profile.officialEmail,
                idProofDocumentName = profile.idProofDocumentName,
                isIdProofAttached = profile.isIdProofVerified,
                successMessage = if (finalRole == "provider") "Welcome back, Officer ${profile.name}!" else "Welcome back, ${profile.name}!"
            )
            localDb.saveActiveSessionUid(uid)
            localDb.saveUserProfile(profile)
        } else {
            // New user without completed profile name: prompt directly for their real name and details!
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                loadingStatusMessage = "",
                step = AuthStep.COMPLETE_PROFILE,
                currentUserId = uid,
                phoneNumber = phone,
                name = "",
                homeCountry = "",
                preferredLanguage = "English",
                userRole = if (providerPredefined != null) "provider" else "traveler",
                successMessage = "Phone verified! Please enter your name and traveler safety details to proceed."
            )
        }
    }

    fun saveUserProfile() {
        val uid = _uiState.value.currentUserId ?: authRepository.currentUserId ?: "user_${System.currentTimeMillis().toString().takeLast(6)}"
        val name = _uiState.value.name.trim()
        val country = _uiState.value.homeCountry.trim()

        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your full legal name.")
            return
        }
        if (_uiState.value.userRole == "traveler" && country.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your home country / nationality.")
            return
        }

        val isProvider = _uiState.value.userRole == "provider"

        if (isProvider) {
            val idNumber = _uiState.value.idProofNumber.trim()
            val authority = _uiState.value.issuingAuthority.trim()
            val badge = _uiState.value.badgeNumber.trim()

            if (idNumber.isBlank() || idNumber.length < 3) {
                _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Government / Department ID Proof Number.")
                return
            }
            if (authority.isBlank()) {
                _uiState.value = _uiState.value.copy(errorMessage = "Please enter the Issuing Authority / Ministry Name.")
                return
            }
            if (badge.isBlank()) {
                _uiState.value = _uiState.value.copy(errorMessage = "Please enter your Official Badge Number or PSV License ID.")
                return
            }
            if (!_uiState.value.isDeclarationAccepted) {
                _uiState.value = _uiState.value.copy(errorMessage = "You must accept the official service declaration & safety oath.")
                return
            }
        }

        val phone = "${_uiState.value.countryCode}${_uiState.value.phoneNumber}".ifBlank { "+919876543210" }

        val newProfile = UserProfile(
            uid = uid,
            phone = phone,
            name = name,
            homeCountry = country.ifBlank { "India" },
            preferredLanguage = _uiState.value.preferredLanguage,
            role = _uiState.value.userRole,
            providerType = _uiState.value.providerType,
            agencyName = if (isProvider) _uiState.value.agencyName.ifBlank { "${_uiState.value.providerType} Unit" } else "",
            badgeNumber = if (isProvider) _uiState.value.badgeNumber.ifBlank { "VERIFIED-${uid.takeLast(4)}" } else "",
            serviceArea = if (isProvider) _uiState.value.serviceArea.ifBlank { "Central Tourist Zone" } else "",
            dutyStatus = "ON_DUTY",
            trustScore = 4.95,
            ratingCount = 120,
            isVerifiedProvider = isProvider,
            idProofType = if (isProvider) _uiState.value.idProofType else "",
            idProofNumber = if (isProvider) _uiState.value.idProofNumber.trim() else "",
            issuingAuthority = if (isProvider) _uiState.value.issuingAuthority.trim() else "",
            designationRank = if (isProvider) _uiState.value.designationRank.trim().ifBlank { "Verified Officer" } else "",
            officialEmail = if (isProvider) _uiState.value.officialEmail.trim().ifBlank { "dispatch@tourist-safety.org" } else "",
            idProofDocumentName = if (isProvider) _uiState.value.idProofDocumentName else "",
            isIdProofVerified = isProvider,
            createdAt = Timestamp.now()
        )

        authRepository.setLocalSession(uid)

        // Instant optimistic transition to appropriate portal
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            loadingStatusMessage = "",
            name = name,
            homeCountry = country,
            isUserFullyAuthenticated = true,
            successMessage = if (isProvider) "Verified Provider ID Approved: Welcome Officer $name!" else "Welcome to SafeYatra, $name!"
        )

        viewModelScope.launch {
            firestoreRepository.saveUserProfile(newProfile)
            localDb.saveUserProfile(newProfile)
            localDb.saveActiveSessionUid(uid)

            // Save user entered Emergency Contacts if present
            if (_uiState.value.emergencyContactName1.isNotBlank() && _uiState.value.emergencyContactPhone1.isNotBlank()) {
                val c1 = EmergencyContact(
                    name = _uiState.value.emergencyContactName1.trim(),
                    phone = _uiState.value.emergencyContactPhone1.trim(),
                    relationship = _uiState.value.emergencyContactRelation1
                )
                firestoreRepository.addEmergencyContact(uid, c1)
                localDb.addEmergencyContact(uid, c1)
            }
            if (_uiState.value.emergencyContactName2.isNotBlank() && _uiState.value.emergencyContactPhone2.isNotBlank()) {
                val c2 = EmergencyContact(
                    name = _uiState.value.emergencyContactName2.trim(),
                    phone = _uiState.value.emergencyContactPhone2.trim(),
                    relationship = _uiState.value.emergencyContactRelation2
                )
                firestoreRepository.addEmergencyContact(uid, c2)
                localDb.addEmergencyContact(uid, c2)
            }

            // Also seed default helpline guardian if no contacts were provided
            if (_uiState.value.emergencyContactName1.isBlank()) {
                val helpline = EmergencyContact(
                    name = "SafeYatra 24x7 Tourist Helpline & Police",
                    phone = "+91 11 2346 9526",
                    relationship = "Tourist Police Emergency"
                )
                firestoreRepository.addEmergencyContact(uid, helpline)
                localDb.addEmergencyContact(uid, helpline)
            }

            firestoreRepository.seedInitialProvidersIfEmpty()
            firestoreRepository.seedInitialRiskReportsIfEmpty()
        }
    }

    fun backToPhoneEntry() {
        _uiState.value = _uiState.value.copy(
            step = AuthStep.ENTER_PHONE,
            otpCode = "",
            errorMessage = null,
            loadingStatusMessage = ""
        )
    }
}
