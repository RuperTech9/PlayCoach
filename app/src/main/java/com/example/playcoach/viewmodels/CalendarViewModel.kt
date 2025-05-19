package com.example.playcoach.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.entities.EventEntity
import com.example.playcoach.data.entities.MatchdayEntity
import com.example.playcoach.data.repositories.EventRepository
import com.example.playcoach.data.repositories.MatchdayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val matchdayRepository: MatchdayRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _selectedTeam = MutableStateFlow<String?>(null)
    val selectedTeam: StateFlow<String?> = _selectedTeam.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _selectedEvent = MutableStateFlow<EventEntity?>(null)
    val selectedEvent: StateFlow<EventEntity?> = _selectedEvent.asStateFlow()

    private val _selectedMatchday = MutableStateFlow<MatchdayEntity?>(null)
    val selectedMatchday: StateFlow<MatchdayEntity?> = _selectedMatchday.asStateFlow()

    private val _showAddEventDialog = MutableStateFlow(false)
    val showAddEventDialog: StateFlow<Boolean> = _showAddEventDialog.asStateFlow()

    private val _showDateSelector = MutableStateFlow(false)
    val showDateSelector: StateFlow<Boolean> = _showDateSelector.asStateFlow()

    private val _showAttendanceDialog = MutableStateFlow(false)
    val showAttendanceDialog: StateFlow<Boolean> = _showAttendanceDialog.asStateFlow()

    private val _showCallUpDialog = MutableStateFlow(false)
    val showCallUpDialog: StateFlow<Boolean> = _showCallUpDialog.asStateFlow()

    private val _currentMonth = MutableStateFlow(LocalDate.now().monthValue)
    val currentMonth: StateFlow<Int> = _currentMonth.asStateFlow()

    private val _currentYear = MutableStateFlow(LocalDate.now().year)
    val currentYear: StateFlow<Int> = _currentYear.asStateFlow()

    private val _visibleMatchdayIndex = MutableStateFlow(0)
    val visibleMatchdayIndex: StateFlow<Int> = _visibleMatchdayIndex.asStateFlow()

    private val _initialVisibleMatchdayIndex = MutableStateFlow(0)
    val initialVisibleMatchdayIndex: StateFlow<Int> = _initialVisibleMatchdayIndex.asStateFlow()

    fun setTeam(team: String) {
        _selectedTeam.value = team
    }

    fun goToPreviousMonth() {
        if (_currentMonth.value == 1) {
            _currentMonth.value = 12
            _currentYear.value -= 1
        } else {
            _currentMonth.value -= 1
        }
    }

    fun goToNextMonth() {
        if (_currentMonth.value == 12) {
            _currentMonth.value = 1
            _currentYear.value += 1
        } else {
            _currentMonth.value += 1
        }
    }

    fun calculateInitialMatchdayIndex(sorted: List<MatchdayEntity>, today: LocalDate) {
        val index = sorted.indexOfFirst {
            runCatching { LocalDate.parse(it.date, dateFormatter) }.getOrNull()?.let { date ->
                date >= today && (date.dayOfWeek.value in 6..7)
            } ?: false
        }
        _initialVisibleMatchdayIndex.value = index.coerceAtLeast(0)
        _visibleMatchdayIndex.value = index.coerceAtLeast(0)
    }

    fun selectEvent(event: EventEntity?, date: LocalDate) {
        _selectedEvent.value = event
        _selectedDate.value = date
    }

    fun clearSelectedEvent() {
        _selectedEvent.value = null
    }

    fun selectMatchday(matchday: MatchdayEntity) {
        _selectedMatchday.value = matchday
    }

    fun clearSelectedMatchday() {
        _selectedMatchday.value = null
    }

    fun prepareAddEvent(date: LocalDate) {
        _selectedDate.value = date
        _showAddEventDialog.value = true
    }

    fun dismissAddEventDialog() {
        _showAddEventDialog.value = false
    }

    fun showAttendanceDialog() {
        _showAttendanceDialog.value = true
    }

    fun dismissAttendanceDialog() {
        _showAttendanceDialog.value = false
    }

    fun showCallUpDialog() {
        _showCallUpDialog.value = true
    }

    fun dismissCallUpDialog() {
        _showCallUpDialog.value = false
    }

    fun handleDateSelection(date: LocalDate, groupedEvents: Map<LocalDate, List<EventEntity>>) {
        val eventsThatDay = groupedEvents[date]
        if (!eventsThatDay.isNullOrEmpty()) {
            _selectedEvent.value = eventsThatDay.first()
        } else {
            _selectedDate.value = date
            _showAddEventDialog.value = true
        }
        _showDateSelector.value = false
    }

    fun dismissDateSelector() {
        _showDateSelector.value = false
    }

    fun addEvent(type: String, time: String, formatter: DateTimeFormatter, eventViewModel: EventViewModel) {
        val dateStr = _selectedDate.value?.format(formatter) ?: return
        val team = _selectedTeam.value ?: return
        eventViewModel.updateDate(dateStr)
        eventViewModel.updateType(type)
        eventViewModel.updateTime(time)
        eventViewModel.insertEvent()
        _showAddEventDialog.value = false
    }

    fun deleteEvent(event: EventEntity, eventViewModel: EventViewModel) {
        eventViewModel.deleteEvent(event)
        _selectedEvent.value = null
    }
}
