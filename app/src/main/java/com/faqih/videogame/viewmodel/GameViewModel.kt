package com.faqih.videogame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faqih.videogame.data.model.Game
import com.faqih.videogame.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GamesUiState {
    object Loading : GamesUiState
    data class Success(val games: List<Game>) : GamesUiState
    data class Error(val message: String) : GamesUiState
}

sealed interface GameDetailUiState {
    object Loading : GameDetailUiState
    data class Success(val game: Game) : GameDetailUiState
    data class Error(val message: String) : GameDetailUiState
}

class GameViewModel : ViewModel() {

    private val repository = GameRepository.instance

    // State daftar game
    private val _gamesUiState = MutableStateFlow<GamesUiState>(GamesUiState.Loading)
    val gamesUiState: StateFlow<GamesUiState> = _gamesUiState.asStateFlow()

    // State query pencarian
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // State detail game
    private val _detailUiState = MutableStateFlow<GameDetailUiState>(GameDetailUiState.Loading)
    val detailUiState: StateFlow<GameDetailUiState> = _detailUiState.asStateFlow()

    init {
        loadGames()
    }

    fun loadGames(search: String? = null) {
        viewModelScope.launch {
            _gamesUiState.value = GamesUiState.Loading

            try {
                val games = repository.getGames(search)
                _gamesUiState.value = GamesUiState.Success(games)
            } catch (e: Exception) {
                _gamesUiState.value = GamesUiState.Error(
                    e.message ?: "Gagal memuat data game"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearch() {
        val query = _searchQuery.value.trim()
        loadGames(search = query.ifEmpty { null })
    }

    fun clearSearch() {
        _searchQuery.value = ""
        loadGames()
    }

    fun loadGameDetail(gameId: Int) {
        viewModelScope.launch {
            _detailUiState.value = GameDetailUiState.Loading

            try {
                val game = repository.getGameDetail(gameId)
                _detailUiState.value = GameDetailUiState.Success(game)
            } catch (e: Exception) {
                _detailUiState.value = GameDetailUiState.Error(
                    e.message ?: "Gagal memuat detail game"
                )
            }
        }
    }

    fun retry() {
        loadGames(_searchQuery.value.trim().ifEmpty { null })
    }
}