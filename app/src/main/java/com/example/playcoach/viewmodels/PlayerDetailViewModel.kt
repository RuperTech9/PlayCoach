package com.example.playcoach.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.database.AppDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlayerDetailState(
    val id: Int = 0,
    val number: Int = 0,
    val name: String = "",
    val team: String = "",
    val isCoach: Boolean = false,
    val totalGoals: Int = 0,
    val totalAssists: Int = 0,
    val totalYellows: Int = 0,
    val totalReds: Int = 0,
    val totalMinutes: Int = 0,
    val matchesPlayed: Int = 0,
    val starts: Int = 0,
    val substitutes: Int = 0,
    val matchdays: List<MatchdayPlayerDetail> = emptyList()
)

data class MatchdayPlayerDetail(
    val matchdayNumber: Int,
    val description: String,
    val homeTeam: String,
    val awayTeam: String,
    val goals: Int,
    val assists: Int,
    val yellowCards: Int,
    val redCards: Int,
    val minutesPlayed: Int,
    val wasStarter: Boolean,
    val result: String
)

class PlayerDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repos = AppDatabase.getRepository(application)

    private val _playerDetail = MutableStateFlow<PlayerDetailState?>(null)
    val playerDetail: StateFlow<PlayerDetailState?> = _playerDetail.asStateFlow()

    fun loadPlayerAndStats(playerId: Int) {
        viewModelScope.launch {
            combine(
                repos.playerRepository.getPlayerByJerseyNumber(playerId),
                repos.playerStatsRepository.getStatsByPlayer(playerId),
                repos.matchdayRepository.getAllMatchdays()
            ) { maybePlayer, statList, matchdayList ->
                if (maybePlayer == null) {
                    null
                } else {
                    val totalGoals = statList.sumOf { it.goals }
                    val totalAssists = statList.sumOf { it.assists }
                    val totalYellows = statList.sumOf { it.yellowCards }
                    val totalReds = statList.sumOf { it.redCards }
                    val totalMinutes = statList.sumOf { it.minutesPlayed }
                    val matchesPlayed = statList.map { it.matchdayId }.distinct().count()
                    val starts = statList.count { it.wasStarter }
                    val substitutes = statList.count { !it.wasStarter }
                    val matchdayDetails = statList.mapNotNull { stat ->
                        val md = matchdayList.find { it.id == stat.matchdayId }
                        md?.let {
                            MatchdayPlayerDetail(
                                matchdayNumber = it.matchdayNumber,
                                description = it.time,
                                homeTeam = it.homeTeam,
                                awayTeam = it.awayTeam,
                                goals = stat.goals,
                                assists = stat.assists,
                                yellowCards = stat.yellowCards,
                                redCards = stat.redCards,
                                minutesPlayed = stat.minutesPlayed,
                                wasStarter = stat.wasStarter,
                                result = "(${it.homeGoals}-${it.awayGoals})"
                            )
                        }
                    }

                    PlayerDetailState(
                        id = maybePlayer.id,
                        number = maybePlayer.number,
                        name = maybePlayer.firstName,
                        team = maybePlayer.team,
                        totalGoals = totalGoals,
                        totalAssists = totalAssists,
                        totalYellows = totalYellows,
                        totalReds = totalReds,
                        totalMinutes = totalMinutes,
                        matchesPlayed = matchesPlayed,
                        starts = starts,
                        substitutes = substitutes,
                        matchdays = matchdayDetails
                    )
                }
            }.collectLatest {
                _playerDetail.value = it
            }
        }
    }
}
