package com.example.playcoach.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.data.TeamsData
import com.example.playcoach.ui.components.BaseScreen
import com.example.playcoach.viewmodels.PlayerStatViewModel

data class PlayerStats(
    val number: Int,
    val name: String,
    val goals: Int,
    val assists: Int,
    val minutesPlayed: Int,
    val yellowCards: Int,
    val redCards: Int,
    val matchdays: List<MatchdayDetail>,
    val matchesPlayed: Int,
    val starts: Int,
    val substitutes: Int
)

data class MatchdayDetail(
    val description: String,
    val minutesPlayed: Int,
    val yellowCards: Int,
    val redCards: Int,
    val goals: Int,
    val assists: Int,
    val wasStarter: Boolean
)

@Composable
fun PlayersStats(
    onNavigateBack: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToSquad: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToFormations: () -> Unit,
    onNavigateToOthers: () -> Unit,
    onNavigateToPlayerDetail: (PlayerStats) -> Unit,
    teamName: String?
) {
    val playerStatViewModel: PlayerStatViewModel = hiltViewModel()

    LaunchedEffect(teamName) {
        if (!teamName.isNullOrEmpty()) {
            playerStatViewModel.loadStatsForTeam(teamName)
        }
    }

    val originalList = playerStatViewModel.playersStats.collectAsState().value
    val sortedList by remember(originalList) {
        derivedStateOf { originalList.sortedBy { it.number } }
    }

    BaseScreen(
        title = "Estadísticas",
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
        Column(
            modifier = contentModifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PlayerStatsGrid(
                players = sortedList,
                teamName = teamName.orEmpty(),
                onClick = { onNavigateToPlayerDetail(it) }
            )
        }
    }
}

@Composable
fun PlayerStatsGrid(
    players: List<PlayerStats>,
    teamName: String,
    onClick: (PlayerStats) -> Unit
) {
    val sorted = players.sortedBy { it.number }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(sorted) { player ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { visible = true }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                Card(
                    onClick = { onClick(player) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    border = BorderStroke(1.dp, Color(0xFF00205B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.8f)
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            val imageRes = remember(teamName, player.number) {
                                TeamsData.getPlayerImageForTeamAndNumber(teamName, player.number)
                            }

                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = "Foto",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color(0xFF00205B), CircleShape)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${player.number} - ${player.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF00205B),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "G ${player.goals} | A ${player.assists} | M ${player.minutesPlayed}",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "🟨 ${player.yellowCards}  🟥 ${player.redCards}  🎮 ${player.matchesPlayed}",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}