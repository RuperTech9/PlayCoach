package com.example.playcoach.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playcoach.ui.components.BaseScreen

@Composable
fun Others(
    onNavigateBack: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToSquad: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToFormations: () -> Unit,
    onNavigateToOthers: () -> Unit,
    teamName: String?
) {
    BaseScreen(
        title = "Otros",
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
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item { MenuItem("Calendario", onNavigateToCalendar) }
            item { MenuItem("Mensajes", onNavigateToMessages) }
            item { MenuItem("Plantilla", onNavigateToSquad) }
            item { MenuItem("Estadísticas", onNavigateToStats) }
            item { MenuItem("Formaciones", onNavigateToFormations) }
            item { MenuItem("Otros", onNavigateToOthers) }
        }
    }
}

@Composable
fun MenuItem(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFF00205B)),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            color = Color(0xFF00205B)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OthersScreenPreview() {
    Others(
        onNavigateBack = {},
        onNavigateToNotifications = {},
        onNavigateToProfile = {},
        onNavigateToCalendar = {},
        onNavigateToMessages = {},
        onNavigateToSquad = {},
        onNavigateToStats = {},
        onNavigateToFormations = {},
        onNavigateToOthers = {},
        teamName = "Equipo"
    )
}
