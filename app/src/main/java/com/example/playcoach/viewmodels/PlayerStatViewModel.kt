package com.example.playcoach.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.entities.PlayerStatEntity
import com.example.playcoach.data.repositories.MatchdayRepository
import com.example.playcoach.data.repositories.PlayerRepository
import com.example.playcoach.data.repositories.PlayerStatRepository
import com.example.playcoach.ui.screens.MatchdayDetail
import com.example.playcoach.ui.screens.PlayerStats
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class PlayerStatViewModel @Inject constructor(
    private val playerRepo: PlayerRepository,
    private val statsRepo: PlayerStatRepository,
    private val matchdayRepo: MatchdayRepository
) : ViewModel() {

    private val _playersStats = MutableStateFlow<List<PlayerStats>>(emptyList())
    val playersStats: StateFlow<List<PlayerStats>> = _playersStats.asStateFlow()

    fun loadStatsForTeam(team: String) {
        viewModelScope.launch {
            combine(
                playerRepo.getPlayersByTeam(team),
                statsRepo.getAllStats(),
                matchdayRepo.getAllMatchdays()
            ) { playerList, statList, matchdayList ->

                playerList.map { player ->
                    val playerStats = statList.filter { it.playerId == player.number }

                    // Only stats from played matchdays
                    val filteredStats = playerStats.filter { stat ->
                        matchdayList.find { it.id == stat.matchdayId }?.played == true
                    }

                    val totalGoals = filteredStats.sumOf { it.goals }
                    val totalAssists = filteredStats.sumOf { it.assists }
                    val totalMinutes = filteredStats.sumOf { it.minutesPlayed }
                    val totalYellows = filteredStats.sumOf { it.yellowCards }
                    val totalReds = filteredStats.sumOf { it.redCards }
                    val matchesPlayed = filteredStats.size
                    val starts = filteredStats.count { it.wasStarter }
                    val substitutes = filteredStats.count { !it.wasStarter }

                    val matchdayDetails = playerStats.map { stat ->
                        val matchday = matchdayList.find { it.id == stat.matchdayId }
                        val description = matchday?.let {
                            "${it.time} (${it.homeGoals}-${it.awayGoals})"
                        } ?: "Matchday ${stat.matchdayId}"

                        MatchdayDetail(
                            description = description,
                            minutesPlayed = stat.minutesPlayed,
                            yellowCards = stat.yellowCards,
                            redCards = stat.redCards,
                            goals = stat.goals,
                            assists = stat.assists,
                            wasStarter = stat.wasStarter
                        )
                    }

                    PlayerStats(
                        number = player.number,
                        name = player.nickname,
                        goals = totalGoals,
                        assists = totalAssists,
                        minutesPlayed = totalMinutes,
                        yellowCards = totalYellows,
                        redCards = totalReds,
                        matchdays = matchdayDetails,
                        matchesPlayed = matchesPlayed,
                        starts = starts,
                        substitutes = substitutes
                    )
                }
            }.collect {
                _playersStats.value = it
            }
        }
    }

    fun getPlayerStat(playerId: Int, matchdayId: Int): Flow<PlayerStatEntity?> {
        return statsRepo.getStatByPlayerAndMatchday(playerId, matchdayId)
    }

    fun insertPlayerStat(
        playerId: Int,
        matchdayId: Int,
        goals: Int,
        assists: Int,
        yellowCards: Int,
        redCards: Int,
        minutesPlayed: Int,
        wasStarter: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newStat = PlayerStatEntity(
                playerId = playerId,
                matchdayId = matchdayId,
                minutesPlayed = minutesPlayed,
                goals = goals,
                assists = assists,
                yellowCards = yellowCards,
                redCards = redCards,
                wasStarter = wasStarter
            )
            statsRepo.insertStat(newStat)
        }
    }

    private val _allStats = MutableStateFlow<List<PlayerStatEntity>>(emptyList())
    val allStats: StateFlow<List<PlayerStatEntity>> = _allStats.asStateFlow()

    init {
        viewModelScope.launch {
            statsRepo.getAllStats().collectLatest {
                _allStats.value = it
            }
        }
    }
}
