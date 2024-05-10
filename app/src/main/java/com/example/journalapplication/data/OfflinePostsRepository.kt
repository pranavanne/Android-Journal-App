package com.example.journalapplication.data

import kotlinx.coroutines.flow.Flow

class OfflinePostsRepository(private val postDao: PostDao): PostsRepository {
    override suspend fun insertPost(post: Post) {
        postDao.insertPost(post)
    }

    override suspend fun updatePost(post: Post) {
        postDao.updatePost(post)
    }

    override suspend fun deletePost(post: Post) {
        postDao.deletePost(post)
    }

    override fun getPost(id: Int): Flow<Post> {
        return postDao.getPost(id)
    }

    override fun getAllPosts(): Flow<List<Post>> {
        return postDao.getAllPosts()
    }
}