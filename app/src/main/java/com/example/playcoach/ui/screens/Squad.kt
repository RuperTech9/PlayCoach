package com.example.playcoach.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.R
import com.example.playcoach.data.TeamsData
import com.example.playcoach.data.entities.PlayerEntity
import com.example.playcoach.data.entities.CoachEntity
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
    teamName: String?
) {
    val coachViewModel: CoachViewModel = hiltViewModel()
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    val players by playerViewModel.players.collectAsState()
    val coaches by coachViewModel.coaches.collectAsState()

    var tabIndex by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var playerToDelete by remember { mutableStateOf<PlayerEntity?>(null) }
    var coachToDelete by remember { mutableStateOf<CoachEntity?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(teamName) {
        teamName?.let {
            coachViewModel.loadCoachesForTeam(it)
            playerViewModel.loadPlayersByTeam(it)
        }
    }

    val tabs = listOf("Jugadores", "Entrenadores")
    val fabColor = if (tabIndex == 0) Color(0xFF4CAF50) else Color(0xFF1A73E8)

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
        Column(Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = tabIndex, containerColor = Color(0xFFE3F2FD)) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (tabIndex) {
                    0 -> PlayersGrid(
                        players = players,
                        teamName = teamName.orEmpty(),
                        onInfoClick = onNavigateToPlayerDetails,
                        onDeleteClick = { playerToDelete = it }
                    )
                    1 -> CoachesList(
                        coaches = coaches,
                        onDeleteClick = { coachToDelete = it }
                    )
                }

                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = fabColor,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir")
                }
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    if (showDialog) {
        if (tabIndex == 0) {
            AddPlayerDialog(
                onAdd = { f, l, n, d, p ->
                    scope.launch {
                        val success = playerViewModel.addPlayerIfPossible(teamName.orEmpty(), f, l, n, d, p)
                        errorMessage = if (!success) "El dorsal $d ya está en uso en este equipo."
                        else null
                        showDialog = false
                    }
                },
                onDismiss = { showDialog = false }
            )
        } else {
            AddCoachDialog(
                onAdd = { name ->
                    scope.launch {
                        coachViewModel.addCoachIfPossible(teamName.orEmpty(), name)
                        showDialog = false
                    }
                },
                onDismiss = { showDialog = false }
            )
        }
    }

    playerToDelete?.let { player ->
        AlertDialog(
            onDismissRequest = { playerToDelete = null },
            title = { Text("¿Eliminar jugador?") },
            text = { Text("Vas a eliminar a ${player.firstName}. ¿Deseas continuar?") },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        playerViewModel.deletePlayer(player)
                        playerToDelete = null
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { playerToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    coachToDelete?.let { coach ->
        AlertDialog(
            onDismissRequest = { coachToDelete = null },
            title = { Text("¿Eliminar entrenador?") },
            text = { Text("Vas a eliminar a ${coach.name}. ¿Deseas continuar?") },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        coachViewModel.deleteCoach(coach)
                        coachToDelete = null
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { coachToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }


    coachToDelete?.let { coach ->
        AlertDialog(
            onDismissRequest = { coachToDelete = null },
            title = { Text("¿Eliminar entrenador?") },
            text = { Text("Vas a eliminar a ${coach.name}. ¿Deseas continuar?") },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        coachViewModel.deleteCoach(coach)
                        coachToDelete = null
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { coachToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    errorMessage?.let {
        Text(
            text = it,
            color = Color.Red,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun PlayersGrid(
    players: List<PlayerEntity>,
    teamName: String,
    onInfoClick: (PlayerEntity) -> Unit,
    onDeleteClick: (PlayerEntity) -> Unit
) {
    val sorted = players.sortedBy { it.number }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(140.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sorted) { player ->
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                Card(
                    onClick = { onInfoClick(player) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    border = BorderStroke(1.dp, Color(0xFF00205B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.8f)
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                painter = painterResource(id = TeamsData.getPlayerImageForTeamAndNumber(teamName, player.number)),
                                contentDescription = "Foto",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color(0xFF00205B), CircleShape)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${player.number} - ${player.firstName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF00205B),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = player.position,
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        IconButton(
                            onClick = { onDeleteClick(player) },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoachesList(coaches: List<CoachEntity>, onDeleteClick: (CoachEntity) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(coaches, key = { it.id }) { coach ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { visible = true }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE)),
                    border = BorderStroke(1.dp, Color(0xFF1A73E8)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_jugador),
                            contentDescription = null,
                            tint = Color(0xFF1A73E8),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = coach.name,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF00205B),
                            fontSize = 17.sp,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(onClick = { onDeleteClick(coach) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.Red
                            )
                        }
                    }
                }
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

    var showNameError by remember { mutableStateOf(false) }
    var showLastNameError by remember { mutableStateOf(false) }

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
                    onValueChange = {
                        firstName = it
                        showNameError = false
                    },
                    label = { Text("Nombre") },
                    isError = showNameError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showNameError) {
                    Text("El nombre es obligatorio", color = Color.Red, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        showLastNameError = false
                    },
                    label = { Text("Apellidos") },
                    isError = showLastNameError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showLastNameError) {
                    Text("Los apellidos son obligatorios", color = Color.Red, fontSize = 12.sp)
                }

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
                            var valid = true

                            if (firstName.isBlank()) {
                                showNameError = true
                                valid = false
                            }

                            if (lastName.isBlank()) {
                                showLastNameError = true
                                valid = false
                            }

                            if (valid && number != null) {
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