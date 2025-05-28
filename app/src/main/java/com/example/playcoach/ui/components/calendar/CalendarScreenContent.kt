package com.example.playcoach.ui.components.calendar

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.viewmodels.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CalendarScreenContent(
    calendarViewModel: CalendarViewModel,
    eventViewModel: EventViewModel,
    matchdayViewModel: MatchdayViewModel,
    playerViewModel: PlayerViewModel,
    callUpViewModel: CallUpViewModel,
    teamName: String
) {
    val events by eventViewModel.events.collectAsState()
    val matchdays by matchdayViewModel.matchdays.collectAsState()
    val currentMonth by calendarViewModel.currentMonth.collectAsState()
    val currentYear by calendarViewModel.currentYear.collectAsState()
    val selectedDate by calendarViewModel.selectedDate.collectAsState()
    val selectedEvent by calendarViewModel.selectedEvent.collectAsState()
    val selectedMatchday by calendarViewModel.selectedMatchday.collectAsState()
    val showAddEventDialog by calendarViewModel.showAddEventDialog.collectAsState()
    val showDateSelector by calendarViewModel.showDateSelector.collectAsState()
    val showAttendanceDialog by calendarViewModel.showAttendanceDialog.collectAsState()
    val showCallUpDialog by calendarViewModel.showCallUpDialog.collectAsState()
    val visibleMatchdayIndex by calendarViewModel.visibleMatchdayIndex.collectAsState()

    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val today = LocalDate.now()

    val groupedEvents = remember(events) {
        events.groupBy { LocalDate.parse(it.date, dateFormatter) }
    }
    val groupedMatchdays = remember(matchdays) {
        matchdays.groupBy { LocalDate.parse(it.date, dateFormatter) }
    }

    val sortedMatchdays = remember(matchdays) {
        matchdays.sortedBy {
            runCatching { LocalDate.parse(it.date, dateFormatter) }.getOrNull()
        }
    }

    val visibleMatchday = sortedMatchdays.getOrNull(visibleMatchdayIndex)

    val lastPlayedMatchday = remember(sortedMatchdays) {
        sortedMatchdays
            .filter { it.played && runCatching { LocalDate.parse(it.date, dateFormatter) }.getOrNull()?.isBefore(today) == true }
            .maxByOrNull { LocalDate.parse(it.date, dateFormatter) }
    }

    LaunchedEffect(sortedMatchdays) {
        calendarViewModel.ensureInitialVisibleMatchdayIndex(sortedMatchdays)
    }

    LaunchedEffect(visibleMatchday?.team) {
        visibleMatchday?.team?.let {
            playerViewModel.loadPlayersByTeam(it)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                MonthNavigationBar(
                    currentMonth = currentMonth,
                    currentYear = currentYear,
                    onPreviousMonth = { calendarViewModel.previousMonth() },
                    onNextMonth = { calendarViewModel.nextMonth() }
                )
            }

            item {
                val daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth()
                val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1).dayOfWeek.value
                val days = (1..daysInMonth).map { day -> LocalDate.of(currentYear, currentMonth, day) }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    userScrollEnabled = false
                ) {
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
                                calendarViewModel.selectEvent(it)
                                calendarViewModel.selectDate(date)
                            },
                            onMatchdayClick = { calendarViewModel.selectMatchday(it) },
                            onEmptyDayClick = {
                                calendarViewModel.selectDate(date)
                                calendarViewModel.setShowAddEventDialog(true)
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.size(12.dp))
            }

            item {
                if (sortedMatchdays.isNotEmpty()) {
                    MatchdayBottomCard(
                        matchdays = sortedMatchdays,
                        visibleMatchdayIndex = visibleMatchdayIndex,
                        onIndexChange = calendarViewModel::setVisibleMatchdayIndex,
                        onMatchdayClick = calendarViewModel::selectMatchday,
                        initialVisibleIndex = sortedMatchdays.indexOfFirst {
                            runCatching { LocalDate.parse(it.date, dateFormatter) }.getOrNull()?.let { date ->
                                date >= today && (date.dayOfWeek == java.time.DayOfWeek.SATURDAY || date.dayOfWeek == java.time.DayOfWeek.SUNDAY)
                            } ?: false
                        },
                        playerViewModel = playerViewModel,
                        callUpViewModel = callUpViewModel
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(12.dp))
            }

            item {
                lastPlayedMatchday?.let { match ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 54.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        elevation = CardDefaults.cardElevation(4.dp),
                        border = BorderStroke(1.dp, Color(0xFF00205B))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "Último Partido Jugado",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF00205B)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Cabecera de jornada
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Jornada ${match.matchdayNumber}",
                                    fontSize = 16.sp,
                                    color = Color(0xFF00205B)
                                )
                            }

                            // Fecha y hora
                            Text(
                                text = "📅 ${match.date} — ${match.time}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            // Equipos
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = match.homeTeam,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = Color(0xFF00205B)
                                )
                                Text(text = "vs", fontSize = 14.sp, color = Color.Gray)
                                Text(
                                    text = match.awayTeam,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = Color(0xFF00205B)
                                )
                                Text("${match.homeGoals} - ${match.awayGoals}", fontSize = 15.sp)
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
                        calendarViewModel.selectEvent(eventsThatDay.first())
                    } else {
                        calendarViewModel.selectDate(date)
                        calendarViewModel.setShowAddEventDialog(true)
                    }
                    calendarViewModel.setShowDateSelector(false)
                },
                onDismiss = { calendarViewModel.setShowDateSelector(false) }
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
                    calendarViewModel.setShowAddEventDialog(false)
                },
                onDismiss = { calendarViewModel.setShowAddEventDialog(false) }
            )
        }

        if (selectedEvent != null) {
            EventDetailsDialog(
                event = selectedEvent!!,
                onDismiss = { calendarViewModel.clearSelectedEvent() },
                onMarkAttendance = {
                    calendarViewModel.setShowAttendanceDialog(true)
                    calendarViewModel.clearSelectedEvent()
                },
                onDelete = {
                    eventViewModel.deleteEvent(selectedEvent!!)
                    calendarViewModel.clearSelectedEvent()
                }
            )
        }

        if (showAttendanceDialog && selectedDate != null) {
            TrainingAttendanceDialog(
                date = selectedDate!!.format(dateFormatter),
                team = teamName,
                playerViewModel = playerViewModel,
                absenceViewModel = hiltViewModel(),
                onDismiss = { calendarViewModel.setShowAttendanceDialog(false) }
            )
        }

        if (selectedMatchday != null) {
            MatchdayDetailsDialog(
                matchday = selectedMatchday!!,
                onOpenCallUp = {
                    playerViewModel.loadPlayersByTeam(teamName)
                    calendarViewModel.setShowCallUpDialog(true)
                },
                onDismiss = { calendarViewModel.clearSelectedMatchday() }
            )
        }

        if (showCallUpDialog && selectedMatchday != null) {
            MatchdayCallUpDialog(
                matchday = selectedMatchday!!,
                players = playerViewModel.players.value,
                callUpViewModel = callUpViewModel,
                onDismiss = { calendarViewModel.setShowCallUpDialog(false) }
            )
        }
    }
}
