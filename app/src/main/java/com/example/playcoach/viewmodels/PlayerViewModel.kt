package com.example.playcoach.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.database.AppDatabase
import com.example.playcoach.data.entities.PlayerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppDatabase.getRepository(application).playerRepository

    // Form fields
    private val _number = MutableStateFlow(0)
    val number: StateFlow<Int> = _number.asStateFlow()

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName.asStateFlow()

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    private val _position = MutableStateFlow("Player")
    val position: StateFlow<String> = _position.asStateFlow()

    private val _team = MutableStateFlow("")
    val team: StateFlow<String> = _team.asStateFlow()

    // List of players in a team
    private val _players = MutableStateFlow<List<PlayerEntity>>(emptyList())
    val players: StateFlow<List<PlayerEntity>> = _players.asStateFlow()

    fun loadPlayersByTeam(team: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPlayersByTeam(team).collectLatest { list ->
                _players.value = list
            }
        }
    }

    fun deletePlayer(player: PlayerEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePlayer(player)
        }
    }

    /**
     * Adds a player if the jersey number is not already taken in the team.
     * @return true if added successfully, false if the number is already in use.
     */
    suspend fun addPlayerIfPossible(
        team: String,
        firstName: String,
        lastName: String,
        nickname: String,
        number: Int,
        position: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val allPlayers = repository.getPlayersByTeam(team).first()
            val numberExists = allPlayers.any { it.number == number }
            if (numberExists) return@withContext false

            val newPlayer = PlayerEntity(
                number = number,
                firstName = firstName,
                lastName = lastName,
                nickname = nickname,
                position = position,
                team = team
            )

            repository.insertPlayer(newPlayer)
            true
        }
    }

    // Optional setters for form fields
    fun updateNumber(value: Int) {
        _number.value = value
    }

    fun updateFirstName(value: String) {
        _firstName.value = value
    }

    fun updateLastName(value: String) {
        _lastName.value = value
    }

    fun updateNickname(value: String) {
        _nickname.value = value
    }

    fun updatePosition(value: String) {
        _position.value = value
    }

    fun updateTeam(value: String) {
        _team.value = value
    }
}
