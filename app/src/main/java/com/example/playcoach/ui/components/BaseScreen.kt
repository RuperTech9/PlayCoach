package com.example.playcoach.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playcoach.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    title: String,
    teamName: String? = null,
    onNavigateBack: (() -> Unit)? = null,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCalendar: (() -> Unit)? = null,
    onNavigateToMessages: (() -> Unit)? = null,
    onNavigateToSquad: (() -> Unit)? = null,
    onNavigateToStats: (() -> Unit)? = null,
    onNavigateToFormations: (() -> Unit)? = null,
    onNavigateToOthers: (() -> Unit)? = null,
    onNavigateToSelectTeam: (() -> Unit)? = null,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFCCE5FF), // Light blue background
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00205B),
                    titleContentColor = Color(0xFFFDF3D0)
                ),
                title = { Text(title) },
                navigationIcon = {
                    if (teamName != null) {
                        Column (
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            Text(
                                text = teamName,
                                color = Color(0xFFFDF3D0),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .clickable { onNavigateToSelectTeam?.invoke() }
                                    .padding(end = 8.dp)
                            )
                            IconButton(onClick = { onNavigateBack?.invoke() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color(0xFFFDF3D0)
                                )
                            }
                        }
                    } else {
                        onNavigateBack?.let {
                            IconButton(onClick = it) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color(0xFFFDF3D0)
                                )
                            }
                        }
                    }
                },
                actions = {
                    Row {
                        IconButton(onClick = onNavigateToNotifications) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_notificacion),
                                contentDescription = "Notifications",
                                tint = Color(0xFFFDF3D0),
                                modifier = Modifier.size(34.dp)
                            )
                        }
                        IconButton(onClick = onNavigateToProfile) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_jugador),
                                contentDescription = "Profile",
                                tint = Color(0xFFFDF3D0),
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF00205B),
                contentColor = Color(0xFFFDF3D0),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = { onNavigateToCalendar?.invoke() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "Calendar",
                            tint = Color(0xFFFDF3D0),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    IconButton(onClick = { onNavigateToMessages?.invoke() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sms),
                            contentDescription = "Messages",
                            tint = Color(0xFFFDF3D0),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    IconButton(onClick = { onNavigateToSquad?.invoke() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_plantilla),
                            contentDescription = "Squad",
                            tint = Color(0xFFFDF3D0),
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    IconButton(onClick = { onNavigateToStats?.invoke() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_estadisticas),
                            contentDescription = "Statistics",
                            tint = Color(0xFFFDF3D0),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    IconButton(onClick = { onNavigateToFormations?.invoke() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_formaciones),
                            contentDescription = "Formations",
                            tint = Color(0xFFFDF3D0),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    IconButton(onClick = { onNavigateToOthers?.invoke() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu),
                            contentDescription = "Others",
                            tint = Color(0xFFFDF3D0),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                content(Modifier.fillMaxSize())
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BaseScreenPreview() {
    BaseScreen(
        title = "Title",
        teamName = "Team",
        onNavigateBack = { /* No-op */ },
        onNavigateToNotifications = { /* No-op */ },
        onNavigateToProfile = { /* No-op */ },
        onNavigateToCalendar = { /* No-op */ },
        onNavigateToMessages = { /* No-op */ },
        onNavigateToSquad = { /* No-op */ },
        onNavigateToStats = { /* No-op */ },
        onNavigateToFormations = { /* No-op */ },
        onNavigateToOthers = { /* No-op */ }
    ) { modifier ->
        Column(modifier = modifier.padding(16.dp)) {
            Text("Example content inside BaseScreen", color = Color.Black)
        }
    }
}