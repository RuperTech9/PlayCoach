package com.example.playcoach.ui.components.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.playcoach.data.entities.MatchdayEntity
import com.example.playcoach.viewmodels.CallUpViewModel
import com.example.playcoach.viewmodels.PlayerViewModel

@Composable
fun MatchdayBottomCard(
    matchdays: List<MatchdayEntity>,
    visibleMatchdayIndex: Int,
    onIndexChange: (Int) -> Unit,
    onMatchdayClick: (MatchdayEntity) -> Unit,
    initialVisibleIndex: Int,
    playerViewModel: PlayerViewModel,
    callUpViewModel: CallUpViewModel,
    modifier: Modifier = Modifier
) {
    val matchday = matchdays.getOrNull(visibleMatchdayIndex) ?: return

    // Cargar convocatoria cuando cambia el partido
    LaunchedEffect(matchday.id) {
        callUpViewModel.loadCallUpForMatchday(matchday.id)
    }

    val callUpState by callUpViewModel
        .getCallUpUiState(playerViewModel.players)
        .collectAsState()

    val isPlayed = matchday.played
    val isLoading = callUpState.players.isEmpty() || callUpState.isLoading
    val hasCallUp = callUpState.calledUp.isNotEmpty()
    val totalPlayers = callUpState.players.size

    val callUpText = when {
        isLoading -> " "
        hasCallUp -> "✅ Convocatoria hecha (${callUpState.calledUp.size}/$totalPlayers)"
        else -> "❌ Sin convocatoria"
    }

    Column(modifier = modifier.padding(bottom = 5.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { if (visibleMatchdayIndex > 0) onIndexChange(visibleMatchdayIndex - 1) },
                enabled = visibleMatchdayIndex > 0,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Anterior")
            }

            Card(
                modifier = Modifier
                    .weight(6f)
                    .padding(horizontal = 4.dp)
                    .heightIn(max = 250.dp)
                    .clickable { onMatchdayClick(matchday) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                border = BorderStroke(1.dp, Color(0xFF00205B)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                val isNext = visibleMatchdayIndex == initialVisibleIndex

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Cabecera de jornada
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Jornada ${matchday.matchdayNumber}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF00205B)
                        )

                        if (isNext) {
                            Surface(
                                color = Color(0xFF4CAF50),
                                shape = RoundedCornerShape(8.dp),
                                tonalElevation = 2.dp,
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    text = "Próxima jornada",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Fecha y hora
                    Text(
                        text = "📅 ${matchday.date} — ${matchday.time}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    // Equipos
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = matchday.homeTeam,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            color = Color(0xFF00205B)
                        )
                        Text(text = "vs", fontSize = 14.sp, color = Color.Gray)
                        Text(
                            text = matchday.awayTeam,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            color = Color(0xFF00205B)
                        )
                    }

                    // Resultado si ya se jugó
                    if (isPlayed && (matchday.homeGoals >= 0 && matchday.awayGoals >= 0)) {
                        val isHome = matchday.homeTeam == matchday.team
                        val goalsFor = if (isHome) matchday.homeGoals else matchday.awayGoals
                        val goalsAgainst = if (isHome) matchday.awayGoals else matchday.homeGoals

                        val resultColor = when {
                            goalsFor > goalsAgainst -> Color(0xFF388E3C) // Verde
                            goalsFor == goalsAgainst -> Color(0xFFFFC107) // Amarillo
                            else -> Color(0xFFD32F2F) // Rojo
                        }

                        Text(
                            text = "${matchday.homeGoals} - ${matchday.awayGoals}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = resultColor
                        )
                    }

                    if (!isPlayed) {
                        if (isLoading) {
                            Spacer(modifier = Modifier.height(8.dp))
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = callUpText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (hasCallUp) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = { if (visibleMatchdayIndex < matchdays.lastIndex) onIndexChange(visibleMatchdayIndex + 1) },
                enabled = visibleMatchdayIndex < matchdays.lastIndex,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Siguiente")
            }
        }
    }
}