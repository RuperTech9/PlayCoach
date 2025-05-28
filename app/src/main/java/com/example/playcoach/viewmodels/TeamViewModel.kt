package com.example.playcoach.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.entities.TeamEntity
import com.example.playcoach.data.repositories.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val repository: TeamRepository
) : ViewModel() {

    private val _teams = MutableStateFlow<List<TeamEntity>>(emptyList())
    val teams: StateFlow<List<TeamEntity>> = _teams.asStateFlow()

    // Form field for new team name
    private val _newTeamName = MutableStateFlow("")
    val newTeamName: StateFlow<String> = _newTeamName.asStateFlow()

    init {
        // Load teams from the database
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllTeams().collectLatest { list ->
                _teams.value = list
            }
        }
    }

    fun updateNewTeamName(name: String) {
        _newTeamName.value = name
    }

    fun insertTeam() {
        val name = _newTeamName.value
        if (name.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.insertTeam(TeamEntity(name = name))
                withContext(Dispatchers.Main) {
                    _newTeamName.value = ""
                }
            }
        }
    }
}
