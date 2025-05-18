package com.example.playcoach.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.database.AppDatabase
import com.example.playcoach.data.entities.TeamEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppDatabase.getRepository(application).teamRepository

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

    // Optional: Enable if you want to allow team deletion
    /*
    fun deleteTeam(team: TeamEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTeam(team)
        }
    }
    */
}
