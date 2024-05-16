package com.example.journalapplication.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.journalapplication.navigation.NavigationDestination
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.journalapplication.entry.PopupBox
import kotlinx.coroutines.launch

object JournalEntryEditDestination : NavigationDestination {
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

    val controller = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            JournalEntryEditAppBar(title = "Edit Content", navigateBack = {
                controller?.hide()
                navigateBack()
            })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        controller?.hide()
                        viewModel.saveUpdate()
                        navigateBack()
                    }
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(20.dp),
            ) {
                Icon(imageVector = Icons.Default.Done, contentDescription = null)
            }
        }
    ) {
        innerPadding -> JournalEntryEditBody(
        post = uiState,
        onEdit = viewModel::updateUiState,
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 10.dp)
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
fun JournalEntryEditBody(
    post: EntryEditData,
    onEdit: (EntryEditData) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        JournalEntryEditInputForm(post = post, onEdit = onEdit)
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun JournalEntryEditInputForm(
    post: EntryEditData,
    onEdit: (EntryEditData) -> Unit,
    modifier: Modifier = Modifier
) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null){
            onEdit(post.copy(imageUri = uri))
        }
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(
            label = { Text(text = "Content") },
            value = post.content,
            onValueChange = { onEdit(post.copy(content = it)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )
        Spacer(modifier = Modifier.padding(10.dp))
        if (post.imageUri != Uri.EMPTY) {
            Box {
                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.zIndex(100f).align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer,OutlinedTextFieldDefaults.shape)
                    )
                }
                IconButton(
                    onClick = { onEdit(post.copy(imageUri = Uri.EMPTY)) },
                    modifier = Modifier.zIndex(100f).align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer,OutlinedTextFieldDefaults.shape)
                    )
                }
                var showPopup by rememberSaveable { mutableStateOf(false) }
                GlideImage(model = post.imageUri, contentDescription = null, modifier = Modifier.clickable { showPopup = true }.clip(OutlinedTextFieldDefaults.shape))
                PopupBox(uri = post.imageUri, showPopup = showPopup, onClickOutside = {showPopup = false})
            }
        } else {
            Button(onClick = { launcher.launch("image/*") }) {
                Text(text = "Add Image")
            }
        }
    }
}