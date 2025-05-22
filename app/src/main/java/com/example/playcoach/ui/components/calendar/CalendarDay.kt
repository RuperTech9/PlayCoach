package com.example.playcoach.ui.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playcoach.data.entities.EventEntity
import com.example.playcoach.data.entities.MatchdayEntity
import java.time.LocalDate

@Composable
fun CalendarDay(
    date: LocalDate,
    today: LocalDate,
    groupedEvents: Map<LocalDate, List<EventEntity>>,
    groupedMatchdays: Map<LocalDate, List<MatchdayEntity>>,
    onEventClick: (EventEntity) -> Unit,
    onMatchdayClick: (MatchdayEntity) -> Unit,
    onEmptyDayClick: () -> Unit
) {
    val event = groupedEvents[date]?.firstOrNull()
    val matchday = groupedMatchdays[date]?.firstOrNull()
    val backgroundColor = when {
        date == today -> Color(0xFFB3E5FC)
        event != null -> Color(0xFFFFF59D)
        matchday != null -> Color(0xFF7CC77F)
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .border(1.dp, Color(0xFF00205B))
            .background(backgroundColor)
            .clickable {
                when {
                    event != null -> onEventClick(event)
                    matchday != null -> onMatchdayClick(matchday)
                    else -> onEmptyDayClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            date.dayOfMonth.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}