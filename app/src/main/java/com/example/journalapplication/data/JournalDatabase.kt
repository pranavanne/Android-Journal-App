package com.example.journalapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Post::class], version = 4, exportSchema = false)
@TypeConverters(value = [ArrayListConverter::class])
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