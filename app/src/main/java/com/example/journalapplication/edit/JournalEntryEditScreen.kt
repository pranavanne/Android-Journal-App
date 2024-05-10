package com.example.journalapplication.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.journalapplication.navigation.NavigationDestination
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

object JournalEntryEditDestination: NavigationDestination {
    override val route: String = "journal_entry_edit"
    override val titleRes: String = "Edit Entry"
    const val journalIdArgs = "journalId"
    val routeWithArgs = "$route/{$journalIdArgs}"
}

@Composable
fun JournalEntryEditScreen(
    viewModel: JournalEntryEditScreenViewModel = viewModel(factory = JournalEntryEditScreenViewModel.Factory),
    navigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            JournalEntryEditAppBar(title = "Edit Content", navigateBack)
        }
    ) {
        innerPadding -> JournalEntryEditBody(
        post = uiState,
        onEdit = viewModel::updateUiState,
        onUpdate = {
                   coroutineScope.launch {
                       viewModel.saveUpdate()
                       navigateBack()
                   }
        },
        modifier = Modifier.padding(innerPadding)
    )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryEditAppBar(title: String, navigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = { navigateBack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
fun JournalEntryEditBody(post: EntryEditData, onEdit: (EntryEditData) -> Unit, onUpdate: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        JournalEntryEditInputForm(post = post, onEdit = onEdit)
        Button(onClick = onUpdate) {
            Text(text = "Update")
        }
    }
}

@Composable
fun JournalEntryEditInputForm(post: EntryEditData, onEdit: (EntryEditData) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        OutlinedTextField(
            label = { Text(text = "Content") },
            value = post.content,
            onValueChange = {onEdit(post.copy(content = it))},
            modifier = Modifier.fillMaxWidth().height(400.dp)
        )
    }
}