package com.example.playcoach.ui.components.matches

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.playcoach.data.entities.PlayerEntity
import com.example.playcoach.viewmodels.PlayerStatViewModel
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun PlayerStatsDialog(
    player: PlayerEntity,
    matchdayId: Int,
    onSave: (goals: Int, assists: Int, yellowCards: Int, redCards: Int, minutes: Int, wasStarter: Boolean) -> Unit,
    onDismiss: () -> Unit,
    playerStatsViewModel: PlayerStatViewModel
) {
    var goalsText by remember { mutableStateOf("") }
    var assistsText by remember { mutableStateOf("") }
    var yellowCardsText by remember { mutableStateOf("") }
    var redCardsText by remember { mutableStateOf("") }
    var minutesText by remember { mutableStateOf("") }
    var wasStarter by remember { mutableStateOf(false) }

    val currentPlayer = rememberUpdatedState(player)
    val currentMatchdayId = rememberUpdatedState(matchdayId)

    LaunchedEffect(currentPlayer.value, currentMatchdayId.value) {
        playerStatsViewModel
            .getPlayerStat(currentPlayer.value.number, currentMatchdayId.value)
            .firstOrNull()
            ?.let {
                goalsText = it.goals.toString()
                assistsText = it.assists.toString()
                yellowCardsText = it.yellowCards.toString()
                redCardsText = it.redCards.toString()
                minutesText = it.minutesPlayed.toString()
                wasStarter = it.wasStarter
            }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, Color(0xFF00205B)),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "📋 Estadística para ${player.number} - ${player.firstName}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00205B)
                )

                OutlinedTextField(
                    value = goalsText,
                    onValueChange = { goalsText = it },
                    label = { Text("Goles") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = assistsText,
                    onValueChange = { assistsText = it },
                    label = { Text("Asistencias") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = yellowCardsText,
                    onValueChange = { yellowCardsText = it },
                    label = { Text("Tarjetas Amarillas") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = redCardsText,
                    onValueChange = { redCardsText = it },
                    label = { Text("Tarjetas Rojas") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { minutesText = it },
                    label = { Text("Minutos Jugados") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿Titular?", modifier = Modifier.weight(1f))
                    Switch(
                        checked = wasStarter,
                        onCheckedChange = { wasStarter = it }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onSave(
                                goalsText.toIntOrNull() ?: 0,
                                assistsText.toIntOrNull() ?: 0,
                                yellowCardsText.toIntOrNull() ?: 0,
                                redCardsText.toIntOrNull() ?: 0,
                                minutesText.toIntOrNull() ?: 0,
                                wasStarter
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                    ) {
                        Text("Guardar", color = Color.White)
                    }
                    OutlinedButton(
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