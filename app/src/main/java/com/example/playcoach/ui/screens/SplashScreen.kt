package com.example.playcoach.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playcoach.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToTeamSelection: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_transition")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset_x"
    )

    val animatedBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF00205B), Color(0xFF004B87), Color(0xFF00205B)),
        start = Offset(offsetX, 0f),
        end = Offset(offsetX + 500f, 1000f)
    )

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scale_logo"
    )

    var navigateNow by remember { mutableStateOf(false) }

    LaunchedEffect(navigateNow) {
        if (navigateNow) {
            delay(150)
            onNavigateToTeamSelection()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_sln),
                contentDescription = "Club Logo",
                modifier = Modifier
                    .size(240.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .clickable {
                        isPressed = true
                        navigateNow = true
                    }
            )

            Text(
                text = "PlayCoach",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFDC801),
                letterSpacing = 1.2.sp
            )

            Text(
                text = "Toca el escudo para empezar",
                fontSize = 16.sp,
                color = Color(0xFFD1E8FF)
            )
        }
    }
}