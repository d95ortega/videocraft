package com.example.ui.movie

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.player.CinemaTheaterPlayer
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CinemaTheaterScreen(
    movie: AiMovieProject,
    onOpenInEditor: (AiMovieProject) -> Unit,
    onExportMovie: (AiMovieProject) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTimeSec by remember { mutableFloatStateOf(0f) }
    var isPlaying by remember { mutableStateOf(false) }

    val totalDuration = movie.totalDurationSec

    // Playback loop
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            val stepMs = 50L
            val stepSec = 0.05f
            while (isPlaying && currentTimeSec < totalDuration) {
                delay(stepMs)
                val next = currentTimeSec + stepSec
                if (next >= totalDuration) {
                    currentTimeSec = 0f
                    isPlaying = false
                } else {
                    currentTimeSec = next
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(14.dp)
            .testTag("cinema_theater_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Main Cinema Theater Screen Player
        item {
            CinemaTheaterPlayer(
                movie = movie,
                currentTimeSec = currentTimeSec,
                isPlaying = isPlaying,
                onTogglePlay = { isPlaying = !isPlaying },
                onSeek = { currentTimeSec = it },
                onOpenInEditor = { onOpenInEditor(movie) }
            )
        }

        // Movie Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, StudioCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = movie.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            color = CoralPink.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${String.format("%.1f", totalDuration)}s",
                                color = CoralPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${movie.tagline}\"",
                        color = BrightAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = movie.synopsis,
                        color = TextSecondaryDark,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons Row (Edit in Studio, Share, Export)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onOpenInEditor(movie) },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.MovieCreation, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Editar Escenas", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "🎬 ¡Mira la película que generé con IA en VideoCraft!\n\n" +
                                                "\"${movie.title}\"\n${movie.tagline}\n\n" +
                                                "Sinopsis: ${movie.synopsis}"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Compartir película"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceDark),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, StudioCardBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Compartir", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section Title: Scenes Breakdown
        item {
            Text(
                text = "Escenas y Dirección Cinematográfica (${movie.scenes.size} Actos)",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Scene items
        items(movie.scenes) { scene ->
            var startSec = 0f
            for (s in movie.scenes) {
                if (s.id == scene.id) break
                startSec += s.durationSec
            }

            val isCurrent = currentTimeSec in startSec..(startSec + scene.durationSec)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        currentTimeSec = startSec
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) CoralPink.copy(alpha = 0.15f) else StudioSurfaceElevated
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    if (isCurrent) 1.5.dp else 1.dp,
                    if (isCurrent) CoralPink else StudioCardBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isCurrent) CoralPink else StudioSurfaceDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${scene.sceneNumber}",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = scene.actName,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${scene.shotType} • ${scene.cameraMotion.displayName}",
                            color = NeonCyan,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${scene.speaker}: \"${scene.dialogue}\"",
                            color = TextSecondaryDark,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = "${String.format("%.1f", scene.durationSec)}s",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
