package com.example.playcoach.ui.components.calendar

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.data.entities.MatchdayEntity
import com.example.playcoach.utils.generateCallUpPdf
import com.example.playcoach.viewmodels.CallUpViewModel
import com.example.playcoach.viewmodels.PlayerViewModel

@Composable
fun MatchdayDetailsDialog(
    matchday: MatchdayEntity,
    onOpenCallUp: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val playersViewModel: PlayerViewModel = hiltViewModel()
    val callUpViewModel: CallUpViewModel = hiltViewModel()
    val players = playersViewModel.players.collectAsState().value
    val calledUp = callUpViewModel.calledUpPlayers.collectAsState().value

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            tonalElevation = 4.dp,
            border = BorderStroke(1.dp, Color(0xFF00205B)),
            modifier = Modifier.padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "⚽ Detalles del Partido",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF00205B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                listOf(
                    "🗓 Jornada: " to "${matchday.matchdayNumber}",
                    "📆 Fecha: " to matchday.date,
                    "📝 Hora: " to matchday.time,
                    "🏠 Local: " to matchday.homeTeam,
                    "🚩 Visitante: " to matchday.awayTeam
                ).forEach { (label, value) ->
                    Text(buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF00205B))) {
                            append(label)
                        }
                        append(value)
                    }, fontSize = 16.sp)
                }

                if (!matchday.played) {
                    Button(
                        onClick = onOpenCallUp,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                    ) {
                        Text("Gestionar Convocatoria", color = Color.White)
                    }
                } else {
                    val resultText = "${matchday.homeGoals} - ${matchday.awayGoals}"
                    val resultColor = when {
                        (matchday.team == matchday.homeTeam && matchday.homeGoals > matchday.awayGoals) ||
                                (matchday.team == matchday.awayTeam && matchday.awayGoals > matchday.homeGoals) -> Color(0xFF388E3C) // Verde

                        matchday.homeGoals == matchday.awayGoals -> Color(0xFFFFC107) // Amarillo

                        else -> Color(0xFFD32F2F) // Rojo
                    }

                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF00205B))) {
                                append("🔢 Resultado: ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = resultColor)) {
                                append(resultText)
                            }
                        },
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            val calledUpPlayers = players.filter { player ->
                                calledUp.contains(player.number)
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00205B))
                    ) {
                        Text("📥 Descargar Convocatoria PDF", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}