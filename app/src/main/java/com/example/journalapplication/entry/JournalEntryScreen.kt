package com.example.journalapplication.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journalapplication.navigation.NavigationDestination
import kotlinx.coroutines.launch

object JournalEntryScreenDestination: NavigationDestination {
    override val route: String = "journal_entry"
    override val titleRes: String = "Add Journal Entry"
}

@Composable
fun JournalEntryScreen(
    viewModel: JournalEntryScreenViewModel = viewModel(factory = JournalEntryScreenViewModel.Factory),
    navigateBack: () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            JournalEntryTopBar(title = JournalEntryScreenDestination.titleRes)
        }
    ) {
        innerPadding -> JournalEntryBody(
        entryData = uiState,
        onEntryDataInputted = viewModel::updateUiState,
        onSaveClicked = {
            coroutineScope.launch {
                viewModel.saveInput()
                navigateBack()
            }
        },
        modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun JournalEntryBody(entryData: EntryData, onEntryDataInputted: (EntryData) -> Unit, onSaveClicked: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        JournalEntryInputForm(entryData = entryData, onEntryDataInputted = onEntryDataInputted)
        Button(
            onClick = onSaveClicked,
            enabled = entryData.isEntryValid
        ) {
            Text(text = "Save")
        }
    }
}

@Composable
fun JournalEntryInputForm(entryData: EntryData, onEntryDataInputted: (EntryData) -> Unit, modifier: Modifier = Modifier) {

    val focusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        OutlinedTextField(
            label = { Text(text = "Content")},
            value = entryData.content,
            onValueChange = {onEntryDataInputted(entryData.copy(content = it))},
            modifier = Modifier.fillMaxWidth().height(400.dp).focusRequester(focusRequester)
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryTopBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        }
    )
}
