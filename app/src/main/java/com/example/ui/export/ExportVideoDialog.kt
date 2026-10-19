package com.example.ui.export

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ExportVideoDialog(
    project: VideoProject,
    onDismiss: () -> Unit,
    onExportComplete: (VideoProject) -> Unit
) {
    val context = LocalContext.current
    var selectedRes by remember { mutableStateOf("1080p Full HD") }
    var selectedFps by remember { mutableIntStateOf(30) }
    var renderState by remember { mutableStateOf<RenderProgressState>(RenderProgressState.Idle) }

    val resolutions = listOf("720p HD", "1080p Full HD", "4K Ultra")
    val fpsOptions = listOf(24, 30, 60)

    val estimatedMb = remember(selectedRes, selectedFps, project.totalDurationSec) {
        val baseRate = when (selectedRes) {
            "720p HD" -> 0.8f
            "1080p Full HD" -> 1.8f
            else -> 4.5f
        }
        val fpsFactor = selectedFps / 30f
        val mb = project.totalDurationSec * baseRate * fpsFactor * 0.15f
        String.format("%.1f MB", mb.coerceAtLeast(1.2f))
    }

    LaunchedEffect(renderState) {
        if (renderState is RenderProgressState.Processing) {
            // Simulated stage rendering
            val stages = listOf(
                "Decodificando fotogramas de clips..." to 0.25f,
                "Aplicando gradación de color y filtros..." to 0.55f,
                "Sintetizando banda sonora y mezclas..." to 0.80f,
                "Codificando contenedor MP4 H.264..." to 1.0f
            )

            for ((stageText, progressTarget) in stages) {
                var currentP = (renderState as RenderProgressState.Processing).progress
                while (currentP < progressTarget) {
                    delay(120)
                    currentP += 0.05f
                    renderState = RenderProgressState.Processing(currentP.coerceAtMost(1f), stageText)
                }
            }

            delay(200)
            val updated = project.copy(
                isExported = true,
                exportedUri = "videocraft_export_${project.id.take(6)}.mp4",
                updatedAt = System.currentTimeMillis()
            )
            onExportComplete(updated)
            renderState = RenderProgressState.Success(
                projectTitle = project.title,
                filePath = "videocraft_export_${project.id.take(6)}.mp4",
                durationSec = project.totalDurationSec
            )
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (renderState !is RenderProgressState.Processing) onDismiss()
        },
        containerColor = StudioSurfaceElevated,
        title = {
            Text(
                text = when (renderState) {
                    is RenderProgressState.Processing -> "Exportando Video..."
                    is RenderProgressState.Success -> "¡Video Listo!"
                    else -> "Configuración de Exportación"
                },
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                when (val state = renderState) {
                    RenderProgressState.Idle -> {
                        // Resolution selector
                        Text("Resolución:", color = TextSecondaryDark, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            resolutions.forEach { res ->
                                val isSel = res == selectedRes
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedRes = res },
                                    color = if (isSel) CoralPink.copy(alpha = 0.25f) else StudioSurfaceDark,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (isSel) CoralPink else StudioCardBorder)
                                ) {
                                    Text(
                                        text = res,
                                        color = if (isSel) CoralPink else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // FPS selector
                        Text("Velocidad de Fotogramas (FPS):", color = TextSecondaryDark, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            fpsOptions.forEach { fps ->
                                val isSel = fps == selectedFps
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedFps = fps },
                                    color = if (isSel) ElectricViolet.copy(alpha = 0.25f) else StudioSurfaceDark,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (isSel) ElectricViolet else StudioCardBorder)
                                ) {
                                    Text(
                                        text = when (fps) {
                                            24 -> "24 Cine"
                                            60 -> "60 Suave"
                                            else -> "30 Normal"
                                        },
                                        color = if (isSel) ElectricViolet else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Summary Box
                        Card(
                            colors = CardDefaults.cardColors(containerColor = StudioSurfaceDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Tamaño Estimado:", color = TextSecondaryDark, fontSize = 11.sp)
                                    Text(estimatedMb, color = NeonCyan, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text("Duración Total:", color = TextSecondaryDark, fontSize = 11.sp)
                                    Text(
                                        "${String.format("%.1f", project.totalDurationSec)} segundos",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    is RenderProgressState.Processing -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LinearProgressIndicator(
                                progress = { state.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = CoralPink,
                                trackColor = StudioSurfaceDark
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "${(state.progress * 100).toInt()}%",
                                color = CoralPink,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.currentStage,
                                color = TextSecondaryDark,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    is RenderProgressState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(MintGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MintGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = state.projectTitle,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Guardado en Galería de VideoCraft • ${String.format("%.1f", state.durationSec)}s",
                                color = TextSecondaryDark,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "¡Mira el video que acabo de crear con VideoCraft! 🎬🔥 #${state.projectTitle.replace(" ", "")}"
                                        )
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Compartir video con")
                                    context.startActivity(shareIntent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("share_exported_video_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Compartir con Amigos / Redes")
                            }
                        }
                    }
                    is RenderProgressState.Error -> {
                        Text(state.message, color = CoralPink)
                    }
                }
            }
        },
        confirmButton = {
            when (renderState) {
                RenderProgressState.Idle -> {
                    Button(
                        onClick = {
                            renderState = RenderProgressState.Processing(0.05f, "Iniciando render...")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                        modifier = Modifier.testTag("start_export_button")
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Renderizar Video")
                    }
                }
                is RenderProgressState.Success -> {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceDark)
                    ) {
                        Text("Listo", color = Color.White)
                    }
                }
                else -> {}
            }
        },
        dismissButton = {
            if (renderState is RenderProgressState.Idle) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = TextSecondaryDark)
                }
            }
        }
    )
}
