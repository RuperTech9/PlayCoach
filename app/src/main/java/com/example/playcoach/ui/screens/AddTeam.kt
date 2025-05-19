package com.example.playcoach.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.playcoach.viewmodels.TeamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTeam(
    onNavigateBack: () -> Unit,
) {
    val teamViewModel: TeamViewModel = hiltViewModel()

    val newTeamName by teamViewModel.newTeamName.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Añadir Equipo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = newTeamName,
                onValueChange = { teamViewModel.updateNewTeamName(it) },
                label = { Text("Nombre del equipo") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    teamViewModel.insertTeam()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = newTeamName.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }
}
