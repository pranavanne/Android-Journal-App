package com.example.journalapplication.data

import android.content.Context

interface AppContainer {
    val postsRepository: PostsRepository
}

class AppDataContainer(private val context: Context): AppContainer {
    override val postsRepository by lazy {
        OfflinePostsRepository(JournalDatabase.getDatabase(context).postDao())
    }
}