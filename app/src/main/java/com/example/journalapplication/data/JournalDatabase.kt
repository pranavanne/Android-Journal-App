package com.example.journalapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Post::class], version = 2, exportSchema = false)
abstract class JournalDatabase: RoomDatabase() {
    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var Instance: JournalDatabase? = null

        fun getDatabase(context: Context): JournalDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, JournalDatabase::class.java, "journal_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}