package com.example.playcoach.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playcoach.ui.screens.*
import com.example.playcoach.viewmodels.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    val mainViewModel: MainViewModel = hiltViewModel()
    val selectedTeam by mainViewModel.selectedTeamFlow.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = modifier
    ) {
        composable("welcome") {
            SplashScreen (
                onNavigateToTeamSelection = {
                    navController.navigate("team_selection")
                }
            )
        }

        composable("team_selection") {
            SelectTeam(
                onTeamSelected = { team ->
                    mainViewModel.selectedTeam = team
                    navController.navigate("calendar")
                },
                onAddTeam = { navController.navigate("add_team") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("add_team") {
            AddTeam(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable("calendar") {
            Calendar(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                teamName = selectedTeam
            )
        }

        composable("messages") {
            Messages(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                teamName = selectedTeam
            )
        }

        composable("squad") {
            Squad(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                onNavigateToPlayerDetails = { player ->
                    navController.navigate("player_details/${player.number}")
                },
                teamName = selectedTeam
            )
        }

        composable("stats") {
            Stats(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                onNavigateToTeamStats = { navController.navigate("team_stats") },
                onNavigateToPlayerStats = { navController.navigate("player_stats") },
                onNavigateToPlayerAttendance = { navController.navigate("player_attendance") },
                onNavigateToMatchdays = { navController.navigate("matches") },
                teamName = selectedTeam
            )
        }

        composable("matches") {
            Matches (
                teamName = selectedTeam,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") }
            )
        }

        composable("team_stats") {
            TeamStats(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                onNavigateToMatchDetail = { matchdayId ->
                    navController.navigate("match_details/$matchdayId")
                },
                teamName = selectedTeam
            )
        }

        composable("match_details/{matchdayId}") { backStackEntry ->
            val matchdayId = backStackEntry.arguments?.getString("matchdayId")?.toIntOrNull() ?: -1

            MatchDetail(
                matchdayId = matchdayId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("player_stats") {
            PlayersStats(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                onNavigateToPlayerDetail = { player ->
                    navController.navigate("player_details/${player.number}")
                },
                teamName = selectedTeam
            )
        }

        composable("player_details/{playerId}") { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId")?.toIntOrNull() ?: -1

            PlayerDetail(
                playerId = playerId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("player_attendance") {
            PlayersAbsence(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                teamName = selectedTeam
            )
        }

        composable("tactical_board") {
            TacticalBoard(
                teamName = selectedTeam,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") }
            )
        }

        composable("others") {
            Others(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                teamName = selectedTeam
            )
        }

        composable("notifications") {
            Notifications(onNavigateBack = { navController.popBackStack() })
        }

        composable("profile") {
            Profile(onNavigateBack = { navController.popBackStack() })
        }
    }
}
