package com.example.playcoach.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoach.data.repositories.MatchdayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

data class TeamStatsData(
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val matchdays: List<MatchdayDetailTeam>
)

data class MatchdayDetailTeam(
    val id: Int,
    val matchdayNumber: Int,
    val description: String,
    val homeGoals: Int,
    val awayGoals: Int,
    val homeTeam: String,
    val awayTeam: String
)

@HiltViewModel
class TeamStatsViewModel @Inject constructor(
    private val matchdayRepository: MatchdayRepository
) : ViewModel() {

    private val _selectedTeam = MutableStateFlow<String?>(null)

    fun updateSelectedTeam(name: String) {
        _selectedTeam.value = name
    }

    private val _refresh = MutableStateFlow(false)
    fun forceRefresh() {
        _refresh.value = !_refresh.value
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val teamStats: StateFlow<TeamStatsData?> =
        combine(_selectedTeam.filterNotNull(), _refresh) { team, _ -> team }
            .flatMapLatest { team ->
                matchdayRepository.getMatchdaysByTeam(team).map { teamMatchdays ->

                    var wins = 0
                    var draws = 0
                    var losses = 0
                    var goalsFor = 0
                    var goalsAgainst = 0

                    val details = teamMatchdays
                        .filter { it.played }
                        .map { m ->
                            val isHome = m.homeTeam == team
                            val gf = if (isHome) m.homeGoals else m.awayGoals
                            val ga = if (isHome) m.awayGoals else m.homeGoals

                            when {
                                gf > ga -> wins++
                                gf == ga -> draws++
                                else -> losses++
                            }

                            goalsFor += gf
                            goalsAgainst += ga

                            MatchdayDetailTeam(
                                id = m.id,
                                matchdayNumber = m.matchdayNumber,
                                description = m.time,
                                homeGoals = m.homeGoals,
                                awayGoals = m.awayGoals,
                                homeTeam = m.homeTeam,
                                awayTeam = m.awayTeam
                            )
                        }

                    TeamStatsData(
                        wins = wins,
                        draws = draws,
                        losses = losses,
                        goalsFor = goalsFor,
                        goalsAgainst = goalsAgainst,
                        matchdays = details
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
