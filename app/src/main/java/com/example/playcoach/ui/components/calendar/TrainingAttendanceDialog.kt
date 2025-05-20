package com.example.playcoach.ui.components.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.playcoach.viewmodels.AbsenceViewModel
import com.example.playcoach.viewmodels.PlayerViewModel

@Composable
fun TrainingAttendanceDialog(
    date: String,
    team: String,
    playerViewModel: PlayerViewModel,
    absenceViewModel: AbsenceViewModel,
    onDismiss: () -> Unit
) {
    val playerList by playerViewModel.players.collectAsState()
    val attendanceByDate by absenceViewModel.attendanceByDate.collectAsState()

    LaunchedEffect(team) {
        playerViewModel.loadPlayersByTeam(team)
        absenceViewModel.selectDate(date)
    }

    val attendanceMap = attendanceByDate.associateBy { it.playerId }
    val selectedMap = remember(date) { mutableStateMapOf<Int, Boolean>() }

    LaunchedEffect(date, playerList, attendanceMap) {
        selectedMap.clear()
        playerList.forEach { player ->
            val attendance = attendanceMap[player.id]
            selectedMap[player.id] = attendance?.present ?: true
        }
    }

    val total = playerList.size
    val present = selectedMap.values.count { it }
    val absent = total - present

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            tonalElevation = 4.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Asistencia del $date",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF00205B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "🟢 Asisten: $present   🔴 Faltan: $absent",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                val sortedPlayers = playerList.sortedBy { it.number }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(top = 8.dp)
                ) {
                    items(sortedPlayers.size) { index ->
                        val player = sortedPlayers[index]
                        val isPresent = selectedMap[player.id] == true

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Checkbox(
                                checked = isPresent,
                                onCheckedChange = { selected ->
                                    selectedMap[player.id] = selected
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF4CAF50),
                                    uncheckedColor = Color(0xFFF44336)
                                )
                            )
                            Text(
                                text = "${player.number} - ${player.firstName}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val presentIds = selectedMap.filterValues { it }.keys.toList()
                        val allPlayerIds = playerList.map { it.id }

                        absenceViewModel.saveFullAttendance(
                            date = date,
                            allPlayers = allPlayerIds,
                            presentPlayers = presentIds
                        )
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                ) {
                    Text("Guardar", color = Color.White)
                }
            }
        }
    }
}