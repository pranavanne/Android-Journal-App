package com.example.journalapplication.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.journalapplication.data.Post
import com.example.journalapplication.navigation.NavigationDestination
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

object JournalEntryDetailsDestination: NavigationDestination {
    override val route: String = "journal_entry_details"
    override val titleRes: String = ""
    const val journalIdArg = "journalId"
    val routeWithArgs = "$route/{$journalIdArg}"
    // like url JournalEntryDetailsScreen/{arguments}

}

@Composable
fun JournalEntryDetailsScreen(
    viewModel: JournalEntryDetailsScreenViewModel = viewModel(factory = JournalEntryDetailsScreenViewModel.Factory),
    navigateToEntryEditScreen: (Int) -> Unit,
    navigateBack: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { JournalEntryDetailsTopBar(uiState, navigateBack) }
    ) {
        contentPadding -> JournalEntryDetailsBody(
        post = uiState,
        navigateToEntryEditScreen = navigateToEntryEditScreen,
        deletePost = {
                     coroutineScope.launch {
                         navigateBack()
                         viewModel.deletePost(uiState)
                     }
        },
        modifier = Modifier.padding(contentPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryDetailsTopBar(post: Post, navigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = with(post) {"$day/$month/$year"})
            }
        },
        navigationIcon = {
            IconButton(onClick = {navigateBack()}) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
fun JournalEntryDetailsBody(post: Post, navigateToEntryEditScreen: (Int) -> Unit, deletePost: () -> Unit, modifier: Modifier = Modifier) {

    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier
        .verticalScroll(rememberScrollState())
        .padding(20.dp)) {
        Text(text = post.content, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { navigateToEntryEditScreen(post.id) }) {
                Text(text = "Edit")
            }
            Button(onClick = { deleteConfirmationRequired = true }) {
                Text(text = "Delete")
            }
        }
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    deletePost()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text("Attention") },
        text = { Text("Are you sure you want to delete?") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text("No")
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text("Yes")
            }
        })
}