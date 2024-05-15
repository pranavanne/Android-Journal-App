package com.example.journalapplication.entry

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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

    val controller = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            JournalEntryTopBar(title = JournalEntryScreenDestination.titleRes, navigateBack = {
                controller?.hide()
                navigateBack()
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                coroutineScope.launch {
                    controller?.hide()
                    viewModel.saveInput()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(20.dp)) {
                Icon(imageVector = Icons.Default.Done, contentDescription = null)
            }
        }
    ) {
        innerPadding -> JournalEntryBody(
        entryData = uiState,
        onEntryDataInputted = viewModel::updateUiState,
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 10.dp))
    }
}

@Composable
fun JournalEntryBody(entryData: EntryData, onEntryDataInputted: (EntryData) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        JournalEntryInputForm(entryData = entryData, onEntryDataInputted = onEntryDataInputted)
    }
}

@Composable
fun JournalEntryInputForm(entryData: EntryData, onEntryDataInputted: (EntryData) -> Unit, modifier: Modifier = Modifier) {

    val focusRequester = remember { FocusRequester() }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onEntryDataInputted(entryData.copy(uri = uri))
        }
    }

    val controller = LocalSoftwareKeyboardController.current

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(
            label = { Text(text = "Content")},
            value = entryData.content,
            onValueChange = {onEntryDataInputted(entryData.copy(content = it))},
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .focusRequester(focusRequester)
        )
        Button(onClick = {
            controller?.hide()
            launcher.launch("image/*")
        }) {
                Text(text = "+ Image")

        }
        if (entryData.uri != Uri.EMPTY) {
            Box() {
                IconButton(onClick = { onEntryDataInputted(entryData.copy(uri = Uri.EMPTY)) }, modifier = Modifier
                    .zIndex(100f)
                    .align(Alignment.TopEnd)
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null, tint = MaterialTheme.colorScheme.background)
                }
                AsyncImage(model = entryData.uri, contentDescription = null)

            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryTopBar(title: String, navigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = {navigateBack()}) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}
