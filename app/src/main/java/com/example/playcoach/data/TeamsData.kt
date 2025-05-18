package com.example.playcoach.data

import com.example.playcoach.R
import com.example.playcoach.data.entities.MatchdayEntity

object TeamsData {

    data class PlayerInfo(
        val number: Int,
        val firstName: String,
        val lastName: String,
        val nickname: String = "",
        val position: String = "Player" // "Goalkeeper" or "Player"
    )

    data class CoachInfo(
        val name: String
    )

    data class TeamInfo(
        val teamName: String,
        val players: List<PlayerInfo> = emptyList(),
        val coaches: List<CoachInfo> = emptyList()
    )

    val teamList = listOf(
        TeamInfo("Chupetines"),
        TeamInfo("Prebenjamín A"),
        TeamInfo("Prebenjamín B"),
        TeamInfo("Benjamín A"),
        TeamInfo("Benjamín B"),
        TeamInfo("Benjamín C"),
        TeamInfo("Alevín A"),
        TeamInfo("Alevín B"),
        TeamInfo("Alevín C"),
        TeamInfo(
            teamName = "Infantil A",
            players = listOf(
                PlayerInfo(13, "Jordan", "Illoh Marimon", "Jordan", "Goalkeeper"),
                PlayerInfo(4, "Martín", "Peña Alonso", "Peña", "Player"),
                PlayerInfo(5, "Iván", "López López", "Iván", "Player"),
                PlayerInfo(17, "Rubén", "Vargas García", "Rubén", "Player"),
                PlayerInfo(33, "Aitor", "Díez García", "Aitor", "Player"),
                PlayerInfo(12, "David", "Camino Moreno", "David", "Player"),
                PlayerInfo(41, "Valentín", "López González", "Valen", "Player"),
                PlayerInfo(10, "Hugo", "Cañeque Lizana", "Cañeque", "Player"),
                PlayerInfo(8,  "Martín", "Godino Perejón", "Martín", "Player"),
                PlayerInfo(6,  "Ibrahim", "Hamdan", "Ibra", "Player"),
                PlayerInfo(14, "Rocío", "Sánchez Martín", "Rocío", "Player"),
                PlayerInfo(34, "Diego", "Fernández de Mera Barroso", "Diego", "Player"),
                PlayerInfo(19, "Iván", "Castro Merchán", "Castro", "Player"),
                PlayerInfo(7,  "Adrián", "Méndez Bañón", "Méndez", "Player"),
                PlayerInfo(11, "Hugo", "Jímenez Moreno", "Jiménez", "Player"),
                PlayerInfo(29, "Marcos Rafael", "Ortega Aquino", "Marcos", "Player"),
                PlayerInfo(9,  "Rodrigo", "Pontes Feito", "Rodri", "Player"),
                PlayerInfo(23, "Nicolás", "Sanz Gámez", "Nico", "Player")
            ),
            coaches = listOf(
                CoachInfo("RUPÉREZ LÓPEZ, ALEJANDRO"),
                CoachInfo("RUPÉREZ LÓPEZ, MARIO")
            )
        ),
        TeamInfo("Infantil B"),
        TeamInfo("Infantil C"),
        TeamInfo("Cadete A"),
        TeamInfo("Cadete B"),
        TeamInfo("Juvenil"),
        TeamInfo("Féminas"),
        TeamInfo("Senior")
    )

    private val imagesInfantilA = mapOf(
        4  to R.drawable.ic_jugador,       // Cambia a tus drawables
        5  to R.drawable.infantil_a_ivan,
        6  to R.drawable.infantil_a_ibra,
        7  to R.drawable.infantil_a_mendez,
        8  to R.drawable.infantil_a_martin,
        9  to R.drawable.infantil_a_rodri,
        10 to R.drawable.infantil_a_caneque,
        11 to R.drawable.infantil_a_jimenez,
        12 to R.drawable.ic_jugador,
        13 to R.drawable.infantil_a_jordan,
        14 to R.drawable.ic_jugador,
        17 to R.drawable.infantil_a_ruben,
        19 to R.drawable.infantil_a_castro,
        23 to R.drawable.infantil_a_nico,
        29 to R.drawable.infantil_a_marcos,
        33 to R.drawable.infantil_a_aitor,
        34 to R.drawable.infantil_a_diego,
        41 to R.drawable.infantil_a_valen
    )

    private val imagesInfantilB = mapOf<Int, Int>()

    fun getPlayerImageForTeamAndNumber(team: String, number: Int): Int {
        return when (team) {
            "Infantil A" -> imagesInfantilA[number] ?: R.drawable.ic_jugador
            "Infantil B" -> imagesInfantilB[number] ?: R.drawable.ic_jugador
            else -> R.drawable.ic_jugador
        }
    }

    private val seasonMatchesInfantilA = listOf(
        MatchdayEntity(
            id = 0,
            matchdayNumber = 1,
            time = "15:00h",
            date = "2024-09-28",
            homeTeam = "Deportivo Yuncos A",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 2,
            time = "11:30h",
            date = "2024-10-05",
            homeTeam = "Infantil A",
            awayTeam = "C.D.E. Amistad Alcorcón B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 3,
            time = "09:30h",
            date = "2024-10-12",
            homeTeam = "A.D.C. Entiergal",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 4,
            time = "11:30h",
            date = "2024-10-19",
            homeTeam = "Infantil A",
            awayTeam = "C.D. Móstoles U.R.J.C. E",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 5,
            time = "09:00h",
            date = "2024-10-26",
            homeTeam = "A.D.N. Boadilla A",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 6,
            time = "11:30h",
            date = "2024-11-02",
            homeTeam = "Infantil A",
            awayTeam = "E.F. Brunete",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 7,
            time = "11:30h",
            date = "2024-11-09",
            homeTeam = "Infantil A",
            awayTeam = "C.D. DV7 Madrid B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 8,
            time = "13:15h",
            date = "2024-11-16",
            homeTeam = "Móstoles C.F. A",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 9,
            time = "11:30h",
            date = "2024-11-23",
            homeTeam = "Infantil A",
            awayTeam = "U.D. Móstoles Balompie A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 10,
            time = "16:00h",
            date = "2024-11-30",
            homeTeam = "C.D. Villa del Prado A",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 11,
            time = "11:30h",
            date = "2024-12-14",
            homeTeam = "Infantil A",
            awayTeam = "C.D. Fuenlabrada Atlantis D",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 12,
            time = "11:00h",
            date = "2025-01-11",
            homeTeam = "A.D. Villaviciosa de Odón B",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 13,
            time = "17:15h",
            date = "2025-01-18",
            homeTeam = "Infantil A",
            awayTeam = "F.S. Navalcarnero A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 14,
            time = "17:00h",
            date = "2025-01-25",
            homeTeam = "C.D. Navalcarnero B",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 15,
            time = "11:30h",
            date = "2025-02-01",
            homeTeam = "Infantil A",
            awayTeam = "E.F. Chapinería",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 16,
            time = "11:30h",
            date = "2025-02-08",
            homeTeam = "Infantil A",
            awayTeam = "Deportivo Yuncos A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 17,
            time = "09:00h",
            date = "2025-02-15",
            homeTeam = "C.D.E. Amistad Alcorcón B",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 18,
            time = "11:30h",
            date = "2025-02-22",
            homeTeam = "Infantil A",
            awayTeam = "A.D.C. Entiergal",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 19,
            time = "13:15h",
            date = "2025-03-01",
            homeTeam = "C.D. Móstoles U.R.J.C. E",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 20,
            time = "11:00h",
            date = "2025-03-08",
            homeTeam = "Infantil A",
            awayTeam = "A.D.N. Boadilla A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 21,
            time = "17:15h",
            date = "2025-03-15",
            homeTeam = "E.F. Brunete",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 22,
            time = "13:00h",
            date = "2025-03-23",
            homeTeam = "C.D. DV7 Madrid B",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 23,
            time = "11:30h",
            date = "2025-03-30",
            homeTeam = "Infantil A",
            awayTeam = "Móstoles C.F. A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 24,
            time = "17:00h",
            date = "2025-04-05",
            homeTeam = "U.D. Móstoles Balompie A",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 25,
            time = "11:30h",
            date = "2025-04-12",
            homeTeam = "Infantil A",
            awayTeam = "C.D. Villa del Prado A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 26,
            time = "09:00h",
            date = "2025-04-26",
            homeTeam = "C.D. Fuenlabrada Atlantis D",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 27,
            time = "11:30h",
            date = "2025-05-10",
            homeTeam = "Infantil A",
            awayTeam = "A.D. Villaviciosa de Odón B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 28,
            time = "14:45",
            date = "2025-05-17",
            homeTeam = "F.S. Navalcarnero A",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 29,
            time = "11:30h",
            date = "2025-05-24",
            homeTeam = "Infantil A",
            awayTeam = "C.D. Navalcarnero B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 30,
            time = "hora sin definir",
            date = "2025-05-31",
            homeTeam = "E.F. Chapinería",
            awayTeam = "Infantil A",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Infantil A"
        )
    )

    private val seasonMatchesInfantilB = emptyList<MatchdayEntity>()

    fun getMatchesForTeam(team: String): List<MatchdayEntity> {
        return when (team) {
            "Infantil A" -> seasonMatchesInfantilA
            "Infantil B" -> seasonMatchesInfantilB
            else -> emptyList()
        }
    }
}