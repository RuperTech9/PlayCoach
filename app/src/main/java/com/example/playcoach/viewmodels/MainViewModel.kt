package com.example.playcoach.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val selectedTeamFlow: StateFlow<String?> = savedStateHandle
        .getStateFlow("selectedTeam", null)

    var selectedTeam: String?
        get() = selectedTeamFlow.value
        set(value) {
            savedStateHandle["selectedTeam"] = value
        }

    val selectedMatchdayIdFlow: StateFlow<Int?> = savedStateHandle
        .getStateFlow("selectedMatchdayId", null)

    var selectedMatchdayId: Int?
        get() = selectedMatchdayIdFlow.value
        set(value) {
            savedStateHandle["selectedMatchdayId"] = value
        }

    val selectedModeFlow: StateFlow<String> = savedStateHandle
        .getStateFlow("selectedMode", "estadísticas")

    var selectedMode: String
        get() = selectedModeFlow.value
        set(value) {
            savedStateHandle["selectedMode"] = value
        }
}
