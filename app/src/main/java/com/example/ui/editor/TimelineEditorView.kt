package com.example.ui.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun TimelineEditorView(
    project: VideoProject,
    currentTimeSec: Float,
    selectedClipIndex: Int,
    onSelectClip: (Int) -> Unit,
    onAddClipClick: () -> Unit,
    onDeleteClip: (Int) -> Unit,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("timeline_editor_card"),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        border = BorderStroke(1.dp, StudioCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: Track labels and quick stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = CoralPink,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Línea de Tiempo",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${project.clips.size} clips • ${String.format("%.1f", project.totalDurationSec)}s",
                    color = TextSecondaryDark,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable Timeline Tracks Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF14131E), RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                ) {
                    // TRACK 1: Video Clips Track
                    Row(
                        modifier = Modifier.height(72.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        var accumSec = 0f
                        project.clips.forEachIndexed { index, clip ->
                            val isSelected = index == selectedClipIndex
                            val clipStart = accumSec
                            val clipWidthDp = (32.dp * clip.effectiveDuration).coerceIn(90.dp, 240.dp)
                            accumSec += clip.effectiveDuration

                            Box(
                                modifier = Modifier
                                    .width(clipWidthDp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(clip.stockScene.primaryColorHex),
                                                Color(clip.stockScene.secondaryColorHex)
                                            )
                                        )
                                    )
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) CoralPink else Color.White.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        onSelectClip(index)
                                        onSeek(clipStart)
                                    }
                                    .padding(6.dp)
                                    .testTag("timeline_clip_$index")
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${index + 1}. ${clip.name}",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        if (isSelected && project.clips.size > 1) {
                                            IconButton(
                                                onClick = { onDeleteClip(index) },
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Eliminar Clip",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Surface(
                                            color = Color.Black.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "${String.format("%.1f", clip.effectiveDuration)}s",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }

                                        if (clip.filter != VideoFilter.ORIGINAL) {
                                            Surface(
                                                color = NeonCyan.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "FX",
                                                    color = NeonCyan,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Add Clip Button
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(StudioSurfaceDark)
                                .border(1.dp, CoralPink.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { onAddClipClick() }
                                .testTag("add_clip_timeline_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Agregar Clip",
                                    tint = CoralPink,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text("Añadir", color = CoralPink, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // TRACK 2: Text Overlays Track
                    if (project.textOverlays.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .height(26.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Title,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            project.textOverlays.forEach { textOverlay ->
                                val textWidth = (28.dp * (textOverlay.endTimeSec - textOverlay.startTimeSec)).coerceIn(60.dp, 160.dp)
                                Surface(
                                    modifier = Modifier
                                        .width(textWidth)
                                        .fillMaxHeight(),
                                    color = NeonCyan.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = textOverlay.text,
                                        color = NeonCyan,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // TRACK 3: Audio / Music Track
                    project.audioTrack?.let { audio ->
                        Row(
                            modifier = Modifier
                                .height(26.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = ElectricViolet,
                                modifier = Modifier.size(16.dp)
                            )
                            Surface(
                                modifier = Modifier
                                    .width((32.dp * project.totalDurationSec).coerceIn(120.dp, 400.dp))
                                    .fillMaxHeight(),
                                color = ElectricViolet.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "♫ ${audio.title} (${audio.bpm} BPM)",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
