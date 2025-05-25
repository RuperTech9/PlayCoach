package com.example.playcoach.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.playcoach.R
import com.example.playcoach.ui.components.BaseScreen

@Composable
fun Stats(
    onNavigateBack: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToSquad: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToFormations: () -> Unit,
    onNavigateToOthers: () -> Unit,
    onNavigateToTeamStats: () -> Unit,
    onNavigateToPlayerStats: () -> Unit,
    onNavigateToPlayerAttendance: () -> Unit,
    onNavigateToMatchdays: () -> Unit,
    teamName: String?
) {
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
    ) { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFCCE5FF))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                StatsCard(
                    imageRes = R.drawable.ic_calendar,
                    title = "Partidos (Editar Jornadas)",
                    onClick = onNavigateToMatchdays
                )
            }
            item {
                StatsCard(
                    imageRes = R.drawable.ic_monitoring,
                    title = "Estadísticas equipo",
                    onClick = onNavigateToTeamStats
                )
            }
            item {
                StatsCard(
                    imageRes = R.drawable.ic_estadisticas,
                    title = "Estadísticas jugadores",
                    onClick = onNavigateToPlayerStats
                )
            }
            item {
                StatsCard(
                    imageRes = R.drawable.ic_asistencia,
                    title = "Asistencia jugadores",
                    onClick = onNavigateToPlayerAttendance
                )
            }
        }
    }
}

@Composable
fun StatsCard(
    imageRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFF00205B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = Color(0xFF00205B),
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Ir a $title",
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatsScreenPreview() {
    Stats(
        onNavigateBack = {},
        onNavigateToNotifications = {},
        onNavigateToProfile = {},
        onNavigateToCalendar = {},
        onNavigateToMessages = {},
        onNavigateToSquad = {},
        onNavigateToStats = {},
        onNavigateToFormations = {},
        onNavigateToOthers = {},
        onNavigateToTeamStats = {},
        onNavigateToPlayerStats = {},
        onNavigateToPlayerAttendance = {},
        onNavigateToMatchdays = {},
        teamName = "Equipo"
    )
}
