package com.example.journalapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Insert
    suspend fun insertPost(post: Post)

    @Update
    suspend fun updatePost(post: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("SELECT * FROM posts WHERE id = :id")
    fun getPost(id: Int): Flow<Post>

    @Query("SELECT * FROM posts ORDER BY time DESC")
    fun getAllPosts(): Flow<List<Post>>
}