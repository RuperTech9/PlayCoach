package com.example.playcoach.data.database

import android.content.Context
import androidx.room.*
import com.example.playcoach.data.daos.*
import com.example.playcoach.data.entities.*
import com.example.playcoach.data.repositories.*
import com.example.playcoach.di.DatabaseCallback

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
                    .addCallback(DatabaseCallback(context)) // ✅ Aquí se añade el callback
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
