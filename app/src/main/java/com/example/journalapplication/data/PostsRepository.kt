package com.example.journalapplication.data

import kotlinx.coroutines.flow.Flow

interface PostsRepository {

    suspend fun insertPost(post: Post)

    suspend fun updatePost(post: Post)

    suspend fun deletePost(post: Post)

    fun getPost(id: Int): Flow<Post>

    fun getAllPosts(): Flow<List<Post>>
}