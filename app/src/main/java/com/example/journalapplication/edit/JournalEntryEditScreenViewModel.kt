package com.example.journalapplication.edit

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class JournalEntryEditScreenViewModel(savedStateHandle: SavedStateHandle, private val postsRepository: PostsRepository): ViewModel() {

    private val postId: Int = checkNotNull(savedStateHandle[JournalEntryEditDestination.journalIdArgs])

    var uiState by mutableStateOf(EntryEditData())

    lateinit var postData: Post

    init {
        viewModelScope.launch {
            postData = postsRepository.getPost(postId).first()
            uiState = postData.toEntryEditData()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JournalApplication
                JournalEntryEditScreenViewModel(this.createSavedStateHandle(), application.container.postsRepository)
            }
        }
    }

    fun updateUiState(entryEditData: EntryEditData) {
        uiState = uiState.copy(content = entryEditData.content, imageUri = entryEditData.imageUri, isEntryValid = validateInput(entryEditData))
    }

    fun validateInput(entryEditData: EntryEditData = uiState): Boolean {
        return with(entryEditData) {
            content.isNotBlank()
        }
    }

    suspend fun saveUpdate() {
        if(validateInput()) {
            postsRepository.updatePost(postData.copy(content = uiState.content, uri = uiState.imageUri))
        }
    }
}

private fun Post.toEntryEditData(): EntryEditData {
    return EntryEditData(content = content, imageUri = uri)
}

data class EntryEditData(
    val content: String = "",
    val imageUri: Uri = Uri.EMPTY,
    var isEntryValid: Boolean = false
)