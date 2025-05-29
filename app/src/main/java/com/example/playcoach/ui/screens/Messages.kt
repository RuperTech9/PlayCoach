package com.example.playcoach.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onNavigateToSelectTeam: () -> Unit,
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
        onNavigateToOthers = onNavigateToOthers,
        onNavigateToSelectTeam = onNavigateToSelectTeam,
    ) { modifier ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Mensajes",
                    tint = Color(0xFF90CAF9),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Todavía no hay mensajes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF00205B)
                )
                Text(
                    text = "Aquí podrás ver tus conversaciones con el equipo",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            FloatingActionButton(
                onClick = { /* TODO: abrir diálogo para nuevo mensaje */ },
                containerColor = Color(0xFF00205B),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo mensaje", tint = Color.White)
            }
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
        onNavigateToSelectTeam = {},
        teamName = "Infantil A"
    )
}
