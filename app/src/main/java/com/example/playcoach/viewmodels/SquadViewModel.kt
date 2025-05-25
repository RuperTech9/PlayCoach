package com.example.playcoach.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.playcoach.data.entities.CoachEntity
import com.example.playcoach.data.entities.PlayerEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SquadViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var showCoachDialog: Boolean
        get() = savedStateHandle["showCoachDialog"] ?: false
        set(value) { savedStateHandle["showCoachDialog"] = value }

    var showPlayerDialog: Boolean
        get() = savedStateHandle["showPlayerDialog"] ?: false
        set(value) { savedStateHandle["showPlayerDialog"] = value }

    var errorMessage: String?
        get() = savedStateHandle["errorMessage"]
        set(value) { savedStateHandle["errorMessage"] = value }

    var coachToDelete: CoachEntity?
        get() = savedStateHandle["coachToDelete"]
        set(value) { savedStateHandle["coachToDelete"] = value }

    var playerToDelete: PlayerEntity?
        get() = savedStateHandle["playerToDelete"]
        set(value) { savedStateHandle["playerToDelete"] = value }
}