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

    var players by remember { mutableStateOf(formationViewModel.players) }
    var selectedPlayer by remember { mutableStateOf(formationViewModel.selectedPlayer) }
    var formationName by remember { mutableStateOf(formationViewModel.formationName) }
    var expanded by remember { mutableStateOf(formationViewModel.expanded) }
    var showDialog by remember { mutableStateOf(false) }
    var formationToDelete by remember { mutableStateOf<FormationEntity?>(null) }
    var fieldSizePx by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val boxWidthDp = with(density) { fieldSizePx.width.toDp() }
    val boxHeightDp = with(density) { fieldSizePx.height.toDp() }

    val scaleFactor = (boxWidthDp.value / 360f).coerceAtLeast(0.6f)
    val playerSizeDp = (40f * scaleFactor).dp
    val fontSizeSp = (10f * scaleFactor).sp
    val subSizeDp = playerSizeDp * 1f
    val subFontSizeSp = fontSizeSp * 1f

    var percentPositions by remember { mutableStateOf<List<Pair<Int, Pair<Float, Float>>>>(emptyList()) }

    LaunchedEffect(teamName) {
        if (!teamName.isNullOrBlank()) {
            playerViewModel.loadPlayersByTeam(teamName)
            formationViewModel.loadFormationsByTeam(teamName)
        }
    }

    LaunchedEffect(selectedFormation?.id, savedPositions, teamPlayers) {
        if (selectedFormation != null && savedPositions.isNotEmpty()) {
            percentPositions = savedPositions.map { pos ->
                pos.playerId to (pos.positionX to pos.positionY)
            }
        } else {
            percentPositions = teamPlayers.take(11).mapIndexed { index, player ->
                val (x, y) = when {
                    player.position == "Portero" -> 43.0f to 79.1f
                    index == 0 -> 11.1f to 62.5f
                    index == 1 -> 43.0f to 62.5f
                    index == 2 -> 72.2f to 62.5f
                    index == 3 -> 5.5f to 43.7f
                    index == 4 -> 26.0f to 43.7f
                    index == 5 -> 61.0f to 43.7f
                    index == 6 -> 81.0f to 43.7f
                    index == 7 -> 11.1f to 25.0f
                    index == 8 -> 43.0f to 25.0f
                    else -> 72.2f to 25.0f
                }
                player.number to (x to y)
            }
        }
    }

    LaunchedEffect(percentPositions, boxWidthDp, boxHeightDp) {
        if (boxWidthDp > 0.dp && boxHeightDp > 0.dp) {
            players = percentPositions.map { (id, percent) ->
                id to DpOffset(
                    (percent.first / 100f) * boxWidthDp,
                    (percent.second / 100f) * boxHeightDp
                )
            }
            formationViewModel.players = players
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
                        onValueChange = {
                            formationName = it
                            formationViewModel.formationName = it
                        },
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
                            formationViewModel.formationName = ""
                        }
                    }) { Text("Guardar") }
                }
            }

            item {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .background(Color(0xFFCCE5FF)),
                    contentAlignment = Alignment.Center
                ) {
                    val density = LocalDensity.current
                    val boxWidthDp = maxWidth
                    val boxHeightDp = maxHeight
                    val playerSizeDp = (50f * scaleFactor).dp


                    val pixelSize = remember { mutableStateOf(IntSize.Zero) }

                    Box(
                        modifier = Modifier
                            .size(boxWidthDp, boxHeightDp)
                            .onGloballyPositioned { layout ->
                                pixelSize.value = layout.size
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

                                // Calcular el offset proporcionalmente si las dimensiones han cambiado
                                val offsetX = offset.x
                                val offsetY = offset.y

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .offset(offsetX, offsetY)
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

                                                formationViewModel.players = players
                                            }
                                        }
                                        .clickable {
                                            if (selectedPlayer == null) {
                                                selectedPlayer = id
                                                formationViewModel.selectedPlayer = id
                                            } else {
                                                val sub = teamPlayers.find { it.number == selectedPlayer }
                                                if (sub != null && sub.number !in players.map { it.first }) {
                                                    players = players.map {
                                                        if (it.first == id) selectedPlayer!! to it.second else it
                                                    }
                                                    selectedPlayer = null
                                                    formationViewModel.selectedPlayer = null
                                                    formationViewModel.players = players
                                                } else {
                                                    selectedPlayer = id
                                                    formationViewModel.selectedPlayer = id
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

                                    Text(
                                        text = player.nickname.ifBlank { player.firstName },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF00205B),
                                        maxLines = 1,
                                        fontSize = fontSizeSp
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
                                modifier = Modifier.size(subSizeDp) ,
                                contentScale = ContentScale.Fit
                            )

                            val subText = if (player.nickname.isNotBlank()) {
                                "${player.number} - \"${player.nickname}\""
                            } else {
                                "${player.number}  - \"${player.firstName}\""
                            }

                            Text(
                                subText,
                                color = Color(0xFF00205B),
                                fontSize = subFontSizeSp,
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
                            val percentX = (offset.x / boxWidthDp) * 100f
                            val percentY = (offset.y / boxHeightDp) * 100f
                            PlayerPositionEntity(
                                formationId = formationId,
                                playerId = id,
                                positionX = percentX,
                                positionY = percentY
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
