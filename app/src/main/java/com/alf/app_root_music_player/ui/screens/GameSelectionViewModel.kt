package com.alf.app_root_music_player.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alf.app_root_music_player.data.model.Game
import com.alf.app_root_music_player.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GameSelectionUiState(
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class GameSelectionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository(application)
    
    private val _uiState = MutableStateFlow(GameSelectionUiState())
    val uiState: StateFlow<GameSelectionUiState> = _uiState.asStateFlow()
    
    init {
        loadGames()
    }
    
    private fun loadGames() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val games = repository.getAllGames()
                _uiState.value = _uiState.value.copy(
                    games = games,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load games"
                )
            }
        }
    }
}

