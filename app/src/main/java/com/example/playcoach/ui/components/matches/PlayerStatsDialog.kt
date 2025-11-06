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
import androidx.compose.runtime.MutableState
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

    val (goalsError, assistsError, yellowError, redError, minutesError) = remember {
        listOf(
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false)
        )
    }

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

    fun validateInt(input: String): Boolean = input.toIntOrNull() != null
    fun validateMinutes(input: String): Boolean = input.toIntOrNull()?.let { it in 0..90 } ?: false

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

                @Composable
                fun OutlinedValidatedField(value: String, onChange: (String) -> Unit, label: String, isError: MutableState<Boolean>, validator: (String) -> Boolean, errorMsg: String) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            onChange(it)
                            isError.value = !validator(it)
                        },
                        label = { Text(label) },
                        isError = isError.value,
                        supportingText = { if (isError.value) Text(errorMsg, color = Color.Red) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedValidatedField(goalsText, { goalsText = it }, "Goles", goalsError, ::validateInt, "Solo números enteros")
                OutlinedValidatedField(assistsText, { assistsText = it }, "Asistencias", assistsError, ::validateInt, "Solo números enteros")
                OutlinedValidatedField(yellowCardsText, { yellowCardsText = it }, "Tarjetas Amarillas", yellowError, ::validateInt, "Solo números enteros")
                OutlinedValidatedField(redCardsText, { redCardsText = it }, "Tarjetas Rojas", redError, ::validateInt, "Solo números enteros")
                OutlinedValidatedField(minutesText, { minutesText = it }, "Minutos Jugados", minutesError, ::validateMinutes, "Debe estar entre 0 y 90")

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
                                goalsText.toInt(),
                                assistsText.toInt(),
                                yellowCardsText.toInt(),
                                redCardsText.toInt(),
                                minutesText.toInt(),
                                wasStarter
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B)),
                        enabled = listOf(goalsError, assistsError, yellowError, redError, minutesError).all { !it.value } &&
                                listOf(goalsText, assistsText, yellowCardsText, redCardsText, minutesText).all { it.isNotBlank() }
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