package com.example.playcoach.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.entities.FormationEntity
import com.example.playcoach.data.entities.PlayerPositionEntity
import com.example.playcoach.data.repositories.FormationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class FormationViewModel @Inject constructor(
    private val repository: FormationRepository
) : ViewModel() {

    private val _formations = MutableStateFlow<List<FormationEntity>>(emptyList())
    val formations: StateFlow<List<FormationEntity>> = _formations.asStateFlow()

    private val _positions = MutableStateFlow<List<PlayerPositionEntity>>(emptyList())
    val positions: StateFlow<List<PlayerPositionEntity>> = _positions.asStateFlow()

    private val _selectedFormation = MutableStateFlow<FormationEntity?>(null)
    val selectedFormation: StateFlow<FormationEntity?> = _selectedFormation.asStateFlow()

    fun loadFormationsByTeam(team: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFormationsByTeam(team).collectLatest {
                _formations.value = it
            }
        }
    }

    fun loadPositionsForFormation(formationId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPositionsByFormation(formationId).collectLatest {
                _positions.value = it
            }
        }
    }

    fun createFormationWithPositions(name: String, team: String, positions: List<PlayerPositionEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            val formation = FormationEntity(name = name, team = team)
            repository.createFormationWithPositions(formation, positions)
            loadFormationsByTeam(team)
        }
    }

    fun updatePositions(formationId: Int, newPositions: List<PlayerPositionEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePositions(formationId, newPositions)
            loadPositionsForFormation(formationId)
        }
    }

    fun deleteFormation(formation: FormationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFormation(formation)
            loadFormationsByTeam(formation.team)
        }
    }

    fun selectFormation(formation: FormationEntity) {
        _selectedFormation.value = formation
        loadPositionsForFormation(formation.id)
    }

    fun clearSelection() {
        _selectedFormation.value = null
        _positions.value = emptyList()
    }
}
