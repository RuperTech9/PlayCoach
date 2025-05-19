package com.example.playcoach.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.entities.PlayerEntity
import com.example.playcoach.data.repositories.CallUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class CallUpViewModel @Inject constructor(
    private val repository: CallUpRepository
) : ViewModel() {

    private val _calledUpPlayers = MutableStateFlow<List<Int>>(emptyList())
    private val calledUpPlayers: StateFlow<List<Int>> = _calledUpPlayers

    data class PlayerDialog(val player: PlayerEntity, val matchdayId: Int)
    private val _playerDialog = MutableStateFlow<PlayerDialog?>(null)
    val playerDialog: StateFlow<PlayerDialog?> = _playerDialog

    fun getCalledUpPlayers(matchdayId: Int): StateFlow<List<Int>> {
        viewModelScope.launch {
            repository.getCalledUpPlayers(matchdayId).collect { list ->
                _calledUpPlayers.value = list
            }
        }
        return calledUpPlayers
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
}
