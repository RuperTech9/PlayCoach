package com.example.playcoach.ui.components.calendar

import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.example.playcoach.data.entities.MatchdayEntity
import com.example.playcoach.data.entities.PlayerEntity
import com.example.playcoach.utils.generateCallUpPdf
import com.example.playcoach.viewmodels.CallUpViewModel

@Composable
fun MatchdayCallUpDialog(
    matchday: MatchdayEntity,
    players: List<PlayerEntity>,
    callUpViewModel: CallUpViewModel,
    onDismiss: () -> Unit
) {
    val callUpState = remember { mutableStateMapOf<Int, Boolean>() }

    LaunchedEffect(matchday.id) {
        callUpViewModel.loadCallUpForMatchday(matchday.id)
    }

    val previousCallUps by callUpViewModel.calledUpPlayers.collectAsState()

    LaunchedEffect(matchday.id, players, previousCallUps) {
        if (players.isNotEmpty()) {
            callUpState.clear()
            players.forEach { player ->
                callUpState[player.number] = previousCallUps.contains(player.number)
            }
        }
    }

    val total = players.size
    val selectedCount = callUpState.values.count { it }
    val notSelected = total - selectedCount

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            tonalElevation = 4.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Convocatoria Jornada ${matchday.matchdayNumber}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF00205B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "🟢 Convocados: $selectedCount   🔴 No convocados: $notSelected",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                val sortedPlayers = players.sortedBy { it.number }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(top = 8.dp)
                ) {
                    items(sortedPlayers.size) { index ->
                        val player = sortedPlayers[index]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Checkbox(
                                checked = callUpState[player.number] == true,
                                onCheckedChange = { selected ->
                                    callUpState[player.number] = selected
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
                        val selectedPlayers = callUpState.filterValues { it }.keys.toList()
                        callUpViewModel.saveCallUps(matchday.id, selectedPlayers)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                ) {
                    Text("Guardar Convocatoria", color = Color.White)
                }

                val context = LocalContext.current

                OutlinedButton(
                    onClick = {
                        val calledUpPlayers = players.filter { player ->
                            callUpState[player.number] == true
                        }

                        val pdf = generateCallUpPdf(context, matchday, calledUpPlayers)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            pdf
                        )

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_TEXT, "📄 Convocados Jornada ${matchday.matchdayNumber}")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        context.startActivity(Intent.createChooser(intent, "Compartir PDF"))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color(0xFF00205B))
                ) {
                    Text("📥 Descargar Convocados PDF", color = Color(0xFF00205B))
                }
            }
        }
    }
}