package com.example.journalapplication.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.journalapplication.JournalApplication
import com.example.journalapplication.data.Post
import com.example.journalapplication.data.PostsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.stateIn

class HomeScreenViewModel(postsRepository: PostsRepository): ViewModel() {

    var uiState by mutableStateOf(HomeScreenData(
        postsDataState = listOf<List<Post>>().asFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), listOf())
    ))

    private var postsDataState: StateFlow<List<Post>>

    init {
        postsDataState = postsRepository.getAllPosts().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            listOf()
        )
        uiState = HomeScreenData(postsDataState)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as JournalApplication
                HomeScreenViewModel(application.container.postsRepository)
            }
        }
    }

    fun updateDay(day: String) {
        uiState = uiState.copy(day = day)
    }

    fun updateMonth(month: String) {
        uiState = uiState.copy(month = month)
    }

    fun updateYear(year: String) {
        uiState = uiState.copy(year = year)
    }

    fun onFilterButtonClicked(clicked: Boolean) {
        uiState = uiState.copy(filterButtonClicked = clicked)
    }

    fun filterPosts(postsData: List<Post>, day: String, month: String, year: String): List<Post> {
        val dayInput = day.toIntOrNull()?:0
        val monthInput = month.toIntOrNull()?:0
        val yearInput = year.toIntOrNull()?:0

        if (dayInput == 0 && monthInput == 0 && yearInput == 0) { // nothing provided
            return postsData
        } else if (dayInput == 0 && monthInput == 0) { // year provided
            return postsData.filter { it.year == yearInput }
        } else if (dayInput == 0 && yearInput == 0) { // month provided
            return postsData.filter { it.month == monthInput }
        } else if (monthInput == 0 && yearInput == 0) { // day provided
            return postsData.filter { it.day == dayInput }
        } else if (monthInput == 0) {
            return postsData.filter { it.day == dayInput && it.year == yearInput }
        } else if (dayInput == 0) {
            return postsData.filter { it.month == monthInput && it.year == yearInput }
        } else if (yearInput == 0) {
            return postsData.filter { it.day == dayInput && it.month == monthInput }
        } else {
            return postsData.filter { it.day == dayInput && it.month == monthInput && it.year == yearInput }
        }
    }
}

data class HomeScreenData(
    val postsDataState: StateFlow<List<Post>>,
    val day: String = "",
    val month: String = "",
    val year: String = "",
    val filterButtonClicked: Boolean = false
)