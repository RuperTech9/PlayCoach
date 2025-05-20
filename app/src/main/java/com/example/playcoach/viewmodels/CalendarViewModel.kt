package com.example.playcoach.viewmodels

import androidx.lifecycle.ViewModel
import com.example.playcoach.data.entities.EventEntity
import com.example.playcoach.data.entities.MatchdayEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import java.time.LocalDate

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {

    private val _currentMonth = MutableStateFlow(LocalDate.now().monthValue)
    val currentMonth: StateFlow<Int> = _currentMonth

    private val _currentYear = MutableStateFlow(LocalDate.now().year)
    val currentYear: StateFlow<Int> = _currentYear

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate

    private val _selectedEvent = MutableStateFlow<EventEntity?>(null)
    val selectedEvent: StateFlow<EventEntity?> = _selectedEvent

    private val _selectedMatchday = MutableStateFlow<MatchdayEntity?>(null)
    val selectedMatchday: StateFlow<MatchdayEntity?> = _selectedMatchday

    private val _showAddEventDialog = MutableStateFlow(false)
    val showAddEventDialog: StateFlow<Boolean> = _showAddEventDialog

    private val _showDateSelector = MutableStateFlow(false)
    val showDateSelector: StateFlow<Boolean> = _showDateSelector

    private val _showAttendanceDialog = MutableStateFlow(false)
    val showAttendanceDialog: StateFlow<Boolean> = _showAttendanceDialog

    private val _showCallUpDialog = MutableStateFlow(false)
    val showCallUpDialog: StateFlow<Boolean> = _showCallUpDialog

    private val _visibleMatchdayIndex = MutableStateFlow(0)
    val visibleMatchdayIndex: StateFlow<Int> = _visibleMatchdayIndex

    fun nextMonth() {
        if (_currentMonth.value == 12) {
            _currentMonth.value = 1
            _currentYear.value++
        } else {
            _currentMonth.value++
        }
    }

    fun previousMonth() {
        if (_currentMonth.value == 1) {
            _currentMonth.value = 12
            _currentYear.value--
        } else {
            _currentMonth.value--
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun selectEvent(event: EventEntity?) {
        _selectedEvent.value = event
    }

    fun clearSelectedEvent() {
        _selectedEvent.value = null
    }

    fun selectMatchday(matchday: MatchdayEntity?) {
        _selectedMatchday.value = matchday
    }

    fun clearSelectedMatchday() {
        _selectedMatchday.value = null
    }

    fun setShowAddEventDialog(show: Boolean) {
        _showAddEventDialog.value = show
    }

    fun setShowDateSelector(show: Boolean) {
        _showDateSelector.value = show
    }

    fun setShowAttendanceDialog(show: Boolean) {
        _showAttendanceDialog.value = show
    }

    fun setShowCallUpDialog(show: Boolean) {
        _showCallUpDialog.value = show
    }

    fun setVisibleMatchdayIndex(index: Int) {
        _visibleMatchdayIndex.value = index
    }
}
