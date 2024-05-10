package com.example.journalapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "day") val day: Int,
    @ColumnInfo(name = "month") val month: Int,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "time") val time: Long
)