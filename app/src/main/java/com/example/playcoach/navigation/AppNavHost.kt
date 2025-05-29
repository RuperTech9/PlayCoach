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
                teamName = selectedTeam,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSelectTeam = { navController.navigate("team_selection") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") }
            )
        }

        composable("messages") {
            Messages(
                teamName = selectedTeam,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSelectTeam = { navController.navigate("team_selection") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") }
            )
        }

        composable("squad") {
            Squad(
                teamName = selectedTeam,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSelectTeam = { navController.navigate("team_selection") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                onNavigateToPlayerDetails = { player ->
                    navController.navigate("player_details/${player.number}")
                }
            )
        }

        composable("stats") {
            Stats(

                teamName = selectedTeam,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSelectTeam = { navController.navigate("team_selection") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                onNavigateToMatchdays = { navController.navigate("matches") },
                onNavigateToTeamStats = { navController.navigate("team_stats") },
                onNavigateToPlayerStats = { navController.navigate("player_stats") },
                onNavigateToPlayerAttendance = { navController.navigate("player_attendance") }
            )
        }

        composable("matches") {
            Matches (
                teamName = selectedTeam,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSelectTeam = { navController.navigate("team_selection") },
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
                teamName = selectedTeam,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSelectTeam = { navController.navigate("team_selection") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                onNavigateToMatchDetail = { matchdayId ->
                    navController.navigate("match_details/$matchdayId")
                }
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
                teamName = selectedTeam,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSelectTeam = { navController.navigate("team_selection") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                onNavigateToPlayerDetail = { player ->
                    navController.navigate("player_details/${player.number}")
                }
            )
        }

        composable("player_details/{playerId}") { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId")?.toIntOrNull() ?: -1

            PlayerDetail(
                playerId = playerId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMatchDetail = { matchdayId ->
                    navController.navigate("match_details/$matchdayId")
                }
            )
        }

        composable("player_attendance") {
            PlayersAbsence(
                teamName = selectedTeam,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSelectTeam = { navController.navigate("team_selection") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") }
            )
        }

        composable("tactical_board") {
            TacticalBoard(
                teamName = selectedTeam,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSelectTeam = { navController.navigate("team_selection") },
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
                teamName = selectedTeam,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSelectTeam = { navController.navigate("team_selection") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") }
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
