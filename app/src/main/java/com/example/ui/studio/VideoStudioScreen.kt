package com.example.ui.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoClip
import com.example.model.VideoProject
import com.example.ui.ai.AiScriptGeneratorDialog
import com.example.ui.editor.AddClipDialog
import com.example.ui.editor.EditorToolbarPanels
import com.example.ui.editor.TimelineEditorView
import com.example.ui.export.ExportVideoDialog
import com.example.ui.player.VideoCanvasPlayer
import com.example.ui.theme.*
import com.example.viewmodel.VideoStudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoStudioScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.currentProject.collectAsState()
    val currentTime by viewModel.currentTimeSec.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val selectedClipIndex by viewModel.selectedClipIndex.collectAsState()

    var showAddClipDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showAiScriptDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("video_studio_screen"),
        containerColor = StudioDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showRenameDialog = true }
                    ) {
                        Text(
                            text = project.title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Renombrar",
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                actions = {
                    // AI Script Generator Button
                    IconButton(
                        onClick = { showAiScriptDialog = true },
                        modifier = Modifier.testTag("ai_script_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Guión IA",
                            tint = BrightAmber
                        )
                    }

                    // Export Video Button
                    Button(
                        onClick = {
                            viewModel.pause()
                            showExportDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("export_video_top_button")
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Exportar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StudioSurfaceDark
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Interactive Video Canvas Player
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                VideoCanvasPlayer(
                    project = project,
                    currentTimeSec = currentTime,
                    isPlaying = isPlaying,
                    onTogglePlay = { viewModel.togglePlay() },
                    onSeek = { viewModel.seekTo(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Multi-track Timeline Track View
            TimelineEditorView(
                project = project,
                currentTimeSec = currentTime,
                selectedClipIndex = selectedClipIndex,
                onSelectClip = { viewModel.selectClip(it) },
                onAddClipClick = { showAddClipDialog = true },
                onDeleteClip = { viewModel.deleteClip(it) },
                onSeek = { viewModel.seekTo(it) },
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Editing Tools Panels (Filters, Text, Transitions, Trim/Speed, Audio, Stickers, Formats)
            EditorToolbarPanels(
                project = project,
                selectedClipIndex = selectedClipIndex,
                onUpdateClip = { idx, clip -> viewModel.updateClip(idx, clip) },
                onAddTextOverlay = { viewModel.addTextOverlay(it) },
                onRemoveTextOverlay = { viewModel.removeTextOverlay(it) },
                onAddSticker = { viewModel.addSticker(it) },
                onRemoveSticker = { viewModel.removeSticker(it) },
                onSelectAudio = { viewModel.setAudioTrack(it) },
                onPlaySfx = { viewModel.playSfx(it) },
                onChangeAspectRatio = { viewModel.setAspectRatio(it) },
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
        }
    }

    if (showAddClipDialog) {
        AddClipDialog(
            onDismiss = { showAddClipDialog = false },
            onAddClip = { newClip ->
                viewModel.addClip(newClip)
                showAddClipDialog = false
            }
        )
    }

    if (showExportDialog) {
        ExportVideoDialog(
            project = project,
            onDismiss = { showExportDialog = false },
            onExportComplete = { exportedProj ->
                viewModel.onExportCompleted(exportedProj)
            }
        )
    }

    if (showAiScriptDialog) {
        AiScriptGeneratorDialog(
            onDismiss = { showAiScriptDialog = false },
            onApplyScript = { script ->
                viewModel.applyAiScript(script)
                showAiScriptDialog = false
            }
        )
    }

    if (showRenameDialog) {
        var tempTitle by remember { mutableStateOf(project.title) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            containerColor = StudioSurfaceElevated,
            title = { Text("Renombrar Proyecto", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = tempTitle,
                    onValueChange = { tempTitle = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CoralPink,
                        unfocusedBorderColor = StudioCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempTitle.isNotBlank()) {
                            viewModel.updateProjectTitle(tempTitle.trim())
                        }
                        showRenameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPink)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancelar", color = TextSecondaryDark)
                }
            }
        )
    }
}
