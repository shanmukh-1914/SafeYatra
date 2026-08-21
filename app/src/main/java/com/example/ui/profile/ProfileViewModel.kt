package com.example.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserProfile
import com.example.data.repository.AuthRepository
import com.example.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val isEditing: Boolean = false,
    val editName: String = "",
    val editHomeCountry: String = "",
    val editLanguage: String = "English",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val isSignedOut: Boolean = false
)

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val userId = authRepository.currentUserId
        viewModelScope.launch {
            firestoreRepository.observeUserProfile(userId).collect { profile ->
                _uiState.value = _uiState.value.copy(
                    userProfile = profile,
                    editName = profile?.name ?: "",
                    editHomeCountry = profile?.homeCountry ?: "",
                    editLanguage = profile?.preferredLanguage ?: "English"
                )
            }
        }
    }

    fun startEditing() {
        val current = _uiState.value.userProfile
        _uiState.value = _uiState.value.copy(
            isEditing = true,
            editName = current?.name ?: "",
            editHomeCountry = current?.homeCountry ?: "",
            editLanguage = current?.preferredLanguage ?: "English",
            errorMessage = null,
            successMessage = null
        )
    }

    fun cancelEditing() {
        _uiState.value = _uiState.value.copy(isEditing = false)
    }

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(editName = name)
    }

    fun onHomeCountryChanged(country: String) {
        _uiState.value = _uiState.value.copy(editHomeCountry = country)
    }

    fun onLanguageChanged(lang: String) {
        _uiState.value = _uiState.value.copy(editLanguage = lang)
    }

    fun saveProfile() {
        val userId = authRepository.currentUserId
        val name = _uiState.value.editName.trim()
        val country = _uiState.value.editHomeCountry.trim()

        if (name.isBlank() || country.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Name and country cannot be empty.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val updated = (_uiState.value.userProfile ?: UserProfile(uid = userId)).copy(
                name = name,
                homeCountry = country,
                preferredLanguage = _uiState.value.editLanguage,
                phone = _uiState.value.userProfile?.phone ?: "+919876543210"
            )

            val result = firestoreRepository.saveUserProfile(updated)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEditing = false,
                        successMessage = "Profile updated in cloud database."
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Could not update profile: ${error.message}"
                    )
                }
            )
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = _uiState.value.copy(isSignedOut = true)
    }
}
