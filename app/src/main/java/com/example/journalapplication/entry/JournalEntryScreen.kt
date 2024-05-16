package com.example.journalapplication.entry

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.journalapplication.navigation.NavigationDestination
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun JournalEntryInputForm(entryData: EntryData, onEntryDataInputted: (EntryData) -> Unit, modifier: Modifier = Modifier) {

    val focusRequester = remember { FocusRequester() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
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
        Spacer(modifier = Modifier.padding(10.dp))
        if (entryData.uri != Uri.EMPTY) {
            Box() {
                IconButton(
                    onClick = {
                    controller?.hide()
                    launcher.launch("image/*")
                              },
                    modifier = Modifier
                    .zIndex(100f)
                    .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer,OutlinedTextFieldDefaults.shape)
                    )
                }
                IconButton(
                    onClick = { onEntryDataInputted(entryData.copy(uri = Uri.EMPTY)) },
                    modifier = Modifier.zIndex(100f).align(Alignment.TopEnd)
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer,OutlinedTextFieldDefaults.shape))
                }
                var showPopup by rememberSaveable { mutableStateOf(false) }
                GlideImage(
                    model = entryData.uri,
                    contentDescription = null,
                    modifier = Modifier.clickable { showPopup = true }.clip(OutlinedTextFieldDefaults.shape)
                )
                PopupBox(uri = entryData.uri, showPopup = showPopup, onClickOutside = {showPopup = false})
            }
        } else {
            Button(onClick = {
                controller?.hide()
                launcher.launch("image/*")
            }) {
                Text(text = "+ Image")
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@ExperimentalGlideComposeApi
@Composable
fun ZoomableImage(model: Uri, contentDescription: String? = null) {
    val angle by remember { mutableStateOf(0f) }
    var zoom by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val configuration = LocalConfiguration.current
    val screenWidth = 2*configuration.screenWidthDp.dp.value/3
    val screenHeight = 2*configuration.screenHeightDp.dp.value/3

    GlideImage(
        model,
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .graphicsLayer(
                scaleX = zoom,
                scaleY = zoom,
                rotationZ = angle
            )
            .pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = { _, pan, gestureZoom, _ ->
                        zoom = (zoom * gestureZoom).coerceIn(1F..2F)
                        if (zoom > 1) {
                            val x = (pan.x * zoom)
                            val y = (pan.y * zoom)
                            val angleRad = angle * PI / 180.0

                            offsetX =
                                (offsetX + (x * cos(angleRad) - y * sin(angleRad)).toFloat()).coerceIn(
                                    -(screenWidth * zoom)..(screenWidth * zoom)
                                )
                            offsetY =
                                (offsetY + (x * sin(angleRad) + y * cos(angleRad)).toFloat()).coerceIn(
                                    -(screenHeight * zoom)..(screenHeight * zoom)
                                )
                        } else {
                            offsetX = 0F
                            offsetY = 0F
                        }
                    },
                )
            }
            .fillMaxSize()
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PopupBox(uri: Uri, showPopup: Boolean, onClickOutside: () -> Unit) {

    if (showPopup) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .zIndex(10F),
            contentAlignment = Alignment.Center
        ) {
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(
                    focusable = true,
                    excludeFromSystemGesture = false,
                    dismissOnBackPress = true
                ),
                // to dismiss on click outside
                onDismissRequest = { onClickOutside() },
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .clip(RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(modifier = Modifier.padding(20.dp).align(Alignment.BottomEnd).zIndex(100f)) {
                        IconButton(onClick = {onClickOutside()}, modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, OutlinedTextFieldDefaults.shape)
                            .zIndex(100f)
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    ZoomableImage(model = uri, contentDescription = null)
                }
            }
        }
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
