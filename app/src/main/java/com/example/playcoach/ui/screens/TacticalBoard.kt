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
        }
    }
}
fun getPredefinedFormationPositions(
    name: String,
    teamPlayers: List<PlayerEntity>,
    widthDp: Dp,
    heightDp: Dp
): List<Pair<Int, DpOffset>> {
    return when (name) {
        "3-4-3" -> teamPlayers.take(11).mapIndexed { index, player ->
            val (xPercent, yPercent) = when {
                player.position == "Portero" -> 43f to 79.1f
                index == 0 -> 11f to 62.5f
                index == 1 -> 43f to 62.5f
                index == 2 -> 75f to 62.5f
                index == 3 -> 5f to 43.7f
                index == 4 -> 25f to 43.7f
                index == 5 -> 61f to 43.7f
                index == 6 -> 81f to 43.7f
                index == 7 -> 11.1f to 25f
                index == 8 -> 43f to 25f
                else -> 72.2f to 25f
            }
            player.number to DpOffset(
                (xPercent / 100f) * widthDp,
                (yPercent / 100f) * heightDp
            )
        }

        "3-5-2" -> teamPlayers.take(11).mapIndexed { index, player ->
            val (xPercent, yPercent) = when {
                player.position == "Portero" -> 43f to 79.1f
                index == 0 -> 11f to 62.5f
                index == 1 -> 43f to 62.5f
                index == 2 -> 75f to 62.5f
                index == 3 -> 5f to 43.7f
                index == 4 -> 25f to 43.7f
                index == 5 -> 43f to 35f
                index == 6 -> 61f to 43.7f
                index == 7 -> 81f to 43.7f
                index == 8 -> 25f to 25f
                else -> 61f to 25f
            }
            player.number to DpOffset(
                (xPercent / 100f) * widthDp,
                (yPercent / 100f) * heightDp
            )
        }

        "4-4-2" -> teamPlayers.take(11).mapIndexed { index, player ->
            val (xPercent, yPercent) = when {
                player.position == "Portero" -> 43f to 79.1f
                index == 0 -> 5f to 62.5f
                index == 1 -> 26f to 62.5f
                index == 2 -> 61f to 62.5f
                index == 3 -> 81f to 62.5f
                index == 4 -> 5f to 43.7f
                index == 5 -> 25f to 43.7f
                index == 6 -> 61f to 43.7f
                index == 7 -> 81f to 43.7f
                index == 8 -> 25f to 25f
                else -> 61f to 25f
            }
            player.number to DpOffset(
                (xPercent / 100f) * widthDp,
                (yPercent / 100f) * heightDp
            )
        }

        "4-3-3" -> teamPlayers.take(11).mapIndexed { index, player ->
            val (xPercent, yPercent) = when {
                player.position == "Portero" -> 43f to 79.1f
                index == 0 -> 5f to 62.5f
                index == 1 -> 26f to 62.5f
                index == 2 -> 61f to 62.5f
                index == 3 -> 81f to 62.5f
                index == 4 -> 19f to 43.7f
                index == 5 -> 43f to 43.7f
                index == 6 -> 68f to 43.7f
                index == 7 -> 11.1f to 25f
                index == 8 -> 43f to 25f
                else -> 72.2f to 25f
            }
            player.number to DpOffset(
                (xPercent / 100f) * widthDp,
                (yPercent / 100f) * heightDp
            )
        }

        "4-5-1" -> teamPlayers.take(11).mapIndexed { index, player ->
            val (xPercent, yPercent) = when {
                player.position == "Portero" -> 43f to 79.1f
                index == 0 -> 5f to 62.5f
                index == 1 -> 26f to 62.5f
                index == 2 -> 61f to 62.5f
                index == 3 -> 81f to 62.5f
                index == 4 -> 5f to 43.7f
                index == 5 -> 25f to 43.7f
                index == 6 -> 43f to 35f
                index == 7 -> 61f to 43.7f
                index == 8 -> 81f to 43.7f
                else -> 43f to 20f
            }
            player.number to DpOffset(
                (xPercent / 100f) * widthDp,
                (yPercent / 100f) * heightDp
            )
        }

        "5-3-2" -> teamPlayers.take(11).mapIndexed { index, player ->
            val (xPercent, yPercent) = when {
                player.position == "Portero" -> 43f to 79.1f
                index == 0 -> 5f to 62.5f
                index == 1 -> 24f to 62.5f
                index == 2 -> 43f to 62.5f
                index == 3 -> 62f to 62.5f
                index == 4 -> 81f to 62.5f
                index == 5 -> 22f to 45f
                index == 6 -> 43f to 40f
                index == 7 -> 64f to 45f
                index == 8 -> 25f to 25f
                else -> 61f to 25f
            }
            player.number to DpOffset(
                (xPercent / 100f) * widthDp,
                (yPercent / 100f) * heightDp
            )
        }

        "5-4-1" -> teamPlayers.take(11).mapIndexed { index, player ->
            val (xPercent, yPercent) = when {
                player.position == "Portero" -> 43f to 79.1f
                index == 0 -> 5f to 62.5f
                index == 1 -> 24f to 62.5f
                index == 2 -> 43f to 62.5f
                index == 3 -> 62f to 62.5f
                index == 4 -> 81f to 62.5f
                index == 5 -> 5f to 43.7f
                index == 6 -> 25f to 43.7f
                index == 7 -> 61f to 43.7f
                index == 8 -> 81f to 43.7f
                else -> 43f to 25f
            }
            player.number to DpOffset(
                (xPercent / 100f) * widthDp,
                (yPercent / 100f) * heightDp
            )
        }

        else -> emptyList()
    }
}
