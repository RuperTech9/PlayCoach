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
import com.example.playcoach.data.entities.PlayerEntity

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
    onNavigateToSelectTeam: () -> Unit,
    onNavigateToOthers: () -> Unit,

    teamName: String?
) {
    val formationViewModel: FormationViewModel = hiltViewModel()
    val playerViewModel: PlayerViewModel = hiltViewModel()

    val teamPlayers = playerViewModel.players.collectAsState().value
    val selectedFormation = formationViewModel.selectedFormation.collectAsState().value

    var players by remember { mutableStateOf(formationViewModel.players) }
    var selectedPlayer by remember { mutableStateOf(formationViewModel.selectedPlayer) }
    var expanded by remember { mutableStateOf(formationViewModel.expanded) }
    var fieldSizePx by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val boxWidthDp = with(density) { fieldSizePx.width.toDp() }
    val scaleFactor = (boxWidthDp.value / 360f).coerceAtLeast(0.6f)
    val playerSizeDp = (40f * scaleFactor).dp
    val fontSizeSp = (10f * scaleFactor).sp
    val subSizeDp = playerSizeDp * 1f
    val subFontSizeSp = fontSizeSp * 1f

    val predefinedFormations = listOf(
        FormationEntity(id = 1, name = "3-4-3", team = teamName ?: ""),
        FormationEntity(id = 2, name = "3-5-2", team = teamName ?: ""),
        FormationEntity(id = 3, name = "4-4-2", team = teamName ?: ""),
        FormationEntity(id = 4, name = "4-3-3", team = teamName ?: ""),
        FormationEntity(id = 4, name = "4-5-1", team = teamName ?: ""),
        FormationEntity(id = 5, name = "5-3-2", team = teamName ?: ""),
        FormationEntity(id = 5, name = "5-4-1", team = teamName ?: "")
    )

    LaunchedEffect(teamName) {
        if (!teamName.isNullOrBlank()) {
            playerViewModel.loadPlayersByTeam(teamName)
            formationViewModel.loadFormationsByTeam(teamName)
        }
    }

    LaunchedEffect(selectedFormation, fieldSizePx, teamPlayers) {
        if (fieldSizePx.width > 0 && fieldSizePx.height > 0 && teamPlayers.isNotEmpty()) {
            val widthDp = with(density) { fieldSizePx.width.toDp() }
            val heightDp = with(density) { fieldSizePx.height.toDp() }

            players = if (selectedFormation != null) {
                getPredefinedFormationPositions(
                    selectedFormation.name,
                    teamPlayers,
                    widthDp,
                    heightDp
                )
            } else {
                getPredefinedFormationPositions("3-4-3", teamPlayers, widthDp, heightDp)
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
        onNavigateToSelectTeam = onNavigateToSelectTeam,
        onNavigateToOthers = onNavigateToOthers
    ) { modifier ->

        LazyColumn(
            modifier = modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                    Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(selectedFormation?.name ?: "Seleccionar formación")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        predefinedFormations.forEach { formation ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(formation.name, modifier = Modifier.padding(end = 8.dp))
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
                                "${player.number} - ${player.nickname}"
                            } else {
                                "${player.number}  - ${player.firstName}"
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
        }
    }
}

fun getPredefinedFormationPositions(
    name: String,
    teamPlayers: List<PlayerEntity>,
    widthDp: Dp,
    heightDp: Dp
): List<Pair<Int, DpOffset>> {
    val portero = teamPlayers.find { it.position == "Portero" }
    val jugadoresCampo = teamPlayers.filterNot { it == portero }.take(10)

    val formaciones = mapOf(
        "3-4-3" to listOf(
            20f to 70f, 43f to 70f, 66f to 70f,
            10f to 45f, 30.5f to 42f, 56f to 42f, 76f to 45f,
            20f to 22f, 43f to 20f, 66f to 22f
        ),
        "3-5-2" to listOf(
            20f to 70f, 43f to 70f, 70f to 70f,
            10f to 44f, 27.5f to 52f, 43f to 41f, 60f to 52f, 76f to 44f,
            33f to 22f, 55f to 22f
        ),
        "4-4-2" to listOf(
            10f to 70f, 30f to 70f, 56f to 70f, 76f to 70f,
            10f to 45f, 30.5f to 45f, 56f to 45f, 76f to 45f,
            32f to 20f, 55f to 20f
        ),
        "4-3-3" to listOf(
            10f to 70f, 30f to 70f, 56f to 70f, 76f to 70f,
            15f to 45f, 43f to 42f, 75f to 45f,
            19f to 22f, 43f to 20f, 70f to 22f
        ),
        "4-5-1" to listOf(
            10f to 70f, 30f to 70f, 56f to 70f, 76f to 70f,
            10f to 44f, 27.5f to 52f, 43f to 41f, 60f to 52f, 76f to 44f,
            43f to 20f
        ),
        "5-3-2" to listOf(
            5f to 70f, 22f to 70f, 43f to 68f, 64f to 70f, 81f to 70f,
            15f to 45f, 43f to 38f, 75f to 45f,
            32f to 20f, 55f to 20f
        ),
        "5-4-1" to listOf(
            5f to 70f, 22f to 70f, 43f to 68f, 64f to 70f, 81f to 70f,
            8f to 39f, 27.5f to 44f, 60f to 44f, 80f to 39f,
            43f to 20f
        )
    )

    val posiciones = formaciones[name] ?: return emptyList()

    val campo = jugadoresCampo.mapIndexed { index, player ->
        player.number to DpOffset(
            (posiciones[index].first / 100f) * widthDp,
            (posiciones[index].second / 100f) * heightDp
        )
    }

    val porteroOffset = portero?.let {
        it.number to DpOffset(
            (43f / 100f) * widthDp,
            (85f / 100f) * heightDp
        )
    }

    return if (porteroOffset != null) listOf(porteroOffset) + campo else campo
}
