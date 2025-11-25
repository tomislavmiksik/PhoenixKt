package dev.tomislavmiksik.phoenix.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.tomislavmiksik.phoenix.core.config.AppConfig
import dev.tomislavmiksik.phoenix.core.data.local.PhoenixDatabase
import dev.tomislavmiksik.phoenix.core.data.local.dao.ExerciseDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        appConfig: AppConfig
    ): PhoenixDatabase {
        val db = Room.databaseBuilder(
            context,
            PhoenixDatabase::class.java,
            "phoenix_database"
        )

        if(appConfig.isDebugMode) return db.fallbackToDestructiveMigration().build()

        return db.build();
    }

    @Provides
    @Singleton
    fun provideExercisesDao(database: PhoenixDatabase): ExerciseDao {
        return database.getExerciseDao()
    }
}
