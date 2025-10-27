package com.example.discoclub.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.discoclub.data.local.user.UserEntity

data class ProfileUiState(
    val user: UserEntity? = null
)

class ProfileViewModel : ViewModel() {

    private var _uiState = ProfileUiState()
    val uiState: ProfileUiState get() = _uiState

    fun setUser(user: UserEntity) {
        _uiState = _uiState.copy(user = user)
    }

    fun clearUser() {
        _uiState = _uiState.copy(user = null)
    }
}
