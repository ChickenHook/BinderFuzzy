package org.chickenhook.binderfuzzy.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(FuzzyTaskInfo::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fuzzyTaskInfoDao(): FuzzyTaskDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            if (INSTANCE == null) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "database-name"
                ).build()
                INSTANCE = db
                return db
            } else {
                return INSTANCE!!
            }
        }
    }
}
