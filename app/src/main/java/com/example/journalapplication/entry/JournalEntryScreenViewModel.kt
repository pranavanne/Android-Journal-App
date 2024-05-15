package com.example.journalapplication.entry

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.journalapplication.JournalApplication
import com.example.journalapplication.data.Post
import com.example.journalapplication.data.PostsRepository
import java.util.Calendar

class JournalEntryScreenViewModel(private val postsRepository: PostsRepository): ViewModel() {

    var uiState by mutableStateOf(EntryData())
        private set

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JournalApplication
                JournalEntryScreenViewModel(postsRepository = application.container.postsRepository)
            }
        }
    }

    fun updateUiState(entryData: EntryData) {
        entryData.isEntryValid = validateInput(entryData = entryData)
        uiState = entryData
    }

    private fun validateInput(entryData: EntryData): Boolean {
        return with(entryData) {
            content.isNotBlank()
        }
    }

    suspend fun saveInput() {
        if (uiState.isEntryValid) {
            val calendar = Calendar.getInstance()
            val post = Post(id = 0, day = calendar.get(Calendar.DATE), month = calendar.get(Calendar.MONTH) + 1, year = calendar.get(Calendar.YEAR), content = uiState.content, time = Calendar.getInstance().timeInMillis, uri = uiState.uri)
            postsRepository.insertPost(post)
        }
    }
}

data class EntryData(
    val content: String = "",
    val uri: Uri = Uri.EMPTY,
    var isEntryValid: Boolean = false
)