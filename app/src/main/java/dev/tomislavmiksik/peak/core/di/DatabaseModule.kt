package dev.tomislavmiksik.peak.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.tomislavmiksik.peak.core.data.local.PeakDatabase
import dev.tomislavmiksik.peak.core.data.local.dao.HealthSnapshotDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PeakDatabase {
        return Room.databaseBuilder(
            context,
            PeakDatabase::class.java,
            "phoenix_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideHealthSnapshotDao(database: PeakDatabase): HealthSnapshotDao {
        return database.healthSnapshotDao()
    }
}
