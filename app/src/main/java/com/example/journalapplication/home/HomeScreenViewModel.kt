package com.example.journalapplication.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.journalapplication.JournalApplication
import com.example.journalapplication.data.PostsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeScreenViewModel(postsRepository: PostsRepository): ViewModel() {

    val uiState = postsRepository.getAllPosts().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        listOf()
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as JournalApplication
                HomeScreenViewModel(application.container.postsRepository)
            }
        }
    }
}