package com.example.playcoach.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playcoach.ui.screens.*
import com.example.playcoach.viewmodels.*

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    val selectedTeam = remember { mutableStateOf<String?>(null) }

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
            val teamViewModel: TeamViewModel = viewModel()
            val matchdayViewModel: MatchdayViewModel = viewModel()
            val eventViewModel: EventViewModel = viewModel()
            SelectTeam(
                onTeamSelected = { team ->
                    selectedTeam.value = team
                    navController.navigate("calendar")
                },
                onAddTeam = { navController.navigate("add_team") },
                onNavigateBack = { navController.popBackStack() },
                teamViewModel = teamViewModel,
                matchdayViewModel = matchdayViewModel,
                eventViewModel = eventViewModel
            )
        }

        composable("add_team") {
            val teamViewModel: TeamViewModel = viewModel()
            AddTeam(
                onNavigateBack = { navController.popBackStack() },
                teamViewModel = teamViewModel
            )
        }

        composable("calendar") {
            val eventViewModel: EventViewModel = viewModel()
            val matchdayViewModel: MatchdayViewModel = viewModel()

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
                teamName = selectedTeam.value,
                eventViewModel = eventViewModel,
                matchdayViewModel = matchdayViewModel
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
                teamName = selectedTeam.value
            )
        }

        composable("squad") {
            val playerViewModel: PlayerViewModel = viewModel()
            val coachViewModel: CoachViewModel = viewModel()

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
                teamName = selectedTeam.value,
                playerViewModel = playerViewModel,
                coachViewModel = coachViewModel
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
                teamName = selectedTeam.value
            )
        }

        composable("matches") {
            val matchdayViewModel: MatchdayViewModel = viewModel()
            val playerStatViewModel: PlayerStatViewModel = viewModel()
            val playerViewModel: PlayerViewModel = viewModel()
            val callupViewModel: CallUpViewModel = viewModel()
            val teamViewModel: TeamStatsViewModel = viewModel()

            Matches (
                matchdayViewModel = matchdayViewModel,
                playerStatViewModel = playerStatViewModel,
                playerViewModel = playerViewModel,
                callupViewModel = callupViewModel,
                teamViewModel = teamViewModel,
                teamName = selectedTeam.value,
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
            val teamViewModel: TeamStatsViewModel = viewModel()
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
                teamName = selectedTeam.value,
                teamViewModel = teamViewModel
            )
        }

        composable("match_details/{matchdayId}") { backStackEntry ->
            val matchdayId = backStackEntry.arguments?.getString("matchdayId")?.toIntOrNull() ?: -1
            val matchdayViewModel: MatchdayViewModel = viewModel()

            MatchDetail(
                matchdayId = matchdayId,
                matchdayViewModel = matchdayViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("player_stats") {
            val playerStatViewModel: PlayerStatViewModel = viewModel()
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
                teamName = selectedTeam.value,
                playerStatViewModel = playerStatViewModel
            )
        }

        composable("player_details/{playerId}") { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId")?.toIntOrNull() ?: -1
            val viewModel: PlayerDetailViewModel = viewModel()
            val absenceViewModel: AbsenceViewModel = viewModel()

            PlayerDetail(
                playerId = playerId,
                viewModel = viewModel,
                absenceViewModel = absenceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("player_attendance") {
            val playerViewModel: PlayerViewModel = viewModel()
            val eventViewModel: EventViewModel = viewModel()
            val absenceViewModel: AbsenceViewModel = viewModel()

            PlayersAttendance(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToCalendar = { navController.navigate("calendar") },
                onNavigateToMessages = { navController.navigate("messages") },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToFormations = { navController.navigate("tactical_board") },
                onNavigateToOthers = { navController.navigate("others") },
                teamName = selectedTeam.value,
                playerViewModel = playerViewModel,
                eventViewModel = eventViewModel,
                absenceViewModel = absenceViewModel
            )
        }

        composable("tactical_board") {
            val formationViewModel: FormationViewModel = viewModel()

            TacticalBoard(
                teamName = selectedTeam.value,
                viewModel = formationViewModel,
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
                teamName = selectedTeam.value
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
