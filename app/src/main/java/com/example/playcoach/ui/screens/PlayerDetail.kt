package com.example.playcoach.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.R
import com.example.playcoach.data.TeamsData
import com.example.playcoach.viewmodels.*
import kotlinx.coroutines.launch

enum class MatchdayFilter(val label: String) {
    ALL("Todas"),
    STARTER("Titular"),
    SUBSTITUTE("Suplente"),
    MINUTES_OVER_30("Min > 30"),
    MINUTES_UNDER_30("Min < 30")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetail(
    playerId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToMatchDetail: (Int) -> Unit
) {
    val playerDetailViewModel: PlayerDetailViewModel = hiltViewModel()
    val absenceViewModel: AbsenceViewModel = hiltViewModel()
    val teamStatsViewModel: TeamStatsViewModel = hiltViewModel()

    LaunchedEffect(playerId) {
        if (playerId != -1) {
            playerDetailViewModel.loadPlayerAndStats(playerId)
        }
    }

    val state = playerDetailViewModel.playerDetail.collectAsState().value

    LaunchedEffect(state?.id) {
        state?.id?.let {
            absenceViewModel.loadAbsenceCount(it)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00205B),
                    titleContentColor = Color(0xFFFDF3D0)
                ),
                title = {
                    Text("Detalle del Jugador", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFFFDF3D0)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Cargando datos del jugador...")
            }
        } else {
            val absences by absenceViewModel.absenceCount.collectAsState()
            var selectedFilter by remember { mutableStateOf(MatchdayFilter.ALL) }

            val filteredMatchdays by remember(selectedFilter, state.matchdays) {
                derivedStateOf {
                    when (selectedFilter) {
                        MatchdayFilter.ALL -> state.matchdays
                        MatchdayFilter.STARTER -> state.matchdays.filter { it.wasStarter }
                        MatchdayFilter.SUBSTITUTE -> state.matchdays.filter { !it.wasStarter }
                        MatchdayFilter.MINUTES_OVER_30 -> state.matchdays.filter { it.minutesPlayed >= 30 }
                        MatchdayFilter.MINUTES_UNDER_30 -> state.matchdays.filter { it.minutesPlayed < 30 }
                    }
                }
            }
            val coroutineScope = rememberCoroutineScope()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFE3F2FD), Color(0xFFCCE5FF))
                        )
                    ),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    PlayerCard(state)
                }
                item {
                    StatSummaryCard(state, absences)
                }
                item {
                    Text(
                        text = "🗓 Jornadas Jugadas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00205B)
                    )
                }
                item {
                    MatchdayFilterCard(selectedFilter) {
                        selectedFilter = it
                    }
                }


                items(filteredMatchdays) { matchday ->
                    MatchdayDetailCard(matchday) {
                        coroutineScope.launch {
                            val id = teamStatsViewModel.getMatchdayId(state.team, matchday.matchdayNumber)
                            id?.let { onNavigateToMatchDetail(it) }
                        }
                    }
                }



            }
        }
    }
}

@Composable
fun PlayerCard(state: PlayerDetailState) {
    val imageRes = remember(state.team, state.number) {
        TeamsData.getPlayerImageForTeamAndNumber(state.team, state.number)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Picture of ${state.name}",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFF00205B), CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${state.number} - ${state.name} ${state.lastname}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00205B)
                )
                Text(
                    text = state.team,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            Image(
                painter = painterResource(id = R.drawable.logo_sln),
                contentDescription = "Team crest",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun StatSummaryCard(state: PlayerDetailState, absences: Int) {
    fun percentage(part: Int, total: Int): String {
        return if (total > 0) "${((part * 100f / total)).toInt()}%" else "0%"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "📊 Estadísticas Generales",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00205B)
        )

        fun statBox(label: String, value: String): @Composable () -> Unit = {
            Column(
                modifier = Modifier.width(80.dp), // Fijo para mantener alineación
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF00205B)
                )
                Text(text = value, fontSize = 14.sp)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            statBox("Goles", "${state.totalGoals}")()
            statBox("Asist", "${state.totalAssists}")()
            statBox("Min", "${state.totalMinutes} (${percentage(state.totalMinutes, state.totalPlayedMatchdays * 70)})")()
            statBox("Amar", "${state.totalYellows}")()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            statBox("Rojas", "${state.totalReds}")()
            statBox("Part", "${state.matchesPlayed} (${percentage(state.matchesPlayed, state.totalPlayedMatchdays)})")()
            statBox("Tit", "${state.starts} (${percentage(state.starts, state.totalPlayedMatchdays)})")()
            statBox("Supl", "${state.substitutes} (${percentage(state.substitutes, state.totalPlayedMatchdays)})")()
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Text(
                text = "Faltas Asistencia: $absences",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Red
            )
        }
    }

}


@Composable
fun MatchdayFilterCard(selectedFilter: MatchdayFilter, onFilterChange: (MatchdayFilter) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF00205B)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🎯 Filtrar jornadas por:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00205B)
            )

            var expanded by remember { mutableStateOf(false) }

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF00205B)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00205B))
                ) {
                    Text(text = selectedFilter.label)
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    MatchdayFilter.entries.forEach { filter ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    filter.label,
                                    fontWeight = if (filter == selectedFilter) FontWeight.Bold else FontWeight.Normal,
                                    color = if (filter == selectedFilter) Color(0xFF00205B) else Color.Black
                                )
                            },
                            onClick = {
                                onFilterChange(filter)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchdayDetailCard(matchday: MatchdayPlayerDetail, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF00205B)),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Jornada ${matchday.matchdayNumber} ${matchday.result}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00205B)
            )
            Text(
                text = "🏠 ${matchday.homeTeam} vs ${matchday.awayTeam}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF00205B)
            )
            StyledStatRow("Goles:", matchday.goals)
            StyledStatRow("Asistencias:", matchday.assists)
            StyledStatRow("Amarillas:", matchday.yellowCards)
            StyledStatRow("Rojas:", matchday.redCards)
            StyledStatRow("Minutos:", matchday.minutesPlayed)
            StyledStatRow("Titular:", if (matchday.wasStarter) "SÍ" else "NO")
        }
    }
}

@Composable
private fun StyledStatRow(label: String, value: Any) {
    Row {
        Text(
            text = "$label ",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00205B),
            fontSize = 14.sp
        )
        Text(
            text = value.toString(),
            fontSize = 14.sp
        )
    }
}

