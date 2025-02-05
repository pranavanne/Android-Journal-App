package com.example.journalapplication.details

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.journalapplication.data.Post
import com.example.journalapplication.entry.PopupBox
import com.example.journalapplication.navigation.NavigationDestination
import kotlinx.coroutines.launch

object JournalEntryDetailsDestination : NavigationDestination {
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
        topBar = { JournalEntryDetailsTopBar(uiState, navigateBack) },
        floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigateToEntryEditScreen(uiState.id) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                }
        }
    ) { contentPadding ->
        JournalEntryDetailsBody(
            post = uiState,
            deletePost = {
                coroutineScope.launch {
                    navigateBack()
                    viewModel.deletePost(uiState)
                }
            },
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryDetailsTopBar(post: Post, navigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = with(post) { "$day/$month/$year" })
            }
        },
        navigationIcon = {
            IconButton(onClick = { navigateBack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun JournalEntryDetailsBody(
    post: Post,
    deletePost: () -> Unit,
    modifier: Modifier = Modifier
) {

    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {
        Log.d("DetailsScreen", post.toString())
        Text(text = post.content, modifier = Modifier.fillMaxWidth())
        if (post.uri != Uri.EMPTY) {
            var showPopup by rememberSaveable { mutableStateOf(false) }
            GlideImage(model = post.uri, contentDescription = null, modifier = Modifier.clickable { showPopup = true }.clip(OutlinedTextFieldDefaults.shape))
            PopupBox(uri = post.uri, showPopup = showPopup, onClickOutside = {showPopup = false})
        }
        Button(
            onClick = { deleteConfirmationRequired = true },
            modifier = Modifier.padding(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
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
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
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
        }
    )
}