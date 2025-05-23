package com.example.playcoach.ui.screens

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.ui.components.BaseScreen
import com.example.playcoach.ui.components.calendar.*
import com.example.playcoach.viewmodels.CalendarViewModel
import com.example.playcoach.viewmodels.CallUpViewModel
import com.example.playcoach.viewmodels.EventViewModel
import com.example.playcoach.viewmodels.MatchdayViewModel
import com.example.playcoach.viewmodels.PlayerViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun Calendar(
    onNavigateBack: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToSquad: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToFormations: () -> Unit,
    onNavigateToOthers: () -> Unit,
    teamName: String?,
) {
    val calendarViewModel: CalendarViewModel = hiltViewModel()
    val eventViewModel: EventViewModel = hiltViewModel()
    val matchdayViewModel: MatchdayViewModel = hiltViewModel()
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val callUpViewModel: CallUpViewModel = hiltViewModel()

    LaunchedEffect(teamName) {
        teamName?.let { team ->
            matchdayViewModel.updateSelectedTeam(team)
            eventViewModel.updateSelectedTeam(team)
        }
    }

    val matchdays by matchdayViewModel.matchdays.collectAsState()
    val visibleMatchdayIndex by calendarViewModel.visibleMatchdayIndex.collectAsState()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val today = LocalDate.now()

    val sortedMatchdays by remember(matchdays) {
        derivedStateOf {
            matchdays.sortedBy {
                runCatching { LocalDate.parse(it.date, dateFormatter) }.getOrNull()
            }
        }
    }

    LaunchedEffect(sortedMatchdays) {
        val index = sortedMatchdays.indexOfFirst {
            runCatching { LocalDate.parse(it.date, dateFormatter) }.getOrNull()?.let { date ->
                date >= today && (date.dayOfWeek == java.time.DayOfWeek.SATURDAY || date.dayOfWeek == java.time.DayOfWeek.SUNDAY)
            } ?: false
        }
        if (index >= 0) {
            calendarViewModel.setVisibleMatchdayIndex(index)
        }
    }

    LaunchedEffect(sortedMatchdays, visibleMatchdayIndex) {
        val matchday = sortedMatchdays.getOrNull(visibleMatchdayIndex)
        matchday?.team?.let {
            playerViewModel.loadPlayersByTeam(it)
        }
    }

    BaseScreen(
        title = "Calendario",
        teamName = teamName,
        onNavigateBack = onNavigateBack,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToCalendar = onNavigateToCalendar,
        onNavigateToMessages = onNavigateToMessages,
        onNavigateToSquad = onNavigateToSquad,
        onNavigateToStats = onNavigateToStats,
        onNavigateToFormations = onNavigateToFormations,
        onNavigateToOthers = onNavigateToOthers
    ) {
        CalendarScreenContent(
            calendarViewModel = calendarViewModel,
            eventViewModel = eventViewModel,
            matchdayViewModel = matchdayViewModel,
            playerViewModel = playerViewModel,
            callUpViewModel = callUpViewModel,
            teamName = teamName ?: ""
        )
    }
}