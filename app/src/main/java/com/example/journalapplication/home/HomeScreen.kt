package com.example.journalapplication.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journalapplication.data.Post
import com.example.journalapplication.navigation.NavigationDestination
import com.example.journalapplication.ui.theme.JournalApplicationTheme
import java.util.Calendar

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
    val uiState by homeScreenViewModel.uiState.collectAsState()
    Log.d("wonder", uiState.toString())
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
        posts = uiState,
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
fun JournalEntryList(posts: List<Post>, onPostClicked: (Int) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(posts) {
            JournalEntry(post = it, modifier = Modifier.clickable{onPostClicked(it.id)})
        }
    }
}

@Composable
fun JournalEntry(post: Post, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${post.day}/${post.month}/${post.year}")
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

@Preview(showBackground = true)
@Composable
fun JournalEntryListPreview() {
    JournalApplicationTheme {
        val listOfPosts = listOf(
            Post(id = 100,day = 1, month = 1, year = 1, content = "Using test Input value", time = 10000L),
            Post(id = 101, day = 2, month = 2, year = 2, content = "Using test Input value", time = 10001L),
            Post(id = 102, day = 3, month = 3, year = 3, content = "Using test Input value", time = 10002L)
        )
        JournalEntryList(posts = listOfPosts, {})
    }
}