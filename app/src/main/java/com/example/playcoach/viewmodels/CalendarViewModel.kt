package com.example.playcoach.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.playcoach.data.entities.EventEntity
import com.example.playcoach.data.entities.MatchdayEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _currentMonth = savedStateHandle.getStateFlow("currentMonth", LocalDate.now().monthValue)
    val currentMonth: StateFlow<Int> = _currentMonth

    private val _currentYear = savedStateHandle.getStateFlow("currentYear", LocalDate.now().year)
    val currentYear: StateFlow<Int> = _currentYear

    private val _visibleMatchdayIndex = savedStateHandle.getStateFlow("visibleMatchdayIndex", 0)
    val visibleMatchdayIndex: StateFlow<Int> = _visibleMatchdayIndex

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

    fun nextMonth() {
        if (_currentMonth.value == 12) {
            savedStateHandle["currentMonth"] = 1
            savedStateHandle["currentYear"] = _currentYear.value + 1
        } else {
            savedStateHandle["currentMonth"] = _currentMonth.value + 1
        }
    }

    fun previousMonth() {
        if (_currentMonth.value == 1) {
            savedStateHandle["currentMonth"] = 12
            savedStateHandle["currentYear"] = _currentYear.value - 1
        } else {
            savedStateHandle["currentMonth"] = _currentMonth.value - 1
        }
    }

    fun setVisibleMatchdayIndex(index: Int) {
        savedStateHandle["visibleMatchdayIndex"] = index
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

    private var hasInitializedMatchdayIndex = false

    fun ensureInitialVisibleMatchdayIndex(matchdays: List<MatchdayEntity>) {
        if (hasInitializedMatchdayIndex) return

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val index = matchdays.indexOfFirst {
            runCatching { LocalDate.parse(it.date, formatter) }.getOrNull()?.let { date ->
                date >= today && (date.dayOfWeek == java.time.DayOfWeek.SATURDAY || date.dayOfWeek == java.time.DayOfWeek.SUNDAY)
            } ?: false
        }

        if (index >= 0) {
            setVisibleMatchdayIndex(index)
        }

        hasInitializedMatchdayIndex = true
    }
}
