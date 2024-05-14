package com.example.journalapplication.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journalapplication.ui.theme.JournalApplicationTheme
import com.example.journalapplication.data.Post
import com.example.journalapplication.navigation.NavigationDestination
import java.util.Calendar
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction4

object HomeScreenDestination: NavigationDestination {
    override val route: String = "home"
    override val titleRes: String = "Journal"
}

@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = viewModel(factory = HomeScreenViewModel.Factory),
    navigateToEntryScreen: () -> Unit,
    navigateToEntryDetailsScreen: (Int) -> Unit,
) {
    val uiState = homeScreenViewModel.uiState

    val postsDataState by uiState.postsDataState.collectAsState()

    Log.d("wonder", postsDataState.toString())
    Log.d("wonder", Calendar.getInstance().get(Calendar.DATE).toString())

    Scaffold(
        topBar = {
            JournalAppBar(title = HomeScreenDestination.titleRes)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToEntryScreen,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(20.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
    ) {
        innerPadding -> JournalEntryList(
        uiState = uiState,
        posts = postsDataState,
        onDayChanged = homeScreenViewModel::updateDay,
        onMonthChanged = homeScreenViewModel::updateMonth,
        onYearChanged = homeScreenViewModel::updateYear,
        onFilterButtonClicked = homeScreenViewModel::onFilterButtonClicked,
        filterPosts = homeScreenViewModel::filterPosts,
        onPostClicked = navigateToEntryDetailsScreen,
        modifier = Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalAppBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        }
    )
}

@Composable
fun JournalEntryList(
    uiState: HomeScreenData,
    posts: List<Post>,
    onPostClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onDayChanged: KFunction1<String, Unit>,
    onMonthChanged: KFunction1<String, Unit>,
    onYearChanged: KFunction1<String, Unit>,
    onFilterButtonClicked: KFunction1<Boolean, Unit>,
    filterPosts: KFunction4<List<Post>, String, String, String, List<Post>>
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)) {
            OutlinedTextField(value = uiState.day, onValueChange = { if(it.length <= 2 && (it.toIntOrNull() ?: 0) <= 31){onDayChanged(it)} }, label = { Text(text = "Day")}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.width(100.dp), enabled = !uiState.filterButtonClicked, singleLine = true)
            OutlinedTextField(value = uiState.month, onValueChange = { if(it.length <= 2 && (it.toIntOrNull()?:0) <= 12){onMonthChanged(it)} }, label = { Text(text = "Month")}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.width(100.dp), enabled = !uiState.filterButtonClicked, singleLine = true)
            OutlinedTextField(value = uiState.year, onValueChange = { if(it.length <= 4){onYearChanged(it)} }, label = { Text(text = "Year")}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.width(100.dp), enabled = !uiState.filterButtonClicked, singleLine = true)
            IconButton(onClick = { onFilterButtonClicked(!uiState.filterButtonClicked) }) {
                if (uiState.filterButtonClicked) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                } else {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                }
            }
        }
        LazyColumn(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                if (uiState.filterButtonClicked) {
                    filterPosts(posts,uiState.day, uiState.month, uiState.year)
                } else {
                    posts
                }
            ) {
                JournalEntry(post = it, modifier = Modifier.clickable{onPostClicked(it.id)})
            }
        }
    }

}

@Composable
fun JournalEntry(post: Post, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${post.day}/${post.month}/${post.year}", style = MaterialTheme.typography.titleLarge)
            }
            Text(text = if(post.content.length > 150){
                post.content.substring(0,150) + "..."
            } else {
                post.content
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JournalEntryPreview() {
    JournalApplicationTheme {
        JournalEntry(post = Post(id = 100, day = 1, month = 1, year = 1, content = "Using test Input value", time = 10000L))
    }
}
