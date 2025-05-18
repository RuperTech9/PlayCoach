package com.example.playcoach.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.TeamsData
import com.example.playcoach.data.database.AppDatabase
import com.example.playcoach.data.entities.MatchdayEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MatchdayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppDatabase.getRepository(application).matchdayRepository

    private val _selectedTeam = MutableStateFlow("Infantil A")

    fun updateSelectedTeam(name: String) {
        _selectedTeam.value = name

        viewModelScope.launch(Dispatchers.IO) {
            val predefined = TeamsData.getMatchesForTeam(name)
            repository.insertMatchdaysIfNotExist(name, predefined)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val matchdays: StateFlow<List<MatchdayEntity>> = _selectedTeam
        .flatMapLatest { team -> repository.getMatchdaysByTeam(team) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _matchdayNumber = MutableStateFlow(1)
    val matchdayNumber: StateFlow<Int> = _matchdayNumber.asStateFlow()

    private val _time = MutableStateFlow("")
    val time: StateFlow<String> = _time.asStateFlow()

    private val _date = MutableStateFlow("")
    val date: StateFlow<String> = _date.asStateFlow()

    private val _homeTeam = MutableStateFlow("")
    val homeTeam: StateFlow<String> = _homeTeam.asStateFlow()

    private val _awayTeam = MutableStateFlow("")
    val awayTeam: StateFlow<String> = _awayTeam.asStateFlow()

    private val _homeGoals = MutableStateFlow("")
    val homeGoals: StateFlow<String> = _homeGoals.asStateFlow()

    private val _awayGoals = MutableStateFlow("")
    val awayGoals: StateFlow<String> = _awayGoals.asStateFlow()

    private val _played = MutableStateFlow(false)
    val played: StateFlow<Boolean> = _played.asStateFlow()
    fun updatePlayed(value: Boolean) { _played.value = value }

    fun updateMatchdayNumber(value: Int) { _matchdayNumber.value = value }
    fun updateDescription(value: String) { _time.value = value }
    fun updateDate(value: String) { _date.value = value }
    fun updateHomeTeam(value: String) { _homeTeam.value = value }
    fun updateAwayTeam(value: String) { _awayTeam.value = value }
    fun updateHomeGoals(value: String) { _homeGoals.value = value }
    fun updateAwayGoals(value: String) { _awayGoals.value = value }

    fun insertMatchday() {
        val matchday = MatchdayEntity(
            matchdayNumber = _matchdayNumber.value,
            time = _time.value,
            date = _date.value,
            homeTeam = _homeTeam.value,
            awayTeam = _awayTeam.value,
            homeGoals = _homeGoals.value.toIntOrNull() ?: 0,
            awayGoals = _awayGoals.value.toIntOrNull() ?: 0,
            team = _selectedTeam.value,
            played = _played.value
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMatchday(matchday)
            withContext(Dispatchers.Main) {
                clearForm()
            }
        }
    }

    suspend fun insertAndReturnId(): Int {
        val matchday = MatchdayEntity(
            matchdayNumber = _matchdayNumber.value,
            time = _time.value,
            date = _date.value,
            homeTeam = _homeTeam.value,
            awayTeam = _awayTeam.value,
            homeGoals = _homeGoals.value.toIntOrNull() ?: 0,
            awayGoals = _awayGoals.value.toIntOrNull() ?: 0,
            team = _selectedTeam.value,
            played = _played.value
        )
        val rowId = repository.insertMatchday(matchday)
        withContext(Dispatchers.Main) {
            clearForm()
        }
        return rowId.toInt()
    }

    private fun clearForm() {
        _matchdayNumber.value = 1
        _time.value = ""
        _date.value = ""
        _homeTeam.value = ""
        _awayTeam.value = ""
        _homeGoals.value = ""
        _awayGoals.value = ""
        _played.value = false
    }

    fun createMatchdayFromEvent(
        matchdayNumber: Int,
        description: String,
        date: String,
        homeTeam: String = "Pending",
        awayTeam: String = "Pending",
        homeGoals: Int = 0,
        awayGoals: Int = 0
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newMatchday = MatchdayEntity(
                id = 0,
                matchdayNumber = matchdayNumber,
                time = description,
                date = date,
                homeTeam = homeTeam,
                awayTeam = awayTeam,
                homeGoals = homeGoals,
                awayGoals = awayGoals,
                summary = "",
                team = _selectedTeam.value
            )
            repository.insertMatchday(newMatchday)
        }
    }

    fun getMatchdayById(id: Int) = repository.getMatchdayById(id)

    fun updateMatchday(matchday: MatchdayEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMatchday(matchday)
        }
    }
}
