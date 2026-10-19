package com.example.ui.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun CinemaTheaterPlayer(
    movie: AiMovieProject,
    currentTimeSec: Float,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    onOpenInEditor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalDuration = movie.totalDurationSec

    // Identify active scene
    val sceneInfo = remember(movie.scenes, currentTimeSec) {
        findActiveMovieScene(movie.scenes, currentTimeSec)
    }
    val currentScene = sceneInfo?.first ?: movie.scenes.firstOrNull()
    val sceneLocalTime = sceneInfo?.second ?: 0f

    val infiniteTransition = rememberInfiniteTransition(label = "theater_anim")
    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(18.dp))
            .testTag("cinema_theater_card"),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, StudioCardBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Movie Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StudioSurfaceDark)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Theaters,
                        contentDescription = null,
                        tint = BrightAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = movie.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Surface(
                    color = BrightAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = movie.genre.displayName,
                        color = BrightAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Cinema Screen Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color(0xFF07060A))
                    .clickable { onTogglePlay() },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    if (currentScene != null) {
                        // Calculate Camera Motion Transform (Ken Burns motion)
                        val progress = (sceneLocalTime / currentScene.durationSec).coerceIn(0f, 1f)
                        var scale = 1.0f
                        var panX = 0f

                        when (currentScene.cameraMotion) {
                            CameraMotion.SLOW_ZOOM_IN -> scale = 1.0f + (0.18f * progress)
                            CameraMotion.SLOW_ZOOM_OUT -> scale = 1.18f - (0.18f * progress)
                            CameraMotion.SLOW_PAN_RIGHT -> panX = w * 0.12f * (progress - 0.5f)
                            CameraMotion.SLOW_PAN_LEFT -> panX = -w * 0.12f * (progress - 0.5f)
                            CameraMotion.DOLLY_PUSH -> scale = 1.05f + (0.22f * progress)
                            CameraMotion.AERIAL_SWEEP -> {
                                scale = 1.1f
                                panX = sin(progress * Math.PI.toFloat()) * (w * 0.08f)
                            }
                            CameraMotion.STATIC_STILL -> {}
                        }

                        // Draw Background Cinematic Scenery
                        drawCinematicScene(
                            scene = currentScene,
                            w = w,
                            h = h,
                            scale = scale,
                            panX = panX,
                            progress = progress,
                            particlePhase = particlePhase
                        )

                        // Draw Atmosphere Effects (Rain, Embers, Stars, Smoke)
                        drawAtmosphereVfx(
                            atmo = currentScene.atmosphere,
                            w = w,
                            h = h,
                            phase = particlePhase
                        )

                        // 2.39:1 Cinema Anamorphic Letterbox Bars
                        val letterboxHeight = h * 0.08f
                        drawRect(color = Color.Black, size = Size(w, letterboxHeight), topLeft = Offset(0f, 0f))
                        drawRect(color = Color.Black, size = Size(w, letterboxHeight), topLeft = Offset(0f, h - letterboxHeight))
                    }
                }

                // Play / Pause Overlay Icon when paused
                if (!isPlaying) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(1.dp, CoralPink.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Reproducir",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                // Top Scene Act Label HUD
                currentScene?.let { sc ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text(
                                text = sc.actName.uppercase(),
                                color = BrightAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = sc.shotType,
                                color = TextSecondaryDark,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // Subtitle Overlay at Bottom
                currentScene?.let { sc ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(0.92f)
                            .padding(bottom = 18.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "[ ${sc.speaker} ]",
                                color = BrightAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "\"${sc.dialogue}\"",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Bottom Controls Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StudioSurfaceDark)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = CoralPink
                    )
                }

                Text(
                    text = "${formatTime(currentTimeSec)} / ${formatTime(totalDuration)}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Button(
                    onClick = onOpenInEditor,
                    colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceElevated),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, CoralPink.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = CoralPink, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar en Línea de Tiempo", fontSize = 11.sp, color = Color.White)
                }
            }

            // Scrub slider
            Slider(
                value = currentTimeSec.coerceIn(0f, totalDuration),
                onValueChange = { onSeek(it) },
                valueRange = 0f..totalDuration.coerceAtLeast(0.1f),
                colors = SliderDefaults.colors(
                    thumbColor = BrightAmber,
                    activeTrackColor = BrightAmber,
                    inactiveTrackColor = Color.DarkGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 0.dp)
            )
        }
    }
}

private fun DrawScope.drawCinematicScene(
    scene: MovieScene,
    w: Float,
    h: Float,
    scale: Float,
    panX: Float,
    progress: Float,
    particlePhase: Float
) {
    val col1 = Color(scene.primaryColorHex)
    val col2 = Color(scene.secondaryColorHex)

    // Animated dynamic gradient with motion
    val brush = Brush.linearGradient(
        colors = listOf(col1, col2, col1),
        start = Offset(panX, 0f),
        end = Offset(w + panX, h)
    )

    clipRect(0f, 0f, w, h) {
        drawRect(brush = brush)

        // Celestial body / Sun / Portal
        val sphereRadius = (min(w, h) * 0.28f) * scale
        val sphereCenter = Offset(w * 0.5f + panX, h * 0.45f)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color.White.copy(alpha = 0.75f), Color(scene.primaryColorHex).copy(alpha = 0.2f), Color.Transparent),
                center = sphereCenter,
                radius = sphereRadius
            ),
            radius = sphereRadius,
            center = sphereCenter
        )

        // Geometric silhouettes / horizon skyline
        val horizonY = h * 0.65f
        for (i in 0 until 12) {
            val blockW = w * 0.1f
            val blockX = (i * blockW * 0.9f) + panX * 0.3f
            val blockH = (sin((i * 1.5).toDouble()) * 40f + 70f).toFloat()
            drawRect(
                color = Color.Black.copy(alpha = 0.55f),
                topLeft = Offset(blockX, horizonY - blockH),
                size = Size(blockW, blockH + (h - horizonY))
            )
        }

        // Vignette
        drawRect(
            brush = Brush.radialGradient(
                listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                center = Offset(w / 2f, h / 2f),
                radius = max(w, h) * 0.75f
            )
        )
    }
}

private fun DrawScope.drawAtmosphereVfx(
    atmo: AtmosphereEffect,
    w: Float,
    h: Float,
    phase: Float
) {
    when (atmo) {
        AtmosphereEffect.CYBER_RAIN -> {
            for (i in 0..40) {
                val startX = (w * ((i * 0.025f + phase * 0.2f) % 1f))
                val startY = (h * ((i * 0.035f + phase * 1.5f) % 1f))
                drawLine(
                    color = Color(0xFF00E5FF).copy(alpha = 0.4f),
                    start = Offset(startX, startY),
                    end = Offset(startX - 6f, startY + 22f),
                    strokeWidth = 1.5f
                )
            }
        }
        AtmosphereEffect.GLOWING_EMBERS -> {
            for (i in 0..30) {
                val emberX = (w * ((i * 0.033f + sin(phase * 4f + i) * 0.05f) % 1f))
                val emberY = h - (h * ((i * 0.035f + phase * 0.8f) % 1f))
                drawCircle(
                    color = Color(0xFFFFB300).copy(alpha = 0.65f),
                    radius = (2f + (i % 3)),
                    center = Offset(emberX, emberY)
                )
            }
        }
        AtmosphereEffect.SPACE_STARS -> {
            for (i in 0..50) {
                val starX = (w * (i * 0.02f))
                val starY = (h * ((i * 0.03f * (i % 7)) % 1f))
                val starAlpha = (sin((phase * 6f + i).toDouble()) + 1f) / 2f
                drawCircle(
                    color = Color.White.copy(alpha = (starAlpha * 0.7f).toFloat()),
                    radius = if (i % 5 == 0) 2.5f else 1.2f,
                    center = Offset(starX, starY)
                )
            }
        }
        AtmosphereEffect.LENS_FLARE -> {
            val flareCenter = Offset(w * 0.5f, h * 0.45f)
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, Color(0xFF00E5FF).copy(alpha = 0.75f), Color.Transparent)
                ),
                start = Offset(0f, flareCenter.y),
                end = Offset(w, flareCenter.y),
                strokeWidth = 3f
            )
        }
        AtmosphereEffect.FOG_SMOKE -> {
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color.Transparent, Color.White.copy(alpha = 0.15f), Color.Transparent),
                    startY = h * 0.4f,
                    endY = h
                )
            )
        }
        AtmosphereEffect.NONE -> {}
    }
}

private fun findActiveMovieScene(scenes: List<MovieScene>, timeSec: Float): Pair<MovieScene, Float>? {
    var accumulated = 0f
    for (scene in scenes) {
        if (timeSec in accumulated..(accumulated + scene.durationSec)) {
            val localTime = timeSec - accumulated
            return Pair(scene, localTime)
        }
        accumulated += scene.durationSec
    }
    return scenes.lastOrNull()?.let { Pair(it, it.durationSec) }
}

private fun formatTime(sec: Float): String {
    val totalSec = sec.toInt().coerceAtLeast(0)
    val minutes = totalSec / 60
    val seconds = totalSec % 60
    return String.format("%02d:%02d", minutes, seconds)
}
