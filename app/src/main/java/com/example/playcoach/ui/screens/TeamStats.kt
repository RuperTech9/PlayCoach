package com.example.playcoach.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playcoach.ui.components.BaseScreen
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
    teamName: String?,
    teamViewModel: TeamStatsViewModel
) {
    LaunchedEffect(teamName) {
        teamName?.let {
            teamViewModel.updateSelectedTeam(it)
        }
    }

    val data = teamViewModel.teamStats.collectAsState().value

    BaseScreen(
        title = "Estadísticas Equipo",
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatsCard("Victorias", data.wins.toString(), MaterialTheme.colorScheme.primary)
            StatsCard("Empates", data.draws.toString(), MaterialTheme.colorScheme.secondary)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatsCard("Derrotas", data.losses.toString(), MaterialTheme.colorScheme.error)
            val total = data.wins + data.draws + data.losses
            StatsCard("Partidos Jugados", total.toString(), MaterialTheme.colorScheme.tertiary)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatsCard("Goles Marcados", data.goalsFor.toString(), MaterialTheme.colorScheme.primary)
            StatsCard("Goles Recibidos", data.goalsAgainst.toString(), MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun StatsCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .width(150.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
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
