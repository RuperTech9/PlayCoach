package com.example.playcoach.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.database.AppDatabase
import com.example.playcoach.data.entities.EventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppDatabase.getRepository(application).eventRepository

    private val _selectedTeam = MutableStateFlow("Infantil A")
    val selectedTeam: StateFlow<String> = _selectedTeam.asStateFlow()

    private val _events = MutableStateFlow<List<EventEntity>>(emptyList())
    val events: StateFlow<List<EventEntity>> = _events.asStateFlow()

    private val _date = MutableStateFlow("")
    val date: StateFlow<String> = _date.asStateFlow()

    private val _type = MutableStateFlow("")
    val type: StateFlow<String> = _type.asStateFlow()

    private val _time = MutableStateFlow("")
    val time: StateFlow<String> = _time.asStateFlow()

    fun updateSelectedTeam(name: String) {
        _selectedTeam.value = name
        loadEventsByTeam(name)
    }

    fun updateDate(newDate: String) {
        _date.value = newDate
    }

    fun updateType(newType: String) {
        _type.value = newType
    }

    fun updateTime(newTime: String) {
        _time.value = newTime
    }

    fun loadEventsByTeam(team: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getEventsByTeam(team).collectLatest {
                _events.value = it
            }
        }
    }

    fun insertEvent() {
        val event = EventEntity(
            date = _date.value,
            type = _type.value,
            time = _time.value,
            team = _selectedTeam.value
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertEvent(event)
            withContext(Dispatchers.Main) {
                _date.value = ""
                _type.value = ""
                _time.value = ""
            }
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEventAndAbsences(event)
        }
    }

    fun updateEvent(event: EventEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEvent(event)
        }
    }
}
