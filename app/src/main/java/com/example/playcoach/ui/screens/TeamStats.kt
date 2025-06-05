package com.example.playcoach.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.ui.components.BaseScreen
import com.example.playcoach.viewmodels.PlayerStatViewModel
import com.example.playcoach.viewmodels.TeamStatsData
import com.example.playcoach.viewmodels.TeamStatsViewModel

@Composable
fun TeamStats(
    onNavigateBack: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToSquad: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToFormations: () -> Unit,
    onNavigateToOthers: () -> Unit,
    onNavigateToMatchDetail: (Int) -> Unit,
    onNavigateToSelectTeam: () -> Unit,
    teamName: String?
) {
    val teamStatsViewModel: TeamStatsViewModel = hiltViewModel()

    LaunchedEffect(teamName) {
        teamName?.let {
            teamStatsViewModel.updateSelectedTeam(it)
        }
    }

    val data = teamStatsViewModel.teamStats.collectAsState().value

    BaseScreen(
        title = "Est. Equipo",
        teamName = teamName,
        onNavigateBack = onNavigateBack,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToCalendar = onNavigateToCalendar,
        onNavigateToMessages = onNavigateToMessages,
        onNavigateToSquad = onNavigateToSquad,
        onNavigateToStats = onNavigateToStats,
        onNavigateToFormations = onNavigateToFormations,
        onNavigateToSelectTeam = onNavigateToSelectTeam,
        onNavigateToOthers = onNavigateToOthers
    ) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = "📊 Detalles Estadísticas",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF00205B),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            item {
                data?.let { stats ->
                    TeamStatsGrid(stats)
                } ?: Text(text = "Cargando o equipo no seleccionado")
            }

            item {
                TopPlayersSection(teamName = teamName)
            }

            item {
                if (data != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 8.dp)
                    ) {
                        Text(
                            text = "🗓 Jornadas",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00205B)
                        )
                    }
                }
            }

            itemsIndexed(data?.matchdays.orEmpty()) { _, matchday ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF00205B)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF)),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .clickable { onNavigateToMatchDetail(matchday.id) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Jornada ${matchday.matchdayNumber}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00205B),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${matchday.homeTeam} vs ${matchday.awayTeam}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Resultado: ${matchday.homeGoals} - ${matchday.awayGoals}",
                            fontSize = 15.sp,
                            color = Color(0xFF00205B),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamStatsGrid(data: TeamStatsData) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isCompact = maxWidth < 600.dp

        if (isCompact) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatsCardsSection(data)
                ChartSection(data)
            }
        } else {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatsCardsSection(data)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ChartSection(data)
                }
            }
        }
    }
}

@Composable
fun StatsCardsSection(data: TeamStatsData) {
    Row(Modifier.fillMaxWidth()) {
        StatsCard("Victorias", data.wins.toString(), MaterialTheme.colorScheme.primary, Modifier.weight(1f))
        StatsCard("Empates", data.draws.toString(), MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
    }
    Row(Modifier.fillMaxWidth()) {
        StatsCard("Derrotas", data.losses.toString(), MaterialTheme.colorScheme.error, Modifier.weight(1f))
        val total = data.wins + data.draws + data.losses
        StatsCard("Partidos Jugados", total.toString(), MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
    }
    Row(Modifier.fillMaxWidth()) {
        StatsCard("Goles Marcados", data.goalsFor.toString(), MaterialTheme.colorScheme.primary, Modifier.weight(1f))
        StatsCard("Goles Recibidos", data.goalsAgainst.toString(), MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
    }
}

@Composable
fun ChartSection(data: TeamStatsData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gráfica Resultados",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00205B),
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WinDrawLossPieChart(data.wins, data.draws, data.losses)
            ChartLegend()
        }
    }
}

@Composable
fun ChartLegend() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LegendItem(color = Color(0xFF4CAF50), label = "Victoria")
        LegendItem(color = Color(0xFFFFC107), label = "Empate")
        LegendItem(color = Color(0xFFF44336), label = "Derrota")
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(16.dp)) {
            drawCircle(color = color)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 14.sp, color = Color.DarkGray)
    }
}

@Composable
fun StatsCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun WinDrawLossPieChart(wins: Int, draws: Int, losses: Int) {
    val total = wins + draws + losses
    val angles = listOf(
        360f * wins / total,
        360f * draws / total,
        360f * losses / total
    )
    val colors = listOf(Color(0xFF4CAF50), Color(0xFFFFC107), Color(0xFFF44336))

    Canvas(modifier = Modifier.size(150.dp)) {
        var startAngle = 0f
        for (i in angles.indices) {
            drawArc(
                color = colors[i],
                startAngle = startAngle,
                sweepAngle = angles[i],
                useCenter = true
            )
            startAngle += angles[i]
        }
    }
}

@Composable
fun TopPlayersSection(teamName: String?) {
    val playerStatViewModel: PlayerStatViewModel = hiltViewModel()
    val stats by playerStatViewModel.playersStats.collectAsState()

    LaunchedEffect(teamName) {
        teamName?.let { playerStatViewModel.loadStatsForTeam(it) }
    }

    val topScorers = stats.sortedByDescending { it.goals }.take(5)
    val topAssists = stats.sortedByDescending { it.assists }.take(5)
    val topMinutes = stats.sortedByDescending { it.minutesPlayed }.take(5)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            StatsList("⚽ Top Goleadores", topScorers) { "${it.goals}" }
        }
        Column(modifier = Modifier.weight(1f)) {
            StatsList("🅰️ Top Asistentes", topAssists) { "${it.assists}" }
        }
        Column(modifier = Modifier.weight(1f)) {
            StatsList("🕒 Top Minutos", topMinutes) { "${it.minutesPlayed} min" }
        }
    }
}


@Composable
fun StatsList(title: String, players: List<PlayerStats>, statText: (PlayerStats) -> String) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00205B),
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        players.forEach {
            Text(
                text = "• ${it.name}: ${statText(it)}",
                fontSize = 15.sp,
                color = Color.DarkGray
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TeamStatsPreview() {
    BaseScreen(
        title = "Estadísticas \nEquipo",
        teamName = "Infantil A",
        onNavigateBack = {},
        onNavigateToNotifications = {},
        onNavigateToProfile = {},
        onNavigateToCalendar = {},
        onNavigateToMessages = {},
        onNavigateToSquad = {},
        onNavigateToStats = {},
        onNavigateToFormations = {},
        onNavigateToOthers = {},
        onNavigateToSelectTeam = {}
    ) { modifier ->
        Column(modifier = modifier.padding(16.dp)) {
            TeamStatsGrid(
                data = TeamStatsData(
                    wins = 4,
                    draws = 2,
                    losses = 1,
                    goalsFor = 12,
                    goalsAgainst = 7,
                    matchdays = emptyList()
                )
            )
        }
    }
}
