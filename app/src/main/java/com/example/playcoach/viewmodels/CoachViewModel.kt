package com.example.playcoach.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.database.AppDatabase
import com.example.playcoach.data.entities.CoachEntity
import com.example.playcoach.data.repositories.CoachRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class CoachViewModel @Inject constructor(
    private val repository: CoachRepository
) : ViewModel() {

    private val _coaches = MutableStateFlow<List<CoachEntity>>(emptyList())
    val coaches: StateFlow<List<CoachEntity>> = _coaches.asStateFlow()

    fun loadCoachesForTeam(team: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCoachesByTeam(team).collectLatest { list ->
                _coaches.value = list
            }
        }
    }

    fun deleteCoach(coach: CoachEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCoach(coach)
        }
    }

    suspend fun addCoachIfPossible(team: String, name: String): Boolean {
        return withContext(Dispatchers.IO) {
            val currentCoaches = repository.getCoachesByTeam(team).first()
            if (currentCoaches.size >= 3) {
                return@withContext false
            }
            val newCoach = CoachEntity(
                name = name.trim(),
                team = team
            )
            repository.insertCoach(newCoach)
            true
        }
    }
}
