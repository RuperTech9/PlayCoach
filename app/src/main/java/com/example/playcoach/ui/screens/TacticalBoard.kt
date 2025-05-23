package com.example.playcoach.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.R
import com.example.playcoach.ui.components.BaseScreen
import com.example.playcoach.viewmodels.FormationViewModel
import com.example.playcoach.viewmodels.PlayerViewModel
import com.example.playcoach.data.entities.FormationEntity
import com.example.playcoach.data.entities.PlayerPositionEntity
import kotlinx.coroutines.launch

@Composable
fun TacticalBoard(
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
    val formationViewModel: FormationViewModel = hiltViewModel()
    val playerViewModel: PlayerViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()
    val teamPlayers = playerViewModel.players.collectAsState().value
    val selectedFormation = formationViewModel.selectedFormation.collectAsState().value
    val savedPositions = formationViewModel.positions.collectAsState().value
    val formations = formationViewModel.formations.collectAsState().value

    var players by remember { mutableStateOf<List<Pair<Int, DpOffset>>>(emptyList()) }
    var formationName by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedPlayer by remember { mutableStateOf<Int?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var formationToDelete by remember { mutableStateOf<FormationEntity?>(null) }

    val playerSizeDp = 60.dp
    var fieldSizePx by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val fieldWidthDp = 360.dp
    val fieldHeightDp = 480.dp

    val boxWidthDp = with(density) { fieldSizePx.width.toDp() }
    val boxHeightDp = with(density) { fieldSizePx.height.toDp() }

    LaunchedEffect(teamName) {
        if (!teamName.isNullOrBlank()) {
            playerViewModel.loadPlayersByTeam(teamName)
            formationViewModel.loadFormationsByTeam(teamName)
        }
    }

    LaunchedEffect(teamPlayers, savedPositions) {
        players = if (savedPositions.isNotEmpty()) {
            savedPositions.map { pos ->
                pos.playerId to DpOffset(pos.positionX.dp, pos.positionY.dp)
            }
        } else {
            teamPlayers.take(11).mapIndexed { index, player ->
                player.number to when {
                    player.position == "Portero" -> DpOffset(150.dp, 380.dp)
                    index == 0 -> DpOffset(40.dp, 300.dp)
                    index == 1 -> DpOffset(150.dp, 300.dp)
                    index == 2 -> DpOffset(260.dp, 300.dp)
                    index == 3 -> DpOffset(20.dp, 210.dp)
                    index == 4 -> DpOffset(90.dp, 210.dp)
                    index == 5 -> DpOffset(200.dp, 210.dp)
                    index == 6 -> DpOffset(280.dp, 210.dp)
                    index == 7 -> DpOffset(40.dp, 120.dp)
                    index == 8 -> DpOffset(150.dp, 120.dp)
                    else -> DpOffset(260.dp, 120.dp)
                }
            }
        }
    }

    BaseScreen(
        title = "Formaciones",
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
            modifier = modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                if (showDialog && formationToDelete != null) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Confirmar eliminación") },
                        text = { Text("¿Deseas eliminar '${formationToDelete?.name}'?") },
                        confirmButton = {
                            TextButton(onClick = {
                                scope.launch {
                                    formationViewModel.deleteFormation(formationToDelete!!)
                                    formationViewModel.loadFormationsByTeam(teamName ?: "")
                                    if (selectedFormation?.id == formationToDelete?.id) {
                                        formationViewModel.clearSelection()
                                    }
                                    showDialog = false
                                    expanded = false
                                    formationToDelete = null
                                }
                            }) { Text("Eliminar", color = Color.Red) }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDialog = false
                                formationToDelete = null
                            }) { Text("Cancelar") }
                        }
                    )
                }
            }

            item {
                Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                    Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(selectedFormation?.name ?: "Seleccionar formación")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        formations.forEach { formation ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(formation.name, modifier = Modifier.padding(end = 8.dp))
                                        IconButton(onClick = {
                                            showDialog = true
                                            formationToDelete = formation
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_cerrar),
                                                contentDescription = "Eliminar",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    formationViewModel.selectFormation(formation)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = formationName,
                        onValueChange = { formationName = it },
                        label = { Text("Crear nueva formación") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        if (!teamName.isNullOrBlank() && formationName.isNotBlank()) {
                            val positions = players.map { (id, offset) ->
                                PlayerPositionEntity(
                                    formationId = 0,
                                    playerId = id,
                                    positionX = offset.x.value,
                                    positionY = offset.y.value
                                )
                            }
                            formationViewModel.createFormationWithPositions(formationName.trim(), teamName, positions)
                            formationName = ""
                        }
                    }) { Text("Guardar") }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .height(500.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFCCE5FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(fieldWidthDp, fieldHeightDp)
                            .onGloballyPositioned { layout ->
                                fieldSizePx = layout.size
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_campofutbol),
                            contentDescription = "Football field",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.matchParentSize()
                        )
                        players.forEach { (id, offset) ->
                            val player = teamPlayers.find { it.number == id }
                            if (player != null) {
                                val isSelected = selectedPlayer == id
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .offset(offset.x, offset.y)
                                        .background(if (isSelected) Color.LightGray else Color.Transparent)
                                        .pointerInput(id) {
                                            detectDragGestures { change, dragAmount ->
                                                change.consume()
                                                val dxDp = with(density) { dragAmount.x.toDp() }
                                                val dyDp = with(density) { dragAmount.y.toDp() }
                                                players = players.map {
                                                    if (it.first == id) {
                                                        val newX = (it.second.x + dxDp).coerceIn(0.dp, boxWidthDp - playerSizeDp)
                                                        val newY = (it.second.y + dyDp).coerceIn(0.dp, boxHeightDp - playerSizeDp)
                                                        id to DpOffset(newX, newY)
                                                    } else it
                                                }
                                            }
                                        }
                                        .clickable {
                                            if (selectedPlayer == null) {
                                                selectedPlayer = id
                                            } else {
                                                val sub = teamPlayers.find { it.number == selectedPlayer }
                                                if (sub != null && sub.number !in players.map { it.first }) {
                                                    players = players.map {
                                                        if (it.first == id) selectedPlayer!! to it.second else it
                                                    }
                                                    selectedPlayer = null
                                                } else {
                                                    selectedPlayer = id
                                                }
                                            }
                                        }
                                ) {
                                    val playerImage = if (player.position == "Portero") {
                                        R.drawable.minikit2
                                    } else {
                                        R.drawable.minikit
                                    }

                                    Image(
                                        painter = painterResource(id = playerImage),
                                        contentDescription = "Jugador",
                                        modifier = Modifier.size(playerSizeDp),
                                        contentScale = ContentScale.Fit
                                    )

                                    val playerText = when {
                                        player.nickname.isNotBlank() -> player.nickname
                                        else -> player.firstName
                                    }

                                    Text(
                                        playerText,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF00205B),
                                        maxLines = 1,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                val starters by remember(players) {
                    derivedStateOf {
                        players.map { it.first }
                    }
                }

                val substitutes by remember(teamPlayers, starters) {
                    derivedStateOf {
                        teamPlayers.filter { it.number !in starters }
                    }
                }

                Text("Suplentes:", modifier = Modifier.padding(start = 12.dp, top = 4.dp))
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    substitutes.forEach { player ->
                        val isSelected = selectedPlayer == player.number

                        val subImage = if (player.position == "Portero") {
                            R.drawable.minikit2
                        } else {
                            R.drawable.minikit
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .background(if (isSelected) Color.LightGray else Color.Transparent)
                                .padding(4.dp)
                                .clickable {
                                    if (selectedPlayer == null) {
                                        selectedPlayer = player.number
                                    } else {
                                        val starterIndex = players.indexOfFirst { it.first == selectedPlayer }
                                        if (starterIndex != -1) {
                                            players = players.toMutableList().apply {
                                                set(starterIndex, player.number to get(starterIndex).second)
                                            }
                                            selectedPlayer = null
                                        } else {
                                            selectedPlayer = player.number
                                        }
                                    }
                                }
                        ) {
                            Image(
                                painter = painterResource(id = subImage),
                                contentDescription = "Substitute",
                                modifier = Modifier.size(48.dp),
                                contentScale = ContentScale.Fit
                            )

                            val subText = if (player.nickname.isNotBlank()) {
                                "${player.number} - \"${player.nickname}\""
                            } else {
                                "${player.number}"
                            }

                            Text(
                                subText,
                                color = Color(0xFF00205B),
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        val formationId = selectedFormation?.id ?: return@Button
                        val positions = players.map { (id, offset) ->
                            PlayerPositionEntity(
                                formationId = formationId,
                                playerId = id,
                                positionX = offset.x.value,
                                positionY = offset.y.value
                            )
                        }
                        scope.launch {
                            formationViewModel.updatePositions(formationId, positions)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Guardar Posiciones")
                }
            }
        }
    }
}
