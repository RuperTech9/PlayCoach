package com.example.playcoach.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playcoach.data.TeamsData
import com.example.playcoach.data.daos.*
import com.example.playcoach.data.entities.*
import com.example.playcoach.data.repositories.*
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

@Database(
    entities = [
        PlayerEntity::class,
        EventEntity::class,
        MatchdayEntity::class,
        PlayerStatEntity::class,
        AbsenceEntity::class,
        TeamEntity::class,
        CoachEntity::class,
        CallUpEntity::class,
        FormationEntity::class,
        PlayerPositionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun eventDao(): EventDao
    abstract fun matchdayDao(): MatchdayDao
    abstract fun playerStatsDao(): PlayerStatDao
    abstract fun absenceDao(): AbsenceDao
    abstract fun teamDao(): TeamDao
    abstract fun coachDao(): CoachDao
    abstract fun callUpDao(): CallUpDao
    abstract fun formationDao(): FormationDao
    abstract fun playerPositionDao(): PlayerPositionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "playcoach_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Preload data in a background thread
                            Executors.newSingleThreadExecutor().execute {
                                runBlocking {
                                    val database = getDatabase(context.applicationContext)
                                    val teamDao = database.teamDao()
                                    val playerDao = database.playerDao()
                                    val coachDao = database.coachDao()
                                    val matchdayDao = database.matchdayDao()

                                    TeamsData.teamList.forEach { teamInfo ->
                                        // Insert team
                                        teamDao.insertTeam(
                                            TeamEntity(name = teamInfo.teamName)
                                        )

                                        // Insert players
                                        teamInfo.players.forEach { player ->
                                            playerDao.insertPlayer(
                                                PlayerEntity(
                                                    number = player.number,
                                                    firstName = player.firstName,
                                                    lastName = player.lastName,
                                                    nickname = player.nickname,
                                                    position = player.position,
                                                    team = teamInfo.teamName
                                                )
                                            )
                                        }

                                        // Insert coaches
                                        teamInfo.coaches.forEach { coach ->
                                            coachDao.insertCoach(
                                                CoachEntity(
                                                    name = coach.name,
                                                    team = teamInfo.teamName
                                                )
                                            )
                                        }

                                        // Insert matchdays if any
                                        val predefinedMatchdays = TeamsData.getMatchesForTeam(teamInfo.teamName)
                                        predefinedMatchdays.forEach { matchday ->
                                            matchdayDao.insertMatchday(matchday)
                                        }
                                    }
                                }
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }

        fun getRepository(context: Context): Repositories {
            val db = getDatabase(context)
            return Repositories(
                playerRepository = PlayerRepository(db.playerDao()),
                eventRepository = EventRepository(db.eventDao(), db.absenceDao()),
                matchdayRepository = MatchdayRepository(db.matchdayDao()),
                playerStatsRepository = PlayerStatRepository(db.playerStatsDao()),
                absenceRepository = AbsenceRepository(db.absenceDao()),
                teamRepository = TeamRepository(db.teamDao()),
                coachRepository = CoachRepository(db.coachDao()),
                callUpRepository = CallUpRepository(db.callUpDao()),
                formationRepository = FormationRepository(db.formationDao(), db.playerPositionDao())
            )
        }
    }

    data class Repositories(
        val playerRepository: PlayerRepository,
        val eventRepository: EventRepository,
        val matchdayRepository: MatchdayRepository,
        val playerStatsRepository: PlayerStatRepository,
        val absenceRepository: AbsenceRepository,
        val teamRepository: TeamRepository,
        val coachRepository: CoachRepository,
        val callUpRepository: CallUpRepository,
        val formationRepository: FormationRepository
    )
}
