package com.example.playcoach.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.entities.PlayerEntity
import com.example.playcoach.data.repositories.CallUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CallUpUiState(
    val players: List<PlayerEntity> = emptyList(),
    val calledUp: List<Int> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CallUpViewModel @Inject constructor(
    private val repository: CallUpRepository
) : ViewModel() {

    private val _calledUpPlayers = MutableStateFlow<List<Int>>(emptyList())
    val calledUpPlayers: StateFlow<List<Int>> = _calledUpPlayers

    private val _isCallUpLoading = MutableStateFlow(false)
    private val isCallUpLoading: StateFlow<Boolean> = _isCallUpLoading

    data class PlayerDialog(val player: PlayerEntity, val matchdayId: Int)
    private val _playerDialog = MutableStateFlow<PlayerDialog?>(null)
    val playerDialog: StateFlow<PlayerDialog?> = _playerDialog

    fun loadCallUpForMatchday(matchdayId: Int) {
        viewModelScope.launch {
            _isCallUpLoading.value = true
            repository.getCalledUpPlayers(matchdayId).collect { list ->
                _calledUpPlayers.value = list
                _isCallUpLoading.value = false
            }
        }
    }


    fun saveCallUps(matchdayId: Int, playerIds: List<Int>) {
        viewModelScope.launch {
            repository.saveCallUps(matchdayId, playerIds)
        }
    }

    fun openPlayerStatsDialog(player: PlayerEntity, matchdayId: Int) {
        _playerDialog.value = PlayerDialog(player, matchdayId)
    }

    fun closePlayerStatsDialog() {
        _playerDialog.value = null
    }

    fun getCallUpUiState(playersFlow: StateFlow<List<PlayerEntity>>): StateFlow<CallUpUiState> {
        return combine(playersFlow, calledUpPlayers, isCallUpLoading) { players, calledUp, loading ->
            CallUpUiState(players = players, calledUp = calledUp, isLoading = loading)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CallUpUiState())
    }
}
