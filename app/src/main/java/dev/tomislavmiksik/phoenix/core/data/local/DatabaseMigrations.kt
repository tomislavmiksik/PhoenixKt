package dev.tomislavmiksik.phoenix.core.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    const val DB_VERSION = 1

    val MIGRATIONS: Array<Migration>
        get() =
            arrayOf<Migration>(
                migration12(),
            )

    private fun migration12(): Migration =
        object : Migration(1, 1) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
}