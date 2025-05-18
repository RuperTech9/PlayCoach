package com.example.playcoach.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.ui.components.BaseScreen
import com.example.playcoach.viewmodels.AbsenceViewModel
import com.example.playcoach.viewmodels.EventViewModel
import com.example.playcoach.viewmodels.PlayerViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PlayersAbsence(
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

    val playerViewModel: PlayerViewModel = hiltViewModel()
    val eventViewModel: EventViewModel = hiltViewModel()
    val absenceViewModel: AbsenceViewModel = hiltViewModel()

    LaunchedEffect(teamName) {
        if (!teamName.isNullOrBlank()) {
            playerViewModel.loadPlayersByTeam(teamName)
            eventViewModel.updateSelectedTeam(teamName)
        }
    }

    val playerList by playerViewModel.players.collectAsState()
    val eventList by eventViewModel.events.collectAsState()
    val attendanceByDate by absenceViewModel.attendanceByDate.collectAsState()

    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val groupedEvents = remember(eventList) {
        eventList.groupBy { LocalDate.parse(it.date, dateFormatter) }
    }

    var selectedEvent by remember { mutableStateOf<LocalDate?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedFormattedDate by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedEvent) {
        selectedEvent?.let { date ->
            val formatted = date.format(dateFormatter)
            selectedFormattedDate = formatted
            showDialog = false
            absenceViewModel.selectDate(formatted)
        }
    }

    LaunchedEffect(attendanceByDate, selectedFormattedDate) {
        if (selectedFormattedDate != null &&
            attendanceByDate.all { it.date == selectedFormattedDate }
        ) {
            showDialog = true
        }
    }

    BaseScreen(
        title = "Asistencia",
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
    ) { contentModifier ->
        Column(
            modifier = contentModifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🗓 Eventos del equipo",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00205B)
            )

            val sortedDates = groupedEvents.keys.sorted()
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sortedDates) { date ->
                    val eventsForDate = groupedEvents[date].orEmpty()
                    val backgroundColor = if (eventsForDate.any { it.type == "Partido" }) {
                        Color(0xFFFFF59D)
                    } else {
                        Color.White
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedEvent = date },
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        border = BorderStroke(1.dp, Color(0xFF00205B)),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val displayDate = "${date.dayOfMonth}/${date.monthValue}/${date.year}"
                            Text(
                                text = "${eventsForDate.joinToString { it.type }} - $displayDate",
                                fontSize = 16.sp,
                                color = Color(0xFF00205B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showDialog && selectedEvent != null && selectedFormattedDate != null) {
                val date = selectedEvent!!
                val formattedDate = selectedFormattedDate!!

                val absences = attendanceByDate.filter { it.date == formattedDate }
                val absentIds = absences.filter { !it.present }.map { it.playerId }.toSet()
                val absentPlayers = playerList
                    .filter { it.id in absentIds }
                    .sortedBy { it.number }

                AlertDialog(
                    onDismissRequest = {
                        selectedEvent = null
                        showDialog = false
                        selectedFormattedDate = null
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedEvent = null
                            showDialog = false
                            selectedFormattedDate = null
                        }) {
                            Text("Cerrar", color = Color(0xFF00205B))
                        }
                    },
                    title = {
                        Text(
                            text = "Evento del ${date.dayOfMonth}/${date.monthValue}/${date.year}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00205B)
                        )
                    },
                    text = {
                        if (absentPlayers.isEmpty()) {
                            Text(
                                text = "✅ Todos asistieron a este evento.",
                                fontSize = 16.sp,
                                color = Color(0xFF2E7D32)
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "🔴 ${absentPlayers.size} " +
                                            "jugador${if (absentPlayers.size == 1) "" else "es"} " +
                                            "falt${if (absentPlayers.size == 1) "ó" else "aron"}",
                                    fontSize = 16.sp,
                                    color = Color.Red,
                                    fontWeight = FontWeight.SemiBold
                                )
                                absentPlayers.forEach { player ->
                                    Text(
                                        text = "• ${player.number} - ${player.nickname}",
                                        fontSize = 15.sp,
                                        color = Color(0xFF00205B)
                                    )
                                }
                            }
                        }
                    },
                    containerColor = Color.White
                )
            }
        }
    }
}
