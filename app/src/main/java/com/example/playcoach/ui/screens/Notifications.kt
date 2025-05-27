package com.example.playcoach.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playcoach.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notifications(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00205B),
                    titleContentColor = Color(0xFFFDF3D0)
                ),
                title = { Text("Notificaciones", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cerrar),
                            contentDescription = "Cerrar",
                            tint = Color(0xFFFDF3D0)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notification settings */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_ajustes),
                            contentDescription = "Configuración",
                            tint = Color(0xFFFDF3D0)
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(
                listOf(
                    "Todavía falta gente para que el equipo esté completo, ¡así que mándales un recordatorio!",
                    "El Capitán acaba de unirse a tu equipo. ¡Dale la bienvenida!",
                    "Bienvenido a tu equipo."
                )
            ) { notification ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = notification, fontSize = 16.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Hace 1 día", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    Notifications(onNavigateBack = {})
}
