package com.example.playcoach.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.playcoach.data.TeamsData
import com.example.playcoach.data.entities.CoachEntity
import com.example.playcoach.data.entities.PlayerEntity
import com.example.playcoach.ui.components.BaseScreen
import com.example.playcoach.viewmodels.CoachViewModel
import com.example.playcoach.viewmodels.PlayerViewModel
import kotlinx.coroutines.launch

@Composable
fun Squad(
    onNavigateBack: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToSquad: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToFormations: () -> Unit,
    onNavigateToOthers: () -> Unit,
    onNavigateToPlayerDetails: (PlayerEntity) -> Unit,
    teamName: String?,
    playerViewModel: PlayerViewModel,
    coachViewModel: CoachViewModel
) {
    LaunchedEffect(teamName) {
        if (!teamName.isNullOrBlank()) {
            playerViewModel.loadPlayersByTeam(teamName)
            coachViewModel.loadCoachesForTeam(teamName)
        }
    }

    val players by playerViewModel.players.collectAsState()
    val coaches by coachViewModel.coaches.collectAsState()

    var showCoachDialog by remember { mutableStateOf(false) }
    var showPlayerDialog by remember { mutableStateOf(false) }

    var coachToDelete by remember { mutableStateOf<CoachEntity?>(null) }
    var playerToDelete by remember { mutableStateOf<PlayerEntity?>(null) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    BaseScreen(
        title = "Plantilla",
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SquadSection(
                title = "Entrenadores",
                titleColor = Color(0xFF00205B),
                titleSize = 20.sp,
                fabColor = Color(0xFF1A73E8),
                fabOnClick = { showCoachDialog = true },
                content = {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(coaches) { coach ->
                            CoachCardItem(
                                text = coach.name,
                                deleteText = "Eliminar Entrenador",
                                onDeleteClick = { coachToDelete = coach }
                            )
                        }
                    }
                }
            )

            SquadSection(
                title = "Jugadores",
                titleColor = Color(0xFF00205B),
                titleSize = 20.sp,
                fabColor = Color(0xFF4CAF50),
                fabOnClick = { showPlayerDialog = true },
                content = {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(players.sortedBy { it.number }) { player ->
                            PlayerCardItem(
                                team = teamName.orEmpty(),
                                player = player,
                                onDeleteClick = { playerToDelete = player },
                                onInfoClick = { onNavigateToPlayerDetails(player) }
                            )
                        }
                    }
                }
            )

            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        if (showCoachDialog) {
            AddCoachDialog(
                onAdd = { name ->
                    scope.launch {
                        val ok = coachViewModel.addCoachIfPossible(
                            team = teamName.orEmpty(),
                            name = name
                        )
                        errorMessage = if (!ok) "Máximo 3 entrenadores en este equipo." else null
                        showCoachDialog = false
                    }
                },
                onDismiss = { showCoachDialog = false }
            )
        }

        if (showPlayerDialog) {
            AddPlayerDialog(
                onAdd = { firstName, lastName, nickname, number, position ->
                    scope.launch {
                        val ok = playerViewModel.addPlayerIfPossible(
                            team = teamName.orEmpty(),
                            firstName = firstName,
                            lastName = lastName,
                            nickname = nickname,
                            number = number,
                            position = position
                        )
                        errorMessage = if (!ok) "El dorsal $number ya está en uso en este equipo." else null
                        showPlayerDialog = false
                    }
                },
                onDismiss = { showPlayerDialog = false }
            )
        }

        if (coachToDelete != null) {
            AlertDialog(
                onDismissRequest = { coachToDelete = null },
                title = { Text("¿Eliminar Entrenador?") },
                text = {
                    Text("Vas a eliminar a '${coachToDelete!!.name}' y sus estadísticas. ¿Deseas continuar?")
                },
                confirmButton = {
                    Button(onClick = {
                        val localCoach = coachToDelete
                        coachToDelete = null
                        if (localCoach != null) {
                            scope.launch {
                                coachViewModel.deleteCoach(localCoach)
                            }
                        }
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { coachToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (playerToDelete != null) {
            AlertDialog(
                onDismissRequest = { playerToDelete = null },
                title = { Text("¿Eliminar Jugador?") },
                text = {
                    Text("Vas a eliminar a '${playerToDelete!!.firstName}' y sus estadísticas. ¿Deseas continuar?")
                },
                confirmButton = {
                    Button(onClick = {
                        val localPlayer = playerToDelete
                        playerToDelete = null
                        if (localPlayer != null) {
                            scope.launch {
                                playerViewModel.deletePlayer(localPlayer)
                            }
                        }
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { playerToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun SquadSection(
    title: String,
    titleColor: Color,
    titleSize: TextUnit,
    fabColor: Color,
    fabOnClick: () -> Unit,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.weight(1f))
                FloatingActionButton(
                    onClick = fabOnClick,
                    containerColor = fabColor,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun CoachCardItem(
    text: String,
    deleteText: String,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFF00205B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = deleteText,
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun AddCoachDialog(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Añadir Entrenador",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00205B)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAdd(name.trim())
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Añadir")
                    }
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerCardItem(
    team: String,
    player: PlayerEntity,
    onDeleteClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    val imageRes = TeamsData.getPlayerImageForTeamAndNumber(team, player.number)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFF00205B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Photo of ${player.firstName}",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            val fullName = "${player.firstName} ${player.lastName}"

            Text(
                text = "${player.number} - $fullName",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Ver Detalle",
                    tint = Color(0xFF1A73E8)
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar Jugador",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun AddPlayerDialog(
    onAdd: (String, String, String, Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var numberText by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("Jugador") }
    val positions = listOf("Jugador", "Portero")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Añadir Jugador",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00205B)
                )

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Apodo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = numberText,
                    onValueChange = { numberText = it },
                    label = { Text("Dorsal") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Posición:", fontSize = 16.sp, color = Color(0xFF00205B))
                    Spacer(modifier = Modifier.width(8.dp))
                    DropdownMenuBox(
                        selected = position,
                        options = positions,
                        onSelectedChange = { position = it }
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val number = numberText.toIntOrNull()
                            if (firstName.isNotBlank() && number != null) {
                                onAdd(firstName.trim(), lastName.trim(), nickname.trim(), number, position)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Añadir")
                    }
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuBox(selected: String, options: List<String>, onSelectedChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}