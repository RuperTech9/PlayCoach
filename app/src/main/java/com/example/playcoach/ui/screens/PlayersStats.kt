package com.example.playcoach.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.R
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
    val sortedList = originalList.sortedBy { it.number }

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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(sortedList) { player ->
                    PlayerStatsCard(
                        team = teamName.orEmpty(),
                        player = player,
                        onClick = { onNavigateToPlayerDetail(player) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerStatsCard(
    team: String,
    player: PlayerStats,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFF00205B)),
        elevation = CardDefaults.cardElevation(3.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageRes = TeamsData.getPlayerImageForTeamAndNumber(team, player.number)

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Photo of ${player.name}",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${player.number} - ${player.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00205B),
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.logo_sln),
                        contentDescription = "Escudo equipo",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Goles: ${player.goals}", fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Text("Asist: ${player.assists}", fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Text("", fontSize = 14.sp, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Min: ${player.minutesPlayed}", fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Text("Amar: ${player.yellowCards}", fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Text("Rojas: ${player.redCards}", fontSize = 14.sp, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Part: ${player.matchesPlayed}", fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Text("Tit: ${player.starts}", fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Text("Supl: ${player.substitutes}", fontSize = 14.sp, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
