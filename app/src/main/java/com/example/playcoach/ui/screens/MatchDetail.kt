package com.example.playcoach.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.data.entities.MatchdayEntity
import com.example.playcoach.viewmodels.MatchdayViewModel
import com.example.playcoach.viewmodels.PlayerStatViewModel
import com.example.playcoach.viewmodels.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetail(
    onNavigateBack: () -> Unit,
    matchdayId: Int,
) {
    val matchdayViewModel: MatchdayViewModel = hiltViewModel()

    val matchdayFlow = remember(matchdayId) {
        matchdayViewModel.getMatchdayById(matchdayId)
    }
    val matchday = matchdayFlow.collectAsState(initial = null).value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Detalle del Partido",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFDF3D0)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFFFDF3D0)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00205B)
                )
            )
        }
    ) { innerPadding ->
        if (matchday == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF00205B))
            }
        } else {
            MatchDetailContent(
                matchday = matchday,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun MatchDetailContent(
    matchday: MatchdayEntity,
    modifier: Modifier
) {
    val playerStatViewModel: PlayerStatViewModel = hiltViewModel()
    val playerViewModel: PlayerViewModel = hiltViewModel()

    val allStats by playerStatViewModel.allStats.collectAsState()
    val allPlayers by playerViewModel.players.collectAsState()

    LaunchedEffect(matchday.team) {
        playerViewModel.loadPlayersByTeam(matchday.team)
    }

    val statsForMatchday = allStats.filter { it.matchdayId == matchday.id }
    val scorers = statsForMatchday.filter { it.goals > 0 }
    val assistants = statsForMatchday.filter { it.assists > 0 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF00205B)),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Jornada ${matchday.matchdayNumber}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        "📅 ${matchday.date}    🕒 ${matchday.time}",
                        color = Color(0xFFD1E8FF),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        "${matchday.homeGoals} - ${matchday.awayGoals}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        matchday.homeTeam,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Text(
                        "vs",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFD1E8FF)
                    )
                    Text(
                        matchday.awayTeam,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF00205B)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("📊 Goleadores y Asistentes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF00205B))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Goleadores
                        Column(modifier = Modifier.weight(1f)) {
                            if (scorers.isEmpty()) {
                                Text("— Ninguno", color = Color.Gray)
                            } else {
                                scorers.sortedByDescending { it.goals }.forEach { stat ->
                                    val player = allPlayers.find { it.number == stat.playerId }
                                    AssistScorerChip(name = player?.firstName ?: "Jugador", value = stat.goals, icon = "⚽")
                                }
                            }
                        }

                        // Asistentes
                        Column(modifier = Modifier.weight(1f)) {
                            if (assistants.isEmpty()) {
                                Text("— Ninguno", color = Color.Gray)
                            } else {
                                assistants.sortedByDescending { it.assists }.forEach { stat ->
                                    val player = allPlayers.find { it.number == stat.playerId }
                                    AssistScorerChip(name = player?.firstName ?: "Jugador", value = stat.assists, icon = "🎯")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (matchday.summary.isNotBlank()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF00205B)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("📝 Crónica del Partido", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF00205B))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(matchday.summary, fontSize = 15.sp, color = Color(0xFF37474F))
                    }
                }
            }
        }
    }
}

@Composable
fun AssistScorerChip(name: String, value: Int, icon: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFF00205B).copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color(0xFF00205B)),
        modifier = Modifier
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(icon, fontSize = 14.sp)
            Spacer(Modifier.width(6.dp))
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF00205B))
            Spacer(Modifier.width(4.dp))
            Text("x$value", fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}
