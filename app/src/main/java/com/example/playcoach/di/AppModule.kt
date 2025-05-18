package com.example.playcoach.di

import android.content.Context
import androidx.room.Room
import com.example.playcoach.data.database.AppDatabase
import com.example.playcoach.data.daos.*
import com.example.playcoach.data.repositories.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "playcoach_database")
            .fallbackToDestructiveMigration(false)
            .build()

    // DAOs
    @Provides fun providePlayerDao(db: AppDatabase): PlayerDao = db.playerDao()
    @Provides fun provideEventDao(db: AppDatabase): EventDao = db.eventDao()
    @Provides fun provideMatchdayDao(db: AppDatabase): MatchdayDao = db.matchdayDao()
    @Provides fun provideAbsenceDao(db: AppDatabase): AbsenceDao = db.absenceDao()
    @Provides fun provideTeamDao(db: AppDatabase): TeamDao = db.teamDao()
    @Provides fun provideCoachDao(db: AppDatabase): CoachDao = db.coachDao()
    @Provides fun provideCallUpDao(db: AppDatabase): CallUpDao = db.callUpDao()
    @Provides fun provideFormationDao(db: AppDatabase): FormationDao = db.formationDao()
    @Provides fun providePlayerPositionDao(db: AppDatabase): PlayerPositionDao = db.playerPositionDao()
    @Provides fun providePlayerStatDao(db: AppDatabase): PlayerStatDao = db.playerStatsDao()

    // Repositories
    @Provides fun providePlayerRepository(dao: PlayerDao) = PlayerRepository(dao)
    @Provides fun provideEventRepository(eventDao: EventDao, absenceDao: AbsenceDao) = EventRepository(eventDao, absenceDao)
    @Provides fun provideMatchdayRepository(dao: MatchdayDao) = MatchdayRepository(dao)
    @Provides fun providePlayerStatRepository(dao: PlayerStatDao) = PlayerStatRepository(dao)
    @Provides fun provideAbsenceRepository(dao: AbsenceDao) = AbsenceRepository(dao)
    @Provides fun provideTeamRepository(dao: TeamDao) = TeamRepository(dao)
    @Provides fun provideCoachRepository(dao: CoachDao) = CoachRepository(dao)
    @Provides fun provideCallUpRepository(dao: CallUpDao) = CallUpRepository(dao)
    @Provides fun provideFormationRepository(fd: FormationDao, pd: PlayerPositionDao) = FormationRepository(fd, pd)
}
