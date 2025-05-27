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
    onNavigateToSelectTeam: () -> Unit,
    teamName: String?,
) {
    val calendarViewModel: CalendarViewModel = hiltViewModel()
    val eventViewModel: EventViewModel = hiltViewModel()
    val matchdayViewModel: MatchdayViewModel = hiltViewModel()
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val callUpViewModel: CallUpViewModel = hiltViewModel()

    // Carga de datos si cambia el equipo
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

    // Solo una vez: establecer índice visible inicial cuando haya datos
    val initialIndexComputed = remember { mutableStateOf(false) }

    LaunchedEffect(matchdays) {
        if (!initialIndexComputed.value && matchdays.isNotEmpty()) {
            val sorted = matchdays.sortedBy {
                runCatching { LocalDate.parse(it.date, dateFormatter) }.getOrNull()
            }
            val index = sorted.indexOfFirst {
                runCatching { LocalDate.parse(it.date, dateFormatter) }.getOrNull()?.let { date ->
                    date >= today && (date.dayOfWeek == java.time.DayOfWeek.SATURDAY || date.dayOfWeek == java.time.DayOfWeek.SUNDAY)
                } ?: false
            }
            if (index >= 0) {
                calendarViewModel.setVisibleMatchdayIndex(index)
            }
            initialIndexComputed.value = true
        }
    }

    // Evitar recargas innecesarias
    var lastLoadedTeam by remember { mutableStateOf<String?>(null) }
    val visibleTeam = sortedMatchdays.getOrNull(visibleMatchdayIndex)?.team

    LaunchedEffect(visibleTeam) {
        if (visibleTeam != null && visibleTeam != lastLoadedTeam) {
            lastLoadedTeam = visibleTeam
            playerViewModel.loadPlayersByTeam(visibleTeam)
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
        onNavigateToOthers = onNavigateToOthers,
        onNavigateToSelectTeam = onNavigateToSelectTeam,
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
