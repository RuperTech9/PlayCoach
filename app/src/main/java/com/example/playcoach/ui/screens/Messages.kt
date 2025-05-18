package com.example.playcoach.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.playcoach.ui.components.BaseScreen

@Composable
fun Messages(
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
        title = "Mensajes",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {
            Text("Vista de Mensajes")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessagesScreenPreview() {
    Messages(
        onNavigateBack = {},
        onNavigateToNotifications = {},
        onNavigateToProfile = {},
        onNavigateToCalendar = {},
        onNavigateToMessages = {},
        onNavigateToSquad = {},
        onNavigateToStats = {},
        onNavigateToFormations = {},
        onNavigateToOthers = {},
        teamName = ""
    )
}
