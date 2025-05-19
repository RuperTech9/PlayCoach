package com.example.playcoach.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.R
import com.example.playcoach.data.entities.TeamEntity
import com.example.playcoach.viewmodels.EventViewModel
import com.example.playcoach.viewmodels.MatchdayViewModel
import com.example.playcoach.viewmodels.PlayerStatViewModel
import com.example.playcoach.viewmodels.PlayerViewModel
import com.example.playcoach.viewmodels.TeamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTeam(
    onTeamSelected: (String) -> Unit,
    onAddTeam: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val matchdayViewModel: MatchdayViewModel = hiltViewModel()
    val teamViewModel: TeamViewModel = hiltViewModel()
    val eventViewModel: EventViewModel = hiltViewModel()

    val teams by teamViewModel.teams.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00205B),
                    titleContentColor = Color(0xFFFDF3D0)
                ),
                title = {
                    Text(
                        "Selecciona un Equipo",
                        color = Color(0xFFFDF3D0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_sln),
                            contentDescription = "Volver"
                        )
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
                    FloatingActionButton(
                        onClick = { onAddTeam() },
                        containerColor = Color.Yellow,
                        elevation = FloatingActionButtonDefaults.elevation(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Añadir Equipo",
                            tint = Color(0xFF00205B),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFCCE5FF)) // light sky blue
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(teams) { team: TeamEntity ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF00205B)),
                        elevation = CardDefaults.cardElevation(3.dp),
                        onClick = {
                            matchdayViewModel.updateSelectedTeam(team.name)
                            eventViewModel.updateSelectedTeam(team.name)
                            onTeamSelected(team.name)
                        }
                    ) {
                        Text(
                            text = team.name,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 18.sp,
                            color = Color(0xFF00205B)
                        )
                    }
                }
            }
        }
    }
}
