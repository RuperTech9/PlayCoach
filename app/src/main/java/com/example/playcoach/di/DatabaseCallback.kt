package com.example.playcoach.di

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playcoach.data.TeamsData
import com.example.playcoach.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseCallback(
    private val context: Context
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(context)
            val teamDao = database.teamDao()
            val playerDao = database.playerDao()
            val coachDao = database.coachDao()
            val matchdayDao = database.matchdayDao()

            TeamsData.teamList.forEach { teamInfo ->
                teamDao.insertTeam(com.example.playcoach.data.entities.TeamEntity(name = teamInfo.teamName))

                teamInfo.players.forEach { player ->
                    playerDao.insertPlayer(
                        com.example.playcoach.data.entities.PlayerEntity(
                            number = player.number,
                            firstName = player.firstName,
                            lastName = player.lastName,
                            nickname = player.nickname,
                            position = player.position,
                            team = teamInfo.teamName
                        )
                    )
                }

                teamInfo.coaches.forEach { coach ->
                    coachDao.insertCoach(
                        com.example.playcoach.data.entities.CoachEntity(
                            firstName = coach.firstName,
                            lastName = coach.lastName,
                            team = teamInfo.teamName
                        )
                    )
                }

                TeamsData.getMatchesForTeam(teamInfo.teamName).forEach { matchday ->
                    matchdayDao.insertMatchday(matchday)
                }
            }
        }
    }
}
