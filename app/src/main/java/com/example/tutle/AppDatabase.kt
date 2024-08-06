package com.example.tutle

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Nickname::class, GraphData::class, AlarmData::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun nicknameDao(): NicknameDao
    abstract fun graphDataDao(): GraphDataDao
    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context, userId: String): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context, userId).also { instance = it }
            }

        private fun buildDatabase(context: Context, userId: String) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "database-$userId")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // 마이그레이션 추가
                .build()

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `GraphData` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `user_id` TEXT NOT NULL, `x_value` FLOAT NOT NULL, `y_value` FLOAT NOT NULL)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `alarms` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `hour` INTEGER NOT NULL, `minute` INTEGER NOT NULL, `description` TEXT NOT NULL)")
            }
        }
    }
}
