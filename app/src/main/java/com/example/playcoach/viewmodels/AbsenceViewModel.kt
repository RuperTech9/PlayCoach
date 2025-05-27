package com.example.playcoach.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.entities.AbsenceEntity
import com.example.playcoach.data.repositories.AbsenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class AbsenceViewModel @Inject constructor(
    private val repository: AbsenceRepository
) : ViewModel() {

    private val _attendanceByDate = MutableStateFlow<List<AbsenceEntity>>(emptyList())
    val attendanceByDate: StateFlow<List<AbsenceEntity>> = _attendanceByDate.asStateFlow()

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _absenceCount = MutableStateFlow(0)
    val absenceCount: StateFlow<Int> = _absenceCount.asStateFlow()

    fun selectDate(date: String) {
        _selectedDate.value = date
        loadAttendanceByDate(date)
    }

    private fun loadAttendanceByDate(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAttendanceByDate(date).collectLatest { list ->
                _attendanceByDate.value = list
            }
        }
    }

    fun loadAbsenceCount(playerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteOrphanedAttendance()

            repository.countAbsences(playerId).collect { count ->
                _absenceCount.value = count
            }
        }
    }

    fun saveFullAttendance(date: String, allPlayers: List<Int>, presentPlayers: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.registerAttendanceForDate(date, allPlayers, presentPlayers)
            loadAttendanceByDate(date) // Refresh the StateFlow
        }
    }
}
