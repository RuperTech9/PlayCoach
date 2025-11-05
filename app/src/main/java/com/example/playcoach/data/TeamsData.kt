package com.example.playcoach.data

import com.example.playcoach.R
import com.example.playcoach.data.entities.MatchdayEntity

object TeamsData {

    data class PlayerInfo(
        val number: Int,
        val firstName: String,
        val lastName: String,
        val nickname: String = "",
        val position: String = "Jugador" // "Portero" o "Jugador"
    )

    data class CoachInfo(
        val firstName: String,
        val lastName: String
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
        TeamInfo("Infantil A"),
        TeamInfo("Infantil B"),
        TeamInfo("Cadete A"),
        TeamInfo("Cadete B"),
        TeamInfo("Juvenil A"),
        TeamInfo(
            teamName = "Juvenil B",
            players = listOf(
                // PORTERO
                PlayerInfo(12, "Pedro", "Martínez Lardies", "Pedro", "Portero"),

                // DEFENSAS
                PlayerInfo(33, "Sergio", "Menéndez Abad", "Bullito", "Jugador"),
                PlayerInfo(30, "Gonzalo", "Lorite Regalado", "Gonzalo", "Jugador"),
                PlayerInfo(24, "Cristobal", "Salazar", "Cristobal", "Jugador"),
                PlayerInfo(29, "Jaime", "Godino Perejón", "Jaime", "Jugador"),
                PlayerInfo(53, "Gabriel", "Benites Miranda", "Gabo", "Jugador"),

                // MEDIOS
                PlayerInfo(45, "Marwan", "Berkani el Khattouti", "Marwan", "Jugador"),
                PlayerInfo(46, "Aitor", "Piqueras Padilla", "Piqueras", "Jugador"),
                PlayerInfo(49,  "Álvaro", "Gallardo Pérez", "Gallardo", "Jugador"),
                PlayerInfo(38,  "Samuel", "Luján Rodríguez", "Luján", "Jugador"),

                // BANDAS
                PlayerInfo(44, "Diego", "Quevedo Calderón", "Quevedo", "Jugador"),
                PlayerInfo(23, "Youssef", "Rabboun el Gharib", "Youssef", "Jugador"),
                PlayerInfo(34, "Samuel", "Menor Senet", "Samu", "Jugador"),
                PlayerInfo(43,  "Álvaro", "Mata Salamero", "Álvaro", "Jugador"),

                // DELANTEROS
                PlayerInfo(55, "Jose Pablo", "Revuelta Oviawe", "Pablo", "Jugador"),
                PlayerInfo(31, "Martín", "Garde Rodríguez", "Martín", "Jugador"),
                PlayerInfo(36,  "Jorge", "Gorrón Robledillo", "Jorge", "Jugador"),
                PlayerInfo(28,  "Neithan", "Alonso Nieves", "Neithan", "Jugador"),
                PlayerInfo(32,  "Iker", "Garrido Cabrera", "Iker", "Jugador"),
                PlayerInfo(27,  "Daniel", "Cano Domínguez", "Cano", "Jugador"),
                PlayerInfo(37,  "Izan", "el Jazzar Company", "Izan", "Jugador"),
                PlayerInfo(39,  "Cristian", "Aristizabal", "Cristian", "Jugador"),
                PlayerInfo(35, "Juan José", "___", "Juanjo", "Jugador")
            ),
            coaches = listOf(
                CoachInfo("ALEJANDRO", "RUPÉREZ LÓPEZ"),
                CoachInfo("MARIO", "RUPÉREZ LÓPEZ")
            )
        ),
        TeamInfo("Féminas"),
        TeamInfo("Senior")
    )

    private val imagesJuvenilB = mapOf(
        12  to R.drawable.juvenil_b_pedro,
        23  to R.drawable.juvenil_b_youssef,
        24  to R.drawable.juvenil_b_cristobal,
        27  to R.drawable.juvenil_b_cano,
        28  to R.drawable.ic_jugador,
        29  to R.drawable.ic_jugador,
        30  to R.drawable.juvenil_b_gonzalo,
        31  to R.drawable.juvenil_b_martin,
        32 to R.drawable.ic_jugador,
        33 to R.drawable.juvenil_b_sergio,
        34 to R.drawable.ic_jugador,
        35 to R.drawable.juvenil_b_juanjo,
        36 to R.drawable.juvenil_b_jorge,
        37 to R.drawable.juvenil_b_izan,
        38 to R.drawable.juvenil_b_lujan,
        39 to R.drawable.ic_jugador,
        43 to R.drawable.juvenil_b_alvaro,
        44 to R.drawable.juvenil_b_quevedo,
        45 to R.drawable.ic_jugador,
        46 to R.drawable.juvenil_b_piqueras,
        49 to R.drawable.juvenil_b_gallardo,
        53 to R.drawable.juvenil_b_gabri,
        55 to R.drawable.juvenil_b_pablo
    )
    private val coachImagesJuvenilB = mapOf(
        "ALEJANDRO RUPÉREZ LÓPEZ" to R.drawable.juvenil_b_primer_entrenador,
        "MARIO RUPÉREZ LÓPEZ" to R.drawable.juvenil_b_segundo_entrenador
    )

    fun getCoachImageForTeamAndName(team: String, fullName: String): Int {
        return when (team) {
            "Juvenil B" -> coachImagesJuvenilB[fullName] ?: R.drawable.ic_jugador
            else -> R.drawable.ic_jugador
        }
    }

    private val imagesInfantilB = mapOf<Int, Int>()

    fun getPlayerImageForTeamAndNumber(team: String, number: Int): Int {
        return when (team) {
            "Juvenil B" -> imagesJuvenilB[number] ?: R.drawable.ic_jugador
            "Infantil B" -> imagesInfantilB[number] ?: R.drawable.ic_jugador
            else -> R.drawable.ic_jugador
        }
    }

    private val seasonMatchesJuvenilB = listOf(
        MatchdayEntity(
            id = 0,
            matchdayNumber = 1,
            time = "17:00h",
            date = "2025-10-11",
            homeTeam = "Juvenil B",
            awayTeam = "Robledo C.F.'A'",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 2,
            time = "16:00h",
            date = "2025-10-19",
            homeTeam = "C.D. Nuevo Boadilla 'I'",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 3,
            time = "17:00h",
            date = "2025-10-26",
            homeTeam = "Juvenil B",
            awayTeam = "C.D.A. Navalcarnero 'D'",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 4,
            time = "13:05h",
            date = "2025-11-02",
            homeTeam = "C.D.E. Gopad U19 'B'",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 5,
            time = "00:00h",
            date = "2025-11-09",
            homeTeam = "A.D. Cadalso",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 6,
            time = "17:00h",
            date = "2025-11-15",
            homeTeam = "Juvenil B",
            awayTeam = "CDB Union Deportiva Arganzuela 'F'",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 7,
            time = "16:00hh",
            date = "2025-11-22",
            homeTeam = "C.D. Villa del Prado 'B'",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 8,
            time = "17:00hh",
            date = "2025-11-29",
            homeTeam = "Juvenil B",
            awayTeam = "C.D. Villamantilla",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 9,
            time = "00:00h",
            date = "2025-12-14",
            homeTeam = "C.D. Municipal Arroyomolinos 'D'",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 10,
            time = "17:00h",
            date = "2025-12-20",
            homeTeam = "Juvenil B",
            awayTeam = "Atlético Valdeiglesias 'B'",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 11,
            time = "00:00hh",
            date = "2026-01-11",
            homeTeam = "C.D. El Alamo 'B'",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 12,
            time = "00:00h",
            date = "2026-01-18",
            homeTeam = "Juvenil B",
            awayTeam = "E.M.F. Chapinería",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 13,
            time = "17:15h",
            date = "2026-01-25",
            homeTeam = "E.F.M.O. Boadilla 'E'",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 14,
            time = "16:00h",
            date = "2026-01-31",
            homeTeam = "Robledo C.F.'A'",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 15,
            time = "17:00h",
            date = "2026-02-08",
            homeTeam = "",
            awayTeam = "C.D. Nuevo Boadilla 'I'",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 16,
            time = "17:00h",
            date = "2026-02-15",
            homeTeam = "C.D.A. Navalcarnero 'D'",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 17,
            time = "17:00h",
            date = "2026-02-22",
            homeTeam = "Juvenil B",
            awayTeam = "C.D.E. Gopad U19 'B'",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 18,
            time = "17:00h",
            date = "2026-03-01",
            homeTeam = "Juvenil B",
            awayTeam = "A.D. Cadalso",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 19,
            time = "00:00h",
            date = "2026-03-08",
            homeTeam = "CDB Union Deportiva Arganzuela 'F'",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 20,
            time = "17:00h",
            date = "2026-03-15",
            homeTeam = "Juvenil B",
            awayTeam = "C.D. Villa del Prado 'B'",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 21,
            time = "00:00h",
            date = "2026-03-22",
            homeTeam = "C.D. Villamantilla",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 22,
            time = "17:00h",
            date = "2026-04-12",
            homeTeam = "Juvenil B",
            awayTeam = "C.D. Municipal Arroyomolinos 'D'",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 23,
            time = "00:00h",
            date = "2026-04-19",
            homeTeam = "Atlético Valdeiglesias 'B'",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 24,
            time = "17:00h",
            date = "2026-04-26",
            homeTeam = "Juvenil B",
            awayTeam = "C.D. El Alamo 'B'",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 25,
            time = "00:00h",
            date = "2026-05-10",
            homeTeam = "E.M.F. Chapinería",
            awayTeam = "Juvenil B",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        ),
        MatchdayEntity(
            id = 0,
            matchdayNumber = 26,
            time = "17:00h",
            date = "2026-05-17",
            homeTeam = "Juvenil B",
            awayTeam = "E.F.M.O. Boadilla 'E'",
            homeGoals = 0,
            awayGoals = 0,
            summary = "",
            team = "Juvenil B"
        )
    )

    private val seasonMatchesInfantilB = emptyList<MatchdayEntity>()

    fun getMatchesForTeam(team: String): List<MatchdayEntity> {
        return when (team) {
            "Juvenil B" -> seasonMatchesJuvenilB
            "Infantil B" -> seasonMatchesInfantilB
            else -> emptyList()
        }
    }
}
