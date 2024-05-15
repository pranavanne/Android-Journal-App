package com.example.journalapplication.details

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.journalapplication.JournalApplication
import com.example.journalapplication.data.Post
import com.example.journalapplication.data.PostsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn

class JournalEntryDetailsScreenViewModel(savedStateHandle: SavedStateHandle, private val postsRepository: PostsRepository): ViewModel() {

    private val journalId: Int = checkNotNull(savedStateHandle[JournalEntryDetailsDestination.journalIdArg])

    val uiState = postsRepository.getPost(journalId).filterNotNull().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        Post(day = 1, month = 1, year = 1, content = "Using test Input value", time = 10000L, uri = Uri.EMPTY),
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JournalApplication
                JournalEntryDetailsScreenViewModel(this.createSavedStateHandle(), application.container.postsRepository)
            }
        }
    }

    suspend fun deletePost(post: Post) {
        postsRepository.deletePost(post = post)
    }
}