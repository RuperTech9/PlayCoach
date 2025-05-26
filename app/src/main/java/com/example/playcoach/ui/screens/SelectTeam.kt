package com.example.playcoach.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import com.example.playcoach.viewmodels.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTeam(
    onTeamSelected: (String) -> Unit,
    onAddTeam: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val teamViewModel: TeamViewModel = hiltViewModel()
    val matchdayViewModel: MatchdayViewModel = hiltViewModel()
    val eventViewModel: EventViewModel = hiltViewModel()
    val mainViewModel: MainViewModel = hiltViewModel()

    val colors = listOf(
        Color(0xFFD1E8FF),
        Color(0xFFFFF0B3),
        Color(0xFFFFD6E8),
        Color(0xFFE0FFD1),
        Color(0xFFE6D1FF),
        Color(0xFFFFE3C1)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Elige tu equipo ⚽",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFFFDF3D0)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cerrar),
                            contentDescription = "Volver",
                            tint = Color(0xFFFDF3D0)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00205B)
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Nuevo Equipo", fontWeight = FontWeight.Bold) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Añadir",
                        tint = Color(0xFF00205B)
                    )
                },
                containerColor = Color.Yellow,
                onClick = onAddTeam
            )
        },
        containerColor = Color(0xFFCCE5FF)
    ) { innerPadding ->

        val teams by teamViewModel.teams.collectAsState()

        val customOrder = listOf(
            "Chupetines", "Prebenjamín A", "Prebenjamín B",
            "Benjamín A", "Benjamín B", "Benjamín C",
            "Alevín A", "Alevín B", "Alevín C",
            "Infantil A", "Infantil B", "Infantil C",
            "Cadete A", "Cadete B", "Juvenil", "Féminas", "Senior"
        )

        val orderedTeams = remember(teams) {
            teams.sortedBy { team ->
                customOrder.indexOf(team.name).takeIf { it >= 0 } ?: Int.MAX_VALUE
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 140.dp),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            itemsIndexed(orderedTeams) { index, team ->
                val backgroundColor = colors[index % colors.size]
                ElevatedCard(
                    onClick = {
                        mainViewModel.selectedTeam = team.name
                        matchdayViewModel.updateSelectedTeam(team.name)
                        eventViewModel.updateSelectedTeam(team.name)
                        onTeamSelected(team.name)
                    },
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.large),
                    colors = CardDefaults.elevatedCardColors(containerColor = backgroundColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_sln),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = team.name.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF00205B),
                            modifier = Modifier.padding(top = 8.dp),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

