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
                matchdayViewModel = matchdayViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun MatchDetailContent(
    matchday: MatchdayEntity,
    matchdayViewModel: MatchdayViewModel,
    modifier: Modifier
) {
    var homeGoals by remember { mutableStateOf(matchday.homeGoals.toString()) }
    var awayGoals by remember { mutableStateOf(matchday.awayGoals.toString()) }
    var summary by remember { mutableStateOf(matchday.summary) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF00205B))
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = "Jornada ${matchday.matchdayNumber}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📅 ${matchday.date}", color = Color(0xFFE3F2FD))
                    Text("🕒 ${matchday.time}", color = Color(0xFFE3F2FD))
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF00205B)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("⚽ Resultado", fontWeight = FontWeight.Bold, color = Color(0xFF00205B))

                    Text("🏠 ${matchday.homeTeam}", fontSize = 16.sp)
                    OutlinedTextField(
                        value = homeGoals,
                        onValueChange = { homeGoals = it },
                        label = { Text("Goles Local") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("🚩 ${matchday.awayTeam}", fontSize = 16.sp)
                    OutlinedTextField(
                        value = awayGoals,
                        onValueChange = { awayGoals = it },
                        label = { Text("Goles Visitante") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                    Text("📝 Crónica", fontWeight = FontWeight.Bold, color = Color(0xFF00205B))
                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("Escribe la crónica del partido...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5
                    )

                    Button(
                        onClick = {
                            val updated = matchday.copy(
                                homeGoals = homeGoals.toIntOrNull() ?: 0,
                                awayGoals = awayGoals.toIntOrNull() ?: 0,
                                summary = summary
                            )
                            matchdayViewModel.updateMatchday(updated)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                    ) {
                        Text("Guardar Cambios", color = Color.White)
                    }
                }
            }
        }
    }
}
