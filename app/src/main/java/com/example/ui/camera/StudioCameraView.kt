package com.example.ui.camera

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.model.StockSceneType
import com.example.model.VideoClip
import com.example.model.VideoFilter
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun StudioCameraView(
    onClipRecorded: (VideoClip) -> Unit,
    modifier: Modifier = Modifier
) {
    var isRecording by remember { mutableStateOf(false) }
    var recordSeconds by remember { mutableFloatStateOf(0f) }
    var selectedFilter by remember { mutableStateOf(VideoFilter.CINEMATIC) }
    var isFlashOn by remember { mutableStateOf(false) }
    var cameraFacingFront by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    // Pulsing REC indicator animation
    val infiniteTransition = rememberInfiniteTransition(label = "rec_blink")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )

    // Recording timer coroutine
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordSeconds = 0f
            while (isRecording && recordSeconds < 15f) {
                delay(100)
                recordSeconds += 0.1f
            }
            if (recordSeconds >= 15f) {
                isRecording = false
                // Auto create clip on 15s limit
                onClipRecorded(
                    VideoClip(
                        name = "Toma Grabada (${String.format("%.1f", recordSeconds)}s)",
                        durationSec = recordSeconds,
                        endTrimSec = recordSeconds,
                        stockScene = if (cameraFacingFront) StockSceneType.NEON_PULSE else StockSceneType.GOLDEN_HOUR,
                        filter = selectedFilter
                    )
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("studio_camera_screen")
    ) {
        // Viewfinder Canvas Simulation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (cameraFacingFront) Color(0xFF1E1738) else Color(0xFF151824)
                )
                .border(2.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Viewfinder Grid Overlay
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)
                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)
                Spacer(modifier = Modifier.weight(1f))
            }

            // Top Camera HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // REC badge
                if (isRecording) {
                    Surface(
                        color = Color.Red.copy(alpha = blinkAlpha),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REC ${String.format("%.1f", recordSeconds)}s",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "ESTUDIO CÁMARA 4K",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Flash toggle
                IconButton(
                    onClick = { isFlashOn = !isFlashOn },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (isFlashOn) BrightAmber else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Center subject reticle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(1.5.dp, if (isRecording) CoralPink else NeonCyan.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isRecording) "Grabando..." else "Listo para Capturar",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }

            // Bottom camera controls bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filter Button
                IconButton(
                    onClick = { showFilterMenu = !showFilterMenu },
                    modifier = Modifier
                        .size(44.dp)
                        .background(StudioSurfaceDark, CircleShape)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "Filtro",
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Big Shutter / Record Button
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(6.dp)
                        .clickable {
                            if (isRecording) {
                                isRecording = false
                                val dur = recordSeconds.coerceAtLeast(1.5f)
                                onClipRecorded(
                                    VideoClip(
                                        name = "Toma Nueva (${String.format("%.1f", dur)}s)",
                                        durationSec = dur,
                                        endTrimSec = dur,
                                        stockScene = if (cameraFacingFront) StockSceneType.NEON_PULSE else StockSceneType.DARK_CINEMA,
                                        filter = selectedFilter
                                    )
                                )
                            } else {
                                isRecording = true
                            }
                        }
                        .testTag("camera_record_shutter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isRecording) 32.dp else 56.dp)
                            .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
                            .background(CoralPink)
                    )
                }

                // Flip Camera Button
                IconButton(
                    onClick = { cameraFacingFront = !cameraFacingFront },
                    modifier = Modifier
                        .size(44.dp)
                        .background(StudioSurfaceDark, CircleShape)
                ) {
                    Icon(
                        Icons.Default.FlipCameraAndroid,
                        contentDescription = "Girar Cámara",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
