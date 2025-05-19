package com.example.playcoach.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playcoach.data.entities.EventEntity
import com.example.playcoach.data.entities.MatchdayEntity
import com.example.playcoach.data.entities.PlayerEntity
import com.example.playcoach.ui.components.BaseScreen
import com.example.playcoach.utils.generateCallUpPdf
import com.example.playcoach.viewmodels.AbsenceViewModel
import com.example.playcoach.viewmodels.CallUpViewModel
import com.example.playcoach.viewmodels.EventViewModel
import com.example.playcoach.viewmodels.MatchdayViewModel
import com.example.playcoach.viewmodels.PlayerViewModel
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

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

    val events by eventViewModel.events.collectAsState()
    val matchdays by matchdayViewModel.matchdays.collectAsState()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    val groupedEvents = remember(events) {
        events.groupBy { LocalDate.parse(it.date, dateFormatter) }
    }
    val groupedMatchdays = remember(matchdays) {
        matchdays.groupBy { LocalDate.parse(it.date, dateFormatter) }
    }

    var currentMonth by remember { mutableIntStateOf(LocalDate.now().monthValue) }
    var currentYear by remember { mutableIntStateOf(LocalDate.now().year) }
    val today = LocalDate.now()
    var showAddEventDialog by remember { mutableStateOf(false) }
    var showDateSelector by remember { mutableStateOf(false) }
    var showAttendanceDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEvent by remember { mutableStateOf<EventEntity?>(null) }
    var selectedMatchday by remember { mutableStateOf<MatchdayEntity?>(null) }
    var showCallUpDialog by remember { mutableStateOf(false) }
    val sortedMatchdays = remember(matchdays) {
        matchdays.sortedBy {
            runCatching { LocalDate.parse(it.date, dateFormatter) }.getOrNull()
        }
    }
    val initialVisibleMatchdayIndex = remember(sortedMatchdays) {
        sortedMatchdays.indexOfFirst {
            runCatching { LocalDate.parse(it.date, dateFormatter) }.getOrNull()?.let { date ->
                date >= today && (date.dayOfWeek == java.time.DayOfWeek.SATURDAY || date.dayOfWeek == java.time.DayOfWeek.SUNDAY)
            } ?: false
        }.takeIf { it >= 0 } ?: 0
    }

    var visibleMatchdayIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(sortedMatchdays) {
        val index = sortedMatchdays.indexOfFirst {
            runCatching { LocalDate.parse(it.date, dateFormatter) }.getOrNull()?.let { date ->
                date >= today && (date.dayOfWeek == java.time.DayOfWeek.SATURDAY || date.dayOfWeek == java.time.DayOfWeek.SUNDAY)
            } ?: false
        }
        if (index >= 0) {
            visibleMatchdayIndex = index
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
    ) { contentModifier ->
        Box(
            modifier = contentModifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MonthNavigationBar(
                    currentMonth = currentMonth,
                    currentYear = currentYear,
                    onPreviousMonth = {
                        if (currentMonth == 1) {
                            currentMonth = 12; currentYear--
                        } else currentMonth--
                    },
                    onNextMonth = {
                        if (currentMonth == 12) {
                            currentMonth = 1; currentYear++
                        } else currentMonth++
                    }
                )

                val daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth()
                val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1).dayOfWeek.value
                val days = (1..daysInMonth).map { day -> LocalDate.of(currentYear, currentMonth, day) }

                LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
                    items(listOf("L", "M", "X", "J", "V", "S", "D")) { letter ->
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF00205B)
                            )
                        }
                    }

                    items(firstDayOfMonth - 1) {
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    items(days) { date ->
                        CalendarDay(
                            date = date,
                            today = today,
                            groupedEvents = groupedEvents,
                            groupedMatchdays = groupedMatchdays,
                            onEventClick = {
                                selectedEvent = it
                                selectedDate = date
                            },
                            onMatchdayClick = {
                                selectedMatchday = it
                            },
                            onEmptyDayClick = {
                                selectedDate = date
                                showAddEventDialog = true
                            }
                        )
                    }
                }
            }

            // Ensure players are loaded when the visible matchday changes
            LaunchedEffect(sortedMatchdays, visibleMatchdayIndex) {
                val matchday = sortedMatchdays.getOrNull(visibleMatchdayIndex)
                matchday?.team?.let {
                    playerViewModel.loadPlayersByTeam(it)
                }
            }

            if (sortedMatchdays.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { if (visibleMatchdayIndex > 0) visibleMatchdayIndex-- },
                            enabled = visibleMatchdayIndex > 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous"
                            )
                        }

                        val matchday = sortedMatchdays[visibleMatchdayIndex]
                        val calledUp by produceState(initialValue = emptyList<Int>(), matchday.id) {
                            callUpViewModel.getCalledUpPlayers(matchday.id).collect {
                                value = it
                            }
                        }

                        val players by playerViewModel.players.collectAsState()
                        val totalPlayers = players.size
                        val hasCallUp = calledUp.isNotEmpty()

                        val callUpText = when {
                            hasCallUp && totalPlayers > 0 -> "✅ Convocatoria hecha (${calledUp.size}/$totalPlayers)"
                            !hasCallUp && totalPlayers > 0 -> "❌ Sin convocatoria"
                            else -> "⏳ Cargando..."
                        }

                        Card(
                            modifier = Modifier
                                .weight(6f)
                                .padding(horizontal = 4.dp)
                                .heightIn(max = 250.dp)
                                .clickable { selectedMatchday = matchday },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                            border = BorderStroke(1.dp, Color(0xFF00205B)),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            val isNext = visibleMatchdayIndex == initialVisibleMatchdayIndex
                            val isPlayed = matchday.played

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Matchday number
                                Text(
                                    text = "Jornada ${matchday.matchdayNumber}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (isNext) Color(0xFF00205B) else Color(0xFF6C7A89)
                                )

                                // Date and time
                                Text(
                                    text = "📅 ${matchday.date} — ${matchday.time}",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )

                                // Teams
                                Text(
                                    text = matchday.homeTeam,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = Color(0xFF00205B)
                                )
                                Text(
                                    text = "vs",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = Color(0xFF00205B)
                                )
                                Text(
                                    text = matchday.awayTeam,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = Color(0xFF00205B)
                                )

                                // Score if played
                                if (isPlayed && (matchday.homeGoals >= 0 || matchday.awayGoals >= 0)) {
                                    Text(
                                        text = "${matchday.homeGoals} - ${matchday.awayGoals}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF388E3C)
                                    )
                                }

                                // Call-up status
                                Text(
                                    text = callUpText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (hasCallUp) Color(0xFF4CAF50) else Color(0xFFF44336)
                                )
                            }
                        }

                        IconButton(
                            onClick = { if (visibleMatchdayIndex < sortedMatchdays.lastIndex) visibleMatchdayIndex++ },
                            enabled = visibleMatchdayIndex < sortedMatchdays.lastIndex,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Siguiente"
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDateSelector) {
        DateSelectorDialog(
            currentDate = today,
            onDateSelected = { date ->
                val eventsThatDay = groupedEvents[date]
                if (!eventsThatDay.isNullOrEmpty()) {
                    selectedEvent = eventsThatDay.first()
                } else {
                    selectedDate = date
                    showAddEventDialog = true
                }
                showDateSelector = false
            },
            onDismiss = { showDateSelector = false }
        )
    }

    if (showAddEventDialog) {
        AddEventDialog(
            date = selectedDate,
            onAddEvent = { type, time ->
                selectedDate?.let { date ->
                    val strDate = date.format(dateFormatter)
                    eventViewModel.updateDate(strDate)
                    eventViewModel.updateType(type)
                    eventViewModel.updateTime(time)
                    eventViewModel.insertEvent()
                }
                showAddEventDialog = false
            },
            onDismiss = { showAddEventDialog = false }
        )
    }

    if (selectedEvent != null) {
        Dialog(onDismissRequest = { selectedEvent = null }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = Color.White,
                tonalElevation = 4.dp,
                border = BorderStroke(1.dp, Color(0xFF00205B)),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Detalles del Evento",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF00205B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00205B)
                            )
                        ) {
                            append("📝 Tipo: ")
                        }
                        append(selectedEvent?.type ?: "")
                    }, fontSize = 16.sp)

                    Text(buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00205B)
                            )
                        ) {
                            append("⏰ Hora: ")
                        }
                        append(selectedEvent?.time ?: "")
                    }, fontSize = 16.sp)

                    Text(buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00205B)
                            )
                        ) {
                            append("📆 Fecha: ")
                        }
                        append(selectedEvent?.date ?: "")
                    }, fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            showAttendanceDialog = true
                            selectedEvent = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                    ) {
                        Text("Marcar Asistencia", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            selectedEvent?.let { event ->
                                eventViewModel.deleteEvent(event)
                            }
                            selectedEvent = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Eliminar Evento")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { selectedEvent = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }

    if (showAttendanceDialog && selectedDate != null) {
        TrainingAttendanceDialog(
            date = selectedDate!!.format(dateFormatter),
            team = teamName ?: "",
            playerViewModel = viewModel(),
            absenceViewModel = viewModel(),
            onDismiss = { showAttendanceDialog = false }
        )
    }

    if (selectedMatchday != null) {
        MatchdayDetailsDialog(
            matchday = selectedMatchday!!,
            onOpenCallUp = {
                playerViewModel.loadPlayersByTeam(teamName ?: "")
                showCallUpDialog = true
            },
            onDismiss = { selectedMatchday = null }
        )
    }

    if (showCallUpDialog && selectedMatchday != null) {
        MatchdayCallUpDialog(
            matchday = selectedMatchday!!,
            players = playerViewModel.players.value,
            callUpViewModel = viewModel(),
            onDismiss = { showCallUpDialog = false }
        )
    }
}

@Composable
fun AddEventDialog(
    date: LocalDate?,
    onAddEvent: (eventType: String, eventTime: String) -> Unit,
    onDismiss: () -> Unit
) {
    var eventType by remember { mutableStateOf("Entrenamiento") }
    var eventTime by remember { mutableStateOf("18:45") }
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            tonalElevation = 4.dp,
            border = BorderStroke(1.dp, Color(0xFF00205B)),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "➕ Agregar Evento",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF00205B)
                )

                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00205B)
                            )
                        ) {
                            append("📆 Fecha: ")
                        }
                        append("${date?.dayOfMonth}/${date?.monthValue}/${date?.year}")
                    },
                    fontSize = 16.sp
                )

                // Event type selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Color(0xFF00205B))
                    ) {
                        Text(eventType)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Entrenamiento") },
                            onClick = {
                                eventType = "Entrenamiento"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Amistoso") },
                            onClick = {
                                eventType = "Amistoso"
                                expanded = false
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = eventTime,
                    onValueChange = { eventTime = it },
                    label = { Text("Hora (HH:MM)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onAddEvent(eventType, eventTime)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                    ) {
                        Text("Agregar", color = Color.White)
                    }
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

@Composable
fun TrainingAttendanceDialog(
    date: String,
    team: String,
    playerViewModel: PlayerViewModel,
    absenceViewModel: AbsenceViewModel,
    onDismiss: () -> Unit
) {
    val playerList by playerViewModel.players.collectAsState()
    val attendanceByDate by absenceViewModel.attendanceByDate.collectAsState()

    LaunchedEffect(team) {
        playerViewModel.loadPlayersByTeam(team)
        absenceViewModel.selectDate(date)
    }

    val attendanceMap = attendanceByDate.associateBy { it.playerId }
    val selectedMap = remember(date) { mutableStateMapOf<Int, Boolean>() }

    LaunchedEffect(date, playerList, attendanceMap) {
        selectedMap.clear()
        playerList.forEach { player ->
            val attendance = attendanceMap[player.id]
            selectedMap[player.id] = attendance?.present ?: true
        }
    }

    val total = playerList.size
    val present = selectedMap.values.count { it }
    val absent = total - present

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            tonalElevation = 4.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Asistencia del $date",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF00205B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "🟢 Asisten: $present   🔴 Faltan: $absent",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                val sortedPlayers = playerList.sortedBy { it.number }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(top = 8.dp)
                ) {
                    items(sortedPlayers.size) { index ->
                        val player = sortedPlayers[index]
                        val isPresent = selectedMap[player.id] == true

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Checkbox(
                                checked = isPresent,
                                onCheckedChange = { selected ->
                                    selectedMap[player.id] = selected
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF4CAF50),
                                    uncheckedColor = Color(0xFFF44336)
                                )
                            )
                            Text(
                                text = "${player.number} - ${player.firstName}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val presentIds = selectedMap.filterValues { it }.keys.toList()
                        val allPlayerIds = playerList.map { it.id }

                        absenceViewModel.saveFullAttendance(
                            date = date,
                            allPlayers = allPlayerIds,
                            presentPlayers = presentIds
                        )
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                ) {
                    Text("Guardar", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun MatchdayDetailsDialog(
    matchday: MatchdayEntity,
    onOpenCallUp: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            tonalElevation = 4.dp,
            border = BorderStroke(1.dp, Color(0xFF00205B)),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "⚽ Detalles del Partido",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF00205B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF00205B))) {
                            append("🗓 Jornada: ")
                        }
                        append("${matchday.matchdayNumber}")
                    },
                    fontSize = 16.sp
                )

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF00205B))) {
                            append("📆 Fecha: ")
                        }
                        append(matchday.date)
                    },
                    fontSize = 16.sp
                )

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF00205B))) {
                            append("📝 Hora: ")
                        }
                        append(matchday.time)
                    },
                    fontSize = 16.sp
                )

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF00205B))) {
                            append("🏠 Local: ")
                        }
                        append(matchday.homeTeam)
                    },
                    fontSize = 16.sp
                )

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF00205B))) {
                            append("🚩 Visitante: ")
                        }
                        append(matchday.awayTeam)
                    },
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onOpenCallUp,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                ) {
                    Text("Gestionar Convocatoria", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun MatchdayCallUpDialog(
    matchday: MatchdayEntity,
    players: List<PlayerEntity>,
    callUpViewModel: CallUpViewModel,
    onDismiss: () -> Unit
) {
    val callUpState = remember { mutableStateMapOf<Int, Boolean>() }
    val previousCallUps by callUpViewModel
        .getCalledUpPlayers(matchday.id)
        .collectAsState(initial = emptyList())

    LaunchedEffect(matchday.id, players, previousCallUps) {
        if (players.isNotEmpty()) {
            callUpState.clear()
            players.forEach { player ->
                callUpState[player.number] = previousCallUps.contains(player.number)
            }
        }
    }

    val total = players.size
    val selectedCount = callUpState.values.count { it }
    val notSelected = total - selectedCount

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            tonalElevation = 4.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Convocatoria Jornada ${matchday.matchdayNumber}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF00205B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "🟢 Convocados: $selectedCount   🔴 No convocados: $notSelected",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                val sortedPlayers = players.sortedBy { it.number }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(top = 8.dp)
                ) {
                    items(sortedPlayers.size) { index ->
                        val player = sortedPlayers[index]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Checkbox(
                                checked = callUpState[player.number] == true,
                                onCheckedChange = { selected ->
                                    callUpState[player.number] = selected
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF4CAF50),
                                    uncheckedColor = Color(0xFFF44336)
                                )
                            )
                            Text(
                                text = "${player.number} - ${player.firstName}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val selectedPlayers = callUpState.filterValues { it }.keys.toList()
                        callUpViewModel.saveCallUps(matchday.id, selectedPlayers)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                ) {
                    Text("Guardar Convocatoria", color = Color.White)
                }

                val context = LocalContext.current

                OutlinedButton(
                    onClick = {
                        val calledUpPlayers = players.filter { player ->
                            callUpState[player.number] == true
                        }

                        val pdf = generateCallUpPdf(context, matchday, calledUpPlayers)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            pdf
                        )

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_TEXT, "📄 Convocados Jornada ${matchday.matchdayNumber}")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        context.startActivity(Intent.createChooser(intent, "Compartir PDF"))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color(0xFF00205B))
                ) {
                    Text("📥 Descargar PDF", color = Color(0xFF00205B))
                }
            }
        }
    }
}

@Composable
fun CalendarDay(
    date: LocalDate,
    today: LocalDate,
    groupedEvents: Map<LocalDate, List<EventEntity>>,
    groupedMatchdays: Map<LocalDate, List<MatchdayEntity>>,
    onEventClick: (EventEntity) -> Unit,
    onMatchdayClick: (MatchdayEntity) -> Unit,
    onEmptyDayClick: () -> Unit
) {
    val event = groupedEvents[date]?.firstOrNull()
    val matchday = groupedMatchdays[date]?.firstOrNull()
    val backgroundColor = when {
        date == today -> Color(0xFFB3E5FC)
        event != null -> Color(0xFF7CC77F)
        matchday != null -> Color(0xFFFFF59D)
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .border(1.dp, Color(0xFF00205B))
            .background(backgroundColor)
            .clickable {
                when {
                    event != null -> onEventClick(event)
                    matchday != null -> onMatchdayClick(matchday)
                    else -> onEmptyDayClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            date.dayOfMonth.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun MonthNavigationBar(
    currentMonth: Int,
    currentYear: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val locale = Locale("es", "ES")
    val monthName = DateFormatSymbols(locale).months[currentMonth - 1]
        .replaceFirstChar { it.titlecase(locale) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Mes anterior",
                    tint = Color(0xFF00205B)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = monthName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00205B)
                )
                Text(
                    text = currentYear.toString(),
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Mes siguiente",
                    tint = Color(0xFF00205B)
                )
            }
        }
    }
}

@Composable
fun DateSelectorDialog(
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(currentDate.year) }
    var selectedMonth by remember { mutableIntStateOf(currentDate.monthValue) }
    var selectedDay by remember { mutableIntStateOf(currentDate.dayOfMonth) }

    val daysInMonth = YearMonth.of(selectedYear, selectedMonth).lengthOfMonth()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Selecciona una Fecha", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (selectedYear > 1900) selectedYear-- }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Year")
                    }
                    Text(selectedYear.toString(), fontSize = 16.sp)
                    IconButton(onClick = { if (selectedYear < 2100) selectedYear++ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Year")
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (selectedMonth > 1) selectedMonth-- }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                    }
                    Text(DateFormatSymbols().months[selectedMonth - 1], fontSize = 16.sp)
                    IconButton(onClick = { if (selectedMonth < 12) selectedMonth++ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (selectedDay > 1) selectedDay-- }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Day")
                    }
                    Text(selectedDay.toString(), fontSize = 16.sp)
                    IconButton(onClick = { if (selectedDay < daysInMonth) selectedDay++ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Day")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = {
                        onDateSelected(LocalDate.of(selectedYear, selectedMonth, selectedDay))
                    }) {
                        Text("Seleccionar")
                    }
                    Button(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}





