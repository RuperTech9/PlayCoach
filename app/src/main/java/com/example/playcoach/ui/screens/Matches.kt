package com.example.playcoach.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.ui.components.BaseScreen
import com.example.playcoach.ui.components.matches.SegmentedButtonRow
import com.example.playcoach.ui.components.matches.PlayerStatsDialog
import com.example.playcoach.utils.generateHeadlinePdf
import com.example.playcoach.viewmodels.*
import kotlinx.coroutines.launch

@Composable
fun Matches(
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
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val matchdayViewModel: MatchdayViewModel = hiltViewModel()
    val playerStatViewModel: PlayerStatViewModel = hiltViewModel()
    val teamStatsViewModel: TeamStatsViewModel = hiltViewModel()
    val callupViewModel: CallUpViewModel = hiltViewModel()
    val mainViewModel: MainViewModel = hiltViewModel()

    val matchdays by matchdayViewModel.matchdays.collectAsState()
    val players by playerViewModel.players.collectAsState()
    val calledPlayers by callupViewModel.calledUpPlayers.collectAsState()
    val playerDialog = callupViewModel.playerDialog.collectAsState().value

    val scope = rememberCoroutineScope()
    var lastLoadedTeam by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(teamName) {
        teamName?.let {
            if (it != lastLoadedTeam) {
                lastLoadedTeam = it
                playerViewModel.loadPlayersByTeam(it)
            }
            matchdayViewModel.updateSelectedTeam(it)
            teamStatsViewModel.updateSelectedTeam(it)
        }
    }

    val filteredPlayers by remember(players, calledPlayers) {
        derivedStateOf {
            players.filter { it.number in calledPlayers }
                .sortedBy { it.number }
        }
    }

    val selectedMatchdayId by mainViewModel.selectedMatchdayIdFlow.collectAsState()
    val selectedMatchday = matchdays.find { it.id == selectedMatchdayId }


    val selectedMode = remember { mutableStateOf(mainViewModel.selectedMode) }

    LaunchedEffect(selectedMatchday?.id) {
        selectedMatchday?.id?.let {
            callupViewModel.loadCallUpForMatchday(it)
        }
    }

    BaseScreen(
        title = "Partidos",
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "📅 Selecciona una Jornada",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Color(0xFF00205B)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF)),
                border = BorderStroke(1.dp, Color(0xFF00205B))
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .heightIn(max = 300.dp)
                ) {
                    items(matchdays) { matchday ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    mainViewModel.selectedMatchdayId = matchday.id
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFF90CAF9)),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Jornada ${matchday.matchdayNumber} • ${matchday.date}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF00205B)
                                    )
                                    Text(
                                        text = if (matchday.played) "✅" else "🕒",
                                        color = if (matchday.played) Color(0xFF4CAF50) else Color(0xFFF57C00),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${matchday.homeTeam} - ${matchday.awayTeam}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                                Text(
                                    "${matchday.homeGoals} - ${matchday.awayGoals}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                                Text(
                                    "📝 ${matchday.time}",
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }

            // If a matchday is selected, show options
            selectedMatchday?.let { matchday ->
                Text(
                    "⚙️ Opciones Jornada ${matchday.matchdayNumber}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF00205B)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SegmentedButtonRow(
                        selectedOption = selectedMode.value,
                        onOptionSelected = {
                            selectedMode.value = it
                            mainViewModel.selectedMode = it
                        },
                        options = listOf(
                            "estadísticas" to "📊 Estadísticas Jugador",
                            "editar" to "✏️ Editar Jornada"
                        )
                    )
                }

                if (selectedMode.value == "estadísticas") {
                    val stats by playerStatViewModel.allStats.collectAsState()
                    val startersCount = stats.count { it.matchdayId == matchday.id && it.wasStarter }

                    var showTeamDialog by remember { mutableStateOf(false) }

                    if (filteredPlayers.isEmpty()) {
                        Text(
                            "⛔ No hay convocatoria registrada para esta jornada.",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "🧮 Titulares: $startersCount / 11",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = if (startersCount == 11) Color(0xFF2E7D32) else Color(0xFFF57C00),
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            if (startersCount == 11) {
                                Button(
                                    onClick = { showTeamDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                                ) {
                                    Text("👥 Ver Equipo Completo", color = Color.White)
                                }
                            }

                            filteredPlayers.forEach { player ->
                                val imageRes = remember(teamName, player.number) {
                                    com.example.playcoach.data.TeamsData.getPlayerImageForTeamAndNumber(
                                        teamName.orEmpty(),
                                        player.number
                                    )
                                }

                                val isStarter = stats.any {
                                    it.playerId == player.number && it.matchdayId == matchday.id && it.wasStarter
                                }

                                val cardColor = if (isStarter) Color(0xFFD0F8CE) else Color.White

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            callupViewModel.openPlayerStatsDialog(player, matchday.id)
                                        },
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(containerColor = cardColor),
                                    elevation = CardDefaults.cardElevation(3.dp),
                                    border = BorderStroke(1.dp, if (isStarter) Color(0xFF388E3C) else Color(0xFF66BB6A))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = imageRes),
                                            contentDescription = "Photo of ${player.firstName}",
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(MaterialTheme.shapes.small)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Nº ${player.number}", fontSize = 14.sp, color = Color.Gray)
                                            Text(player.firstName, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00205B))
                                        }
                                        Icon(Icons.Default.Add, contentDescription = "Register Stat", tint = Color(0xFF43A047))
                                    }
                                }
                            }
                        }
                    }

                    if (showTeamDialog) {
                        val starters = remember(filteredPlayers, stats, matchday.id) {
                            filteredPlayers.filter { player ->
                                stats.any { it.playerId == player.number && it.matchdayId == matchday.id && it.wasStarter }
                            }.sortedBy { it.number }
                        }

                        val starterIds = remember(starters) { starters.map { it.number } }

                        val substitutes = remember(filteredPlayers, starterIds) {
                            filteredPlayers.filterNot { it.number in starterIds }
                                .sortedBy { it.number }
                        }

                        Dialog(onDismissRequest = { showTeamDialog = false }) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                tonalElevation = 6.dp,
                                border = BorderStroke(1.dp, Color(0xFF00205B)),
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                                    .heightIn(max = 500.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    Text(
                                        "📋 Equipo para la Jornada ${matchday.matchdayNumber}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF00205B)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        item {
                                            Text(
                                                "✅ Titulares (${starters.size})",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp,
                                                color = Color(0xFF388E3C)
                                            )
                                        }
                                        items(starters) { player ->
                                            val imageRes = remember(player.team, player.number) {
                                                com.example.playcoach.data.TeamsData.getPlayerImageForTeamAndNumber(
                                                    player.team,
                                                    player.number
                                                )
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                            ) {
                                                Image(
                                                    painter = painterResource(id = imageRes),
                                                    contentDescription = "Photo of ${player.firstName}",
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    "${player.number} - ${player.firstName}",
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF00205B)
                                                )
                                            }
                                        }

                                        item {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                "🪑 Suplentes (${substitutes.size})",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp,
                                                color = Color(0xFFF57C00)
                                            )
                                        }
                                        items(substitutes) { player ->
                                            val imageRes = remember(player.team, player.number) {
                                                com.example.playcoach.data.TeamsData.getPlayerImageForTeamAndNumber(
                                                    player.team,
                                                    player.number
                                                )
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                            ) {
                                                Image(
                                                    painter = painterResource(id = imageRes),
                                                    contentDescription = "Photo of ${player.firstName}",
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    "${player.number} - ${player.firstName}",
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF00205B)
                                                )
                                            }
                                        }

                                        item {
                                            val context = LocalContext.current

                                            Button(
                                                onClick = {
                                                    val pdfFile = generateHeadlinePdf(context, matchday, starters, substitutes)

                                                    val uri = FileProvider.getUriForFile(
                                                        context,
                                                        "${context.packageName}.provider",
                                                        pdfFile
                                                    )

                                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                        type = "application/pdf"
                                                        putExtra(Intent.EXTRA_STREAM, uri)
                                                        putExtra(Intent.EXTRA_TEXT, "📋 Equipo Jornada ${matchday.matchdayNumber}")
                                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                    }

                                                    context.startActivity(Intent.createChooser(shareIntent, "Compartir PDF"))
                                                },
                                                modifier = Modifier.align(Alignment.End),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                                            ) {
                                                Text("📥 Descargar PDF", color = Color.White)
                                            }
                                        }

                                        item {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedButton(
                                                onClick = { showTeamDialog = false },
                                                modifier = Modifier.align(Alignment.End),
                                                border = BorderStroke(1.dp, Color(0xFF00205B))
                                            ) {
                                                Text("Cerrar", color = Color(0xFF00205B))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }     else if (selectedMode.value == "editar") {
                    var description by remember { mutableStateOf(matchday.time) }
                    var homeGoals by remember { mutableStateOf(matchday.homeGoals.toString()) }
                    var awayGoals by remember { mutableStateOf(matchday.awayGoals.toString()) }
                    var summary by remember { mutableStateOf(matchday.summary) }

                    val played by matchdayViewModel.played.collectAsState()
                    val snackbarHostState = remember { SnackbarHostState() }

                    LaunchedEffect(matchday.id) {
                        matchdayViewModel.updatePlayed(matchday.played)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 4.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
                            elevation = CardDefaults.cardElevation(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF00205B))
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 24.dp, vertical = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                Text(
                                    text = "✏️ Editar Jornada",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00205B)
                                )

                                OutlinedTextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    label = { Text("🕒 Hora") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = matchday.date,
                                    onValueChange = {},
                                    label = { Text("📅 Fecha") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = false,
                                    readOnly = true,
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = matchday.homeTeam,
                                    onValueChange = {},
                                    label = { Text("🏠 Equipo Local") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = false,
                                    readOnly = true,
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = homeGoals,
                                    onValueChange = { homeGoals = it },
                                    label = { Text("⚽ Goles Local") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = matchday.awayTeam,
                                    onValueChange = {},
                                    label = { Text("🛫 Equipo Visitante") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = false,
                                    readOnly = true,
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = awayGoals,
                                    onValueChange = { awayGoals = it },
                                    label = { Text("⚽ Goles Visitante") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = summary,
                                    onValueChange = { summary = it },
                                    label = { Text("📝 Crónica del Partido") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 100.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    maxLines = 6
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("¿Partido jugado?", modifier = Modifier.weight(1f), fontSize = 16.sp)
                                    Switch(
                                        checked = played,
                                        onCheckedChange = { matchdayViewModel.updatePlayed(it) }
                                    )
                                }

                                Text(
                                    text = if (played) "✅ Partido marcado como jugado" else "🕒 Partido pendiente",
                                    color = if (played) Color(0xFF388E3C) else Color(0xFFF57C00),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )

                                Button(
                                    onClick = {
                                        val editedMatchday = matchday.copy(
                                            time = description,
                                            homeGoals = homeGoals.toIntOrNull() ?: 0,
                                            awayGoals = awayGoals.toIntOrNull() ?: 0,
                                            summary = summary,
                                            played = played
                                        )

                                        scope.launch {
                                            matchdayViewModel.updateMatchday(editedMatchday)
                                            teamName?.let {
                                                teamStatsViewModel.updateSelectedTeam(it)
                                                teamStatsViewModel.forceRefresh()
                                            }

                                            mainViewModel.selectedMatchdayId = null
                                            snackbarHostState.showSnackbar("✅ Jornada guardada correctamente")
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                                ) {
                                    Text("Guardar Cambios", color = Color.White, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    playerDialog?.let { data ->
        PlayerStatsDialog(
            player = data.player,
            matchdayId = data.matchdayId,
            onSave = { goals, assists, yellowCards, redCards, minutes, wasStarter ->
                playerStatViewModel.insertPlayerStat(
                    playerId = data.player.number,
                    matchdayId = data.matchdayId,
                    goals = goals,
                    assists = assists,
                    yellowCards = yellowCards,
                    redCards = redCards,
                    minutesPlayed = minutes,
                    wasStarter = wasStarter
                )
                callupViewModel.closePlayerStatsDialog()
            },
            onDismiss = { callupViewModel.closePlayerStatsDialog() },
            playerStatsViewModel = playerStatViewModel
        )
    }
}
