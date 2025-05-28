package com.example.playcoach.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.repositories.MatchdayRepository
import com.example.playcoach.data.repositories.PlayerRepository
import com.example.playcoach.data.repositories.PlayerStatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlayerDetailState(
    val id: Int = 0,
    val number: Int = 0,
    val name: String = "",
    val lastname: String = "",
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
    val totalPlayedMatchdays: Int = 0,
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

@HiltViewModel
class PlayerDetailViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val playerStatsRepository: PlayerStatRepository,
    private val matchdayRepository: MatchdayRepository
) : ViewModel() {

    private val _playerDetail = MutableStateFlow<PlayerDetailState?>(null)
    val playerDetail: StateFlow<PlayerDetailState?> = _playerDetail.asStateFlow()

    val totalPlayedMatchdays = matchdayList.count { it.played }

    fun loadPlayerAndStats(playerId: Int) {
        viewModelScope.launch {
            combine(
                playerRepository.getPlayerByJerseyNumber(playerId),
                playerStatsRepository.getStatsByPlayer(playerId),
                matchdayRepository.getAllMatchdays()
            ) { maybePlayer, statList, matchdayList ->
                maybePlayer?.let { player ->

                    val matchdayMap = matchdayList.associateBy { it.id }

                    // Only stats from played matchdays
                    val filteredStats = statList.filter { stat ->
                        matchdayMap[stat.matchdayId]?.played == true
                    }

                    val matchdayDetails = statList.mapNotNull { stat ->
                        matchdayMap[stat.matchdayId]?.let { md ->
                            MatchdayPlayerDetail(
                                matchdayNumber = md.matchdayNumber,
                                description = md.time,
                                homeTeam = md.homeTeam,
                                awayTeam = md.awayTeam,
                                goals = stat.goals,
                                assists = stat.assists,
                                yellowCards = stat.yellowCards,
                                redCards = stat.redCards,
                                minutesPlayed = stat.minutesPlayed,
                                wasStarter = stat.wasStarter,
                                result = "(${md.homeGoals}-${md.awayGoals})"
                            )
                        }
                    }

                    PlayerDetailState(
                        id = player.id,
                        number = player.number,
                        name = player.firstName,
                        lastname = player.lastName,
                        team = player.team,
                        totalGoals = statList.sumOf { it.goals },
                        totalAssists = statList.sumOf { it.assists },
                        totalYellows = statList.sumOf { it.yellowCards },
                        totalReds = statList.sumOf { it.redCards },
                        totalMinutes = statList.sumOf { it.minutesPlayed },
                        matchesPlayed = statList.map { it.matchdayId }.distinct().count(),
                        starts = statList.count { it.wasStarter },
                        substitutes = statList.count { !it.wasStarter },
                        matchdays = matchdayDetails,
                        totalPlayedMatchdays = totalPlayedMatchdays
                    )
                }
            }
                .distinctUntilChanged()
                .collectLatest {
                    _playerDetail.value = it
                }
        }
    }
}
