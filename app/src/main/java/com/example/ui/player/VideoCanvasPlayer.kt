package com.example.ui.player

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import kotlin.math.*

@Composable
fun VideoCanvasPlayer(
    project: VideoProject,
    currentTimeSec: Float,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalDuration = project.totalDurationSec

    // Cache loaded clip bitmaps from local device URIs safely
    val loadedBitmaps = remember { mutableStateMapOf<String, ImageBitmap>() }

    LaunchedEffect(project.clips) {
        withContext(Dispatchers.IO) {
            for (clip in project.clips) {
                val uriStr = clip.uri ?: continue
                if (loadedBitmaps.containsKey(uriStr)) continue
                try {
                    val uri = Uri.parse(uriStr)
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val bmp: Bitmap? = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    bmp?.let {
                        val scaled = Bitmap.createScaledBitmap(it, 720, (720 * (it.height.toFloat() / it.width)).toInt().coerceIn(400, 1280), true)
                        loadedBitmaps[uriStr] = scaled.asImageBitmap()
                    }
                } catch (_: Exception) {}
            }
        }
    }

    // Determine current active clip and local clip progress
    val clipInfo = remember(project.clips, currentTimeSec) {
        findActiveClip(project.clips, currentTimeSec)
    }

    val activeClip = clipInfo?.first
    val clipLocalTime = clipInfo?.second ?: 0f

    // Subtle breathing animation for stock scenes and pulsing stickers
    val infiniteTransition = rememberInfiniteTransition(label = "player_infinite")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    // Aspect ratio container
    val ratioValue = project.aspectRatio.ratioWidth / project.aspectRatio.ratioHeight

    Card(
        modifier = modifier
            .testTag("video_player_card")
            .shadow(12.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Actual video preview viewport
            Box(
                modifier = Modifier
                    .aspectRatio(ratioValue)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0A0910))
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { onTogglePlay() })
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("video_canvas_view")
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    if (activeClip != null) {
                        // Render clip transition (first 0.5s of clip)
                        val transitionDuration = 0.5f
                        val transitionProgress = (clipLocalTime / transitionDuration).coerceIn(0f, 1f)
                        val transition = activeClip.transitionIn

                        var canvasAlpha = 1.0f
                        var offsetX = 0f
                        var scale = 1.0f
                        var isFlash = false

                        if (clipLocalTime < transitionDuration && transition != TransitionType.NONE) {
                            when (transition) {
                                TransitionType.FADE_BLACK -> canvasAlpha = transitionProgress
                                TransitionType.FLASH_WHITE -> isFlash = transitionProgress < 0.6f
                                TransitionType.ZOOM_IN -> scale = 1.3f - (0.3f * transitionProgress)
                                TransitionType.SLIDE_LEFT -> offsetX = -canvasWidth * (1f - transitionProgress)
                                TransitionType.SLIDE_RIGHT -> offsetX = canvasWidth * (1f - transitionProgress)
                                TransitionType.DISSOLVE -> canvasAlpha = 0.3f + (0.7f * transitionProgress)
                                else -> {}
                            }
                        }

                        // Clip URI ImageBitmap or Stock Graphic
                        val userBmp = activeClip.uri?.let { loadedBitmaps[it] }

                        if (userBmp != null) {
                            drawUserBitmap(userBmp, canvasWidth, canvasHeight, activeClip.filter, offsetX, scale, canvasAlpha)
                        } else {
                            drawStockMotionScene(
                                activeClip.stockScene,
                                activeClip.filter,
                                clipLocalTime,
                                canvasWidth,
                                canvasHeight,
                                pulseAnim,
                                offsetX,
                                scale,
                                canvasAlpha
                            )
                        }

                        if (isFlash) {
                            drawRect(
                                color = Color.White.copy(alpha = 0.85f * (1f - transitionProgress)),
                                size = size
                            )
                        }

                        // Apply Color Grading / Filter Overlay
                        applyFilterGrade(activeClip.filter, canvasWidth, canvasHeight, pulseAnim)
                    } else {
                        // Fallback when no clip
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF1E1B2E), Color(0xFF0F0E17))),
                            size = size
                        )
                    }

                    // Render Active Text Overlays
                    for (textOverlay in project.textOverlays) {
                        if (currentTimeSec in textOverlay.startTimeSec..textOverlay.endTimeSec) {
                            val elapsed = currentTimeSec - textOverlay.startTimeSec
                            val textDuration = textOverlay.endTimeSec - textOverlay.startTimeSec
                            drawAnimatedTextOverlay(
                                textOverlay = textOverlay,
                                elapsed = elapsed,
                                totalDuration = textDuration,
                                canvasWidth = canvasWidth,
                                canvasHeight = canvasHeight
                            )
                        }
                    }

                    // Render Active Stickers
                    for (sticker in project.stickers) {
                        if (currentTimeSec in sticker.startTimeSec..sticker.endTimeSec) {
                            val elapsed = currentTimeSec - sticker.startTimeSec
                            drawAnimatedSticker(
                                sticker = sticker,
                                elapsed = elapsed,
                                canvasWidth = canvasWidth,
                                canvasHeight = canvasHeight,
                                pulse = pulseAnim
                            )
                        }
                    }

                    // Watermark / Cinema aspect frame badge
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.argb(120, 255, 255, 255)
                            textSize = 28f
                            isAntiAlias = true
                        }
                        drawText("VIDEOCRAFT", 24f, 44f, paint)
                    }
                }

                // Play / Pause Overlay Icon button when paused or tapped
                if (!isPlaying) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .clickable { onTogglePlay() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Active Filter Pill indicator at top right
                if (activeClip != null && activeClip.filter != VideoFilter.ORIGINAL) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = activeClip.filter.displayName,
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Bottom playback status & scrub bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("play_pause_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    tint = CoralPink
                )
            }

            // Time text: 00:03.2 / 00:08.0
            Text(
                text = "${formatTime(currentTimeSec)} / ${formatTime(totalDuration)}",
                color = TextPrimaryDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Audio track indicator badge
            project.audioTrack?.let { track ->
                Surface(
                    color = ElectricViolet.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = ElectricViolet,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = track.title,
                            color = Color.White,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            // Aspect ratio indicator
            Surface(
                color = StudioSurfaceElevated,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = project.aspectRatio.tag,
                    color = TextSecondaryDark,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Scrub slider
        Slider(
            value = currentTimeSec.coerceIn(0f, totalDuration),
            onValueChange = { onSeek(it) },
            valueRange = 0f..totalDuration.coerceAtLeast(0.1f),
            colors = SliderDefaults.colors(
                thumbColor = CoralPink,
                activeTrackColor = CoralPink,
                inactiveTrackColor = Color.DarkGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 0.dp)
                .testTag("timeline_scrubber_slider")
        )
    }
}

private fun DrawScope.drawUserBitmap(
    bitmap: ImageBitmap,
    w: Float,
    h: Float,
    filter: VideoFilter,
    offsetX: Float,
    scale: Float,
    alpha: Float
) {
    val srcW = bitmap.width.toFloat()
    val srcH = bitmap.height.toFloat()

    // Fit-center or crop-fill
    val scaleFactor = max(w / srcW, h / srcH) * scale
    val scaledW = srcW * scaleFactor
    val scaledH = srcH * scaleFactor
    val left = (w - scaledW) / 2f + offsetX
    val top = (h - scaledH) / 2f

    clipRect(0f, 0f, w, h) {
        drawImage(
            image = bitmap,
            srcOffset = androidx.compose.ui.unit.IntOffset(0, 0),
            srcSize = androidx.compose.ui.unit.IntSize(bitmap.width, bitmap.height),
            dstOffset = androidx.compose.ui.unit.IntOffset(left.toInt(), top.toInt()),
            dstSize = androidx.compose.ui.unit.IntSize(scaledW.toInt(), scaledH.toInt()),
            alpha = alpha
        )
    }
}

private fun DrawScope.drawStockMotionScene(
    stockScene: StockSceneType,
    filter: VideoFilter,
    localTime: Float,
    w: Float,
    h: Float,
    pulse: Float,
    offsetX: Float,
    scale: Float,
    alpha: Float
) {
    val col1 = Color(stockScene.primaryColorHex)
    val col2 = Color(stockScene.secondaryColorHex)

    // Animated dynamic gradient motion
    val animOffset = (localTime * 40f) % h
    val brush = Brush.verticalGradient(
        colors = listOf(col1, col2, col1),
        startY = animOffset,
        endY = animOffset + h
    )

    clipRect(0f, 0f, w, h) {
        drawRect(brush = brush, alpha = alpha)

        // Geometric elements based on scene type
        when (stockScene) {
            StockSceneType.NEON_PULSE -> {
                val circleRadius = (min(w, h) * 0.35f * (0.9f + pulse * 0.2f)) * scale
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFFFF3B5C).copy(alpha = 0.7f), Color.Transparent),
                        center = Offset(w / 2f + offsetX, h / 2f),
                        radius = circleRadius
                    ),
                    radius = circleRadius,
                    center = Offset(w / 2f + offsetX, h / 2f)
                )
            }
            StockSceneType.SUNSET_GRADIENT, StockSceneType.GOLDEN_HOUR -> {
                val sunY = h * 0.6f + sin(localTime * 0.5f) * 20f
                drawCircle(
                    color = Color(0xFFFFE082).copy(alpha = 0.85f * alpha),
                    radius = (min(w, h) * 0.22f) * scale,
                    center = Offset(w / 2f + offsetX, sunY)
                )
                // Horizon reflection lines
                for (i in 1..5) {
                    val lineY = sunY + (min(w, h) * 0.22f) + (i * 24f)
                    if (lineY < h) {
                        drawLine(
                            color = Color(0xFFFFD54F).copy(alpha = 0.4f * alpha),
                            start = Offset(w * 0.2f, lineY),
                            end = Offset(w * 0.8f, lineY),
                            strokeWidth = 3f * i
                        )
                    }
                }
            }
            StockSceneType.RETRO_GRID -> {
                // Synthwave grid lines
                val horizon = h * 0.55f
                drawLine(color = Color(0xFFFF007F), start = Offset(0f, horizon), end = Offset(w, horizon), strokeWidth = 3f)
                for (step in 1..8) {
                    val gridY = horizon + (step * step * 7f)
                    if (gridY < h) {
                        drawLine(color = Color(0xFF00E5FF).copy(alpha = 0.5f), start = Offset(0f, gridY), end = Offset(w, gridY), strokeWidth = 2f)
                    }
                }
            }
            else -> {
                // Diagonal aesthetic ambient light flare
                val flareCenter = Offset(w * 0.3f + (localTime * 20f) % w, h * 0.3f)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.4f * alpha), Color.Transparent),
                        center = flareCenter,
                        radius = w * 0.5f
                    ),
                    radius = w * 0.5f,
                    center = flareCenter
                )
            }
        }
    }
}

private fun DrawScope.applyFilterGrade(filter: VideoFilter, w: Float, h: Float, pulse: Float) {
    when (filter) {
        VideoFilter.CINEMATIC -> {
            // Teal shadow, amber highlights overlay + vignette
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF003049).copy(alpha = 0.25f), // Teal
                        Color(0xFFF77F00).copy(alpha = 0.18f)  // Amber
                    )
                ),
                blendMode = BlendMode.Overlay
            )
            // Subtle cinematic vignette
            drawRect(
                brush = Brush.radialGradient(
                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                    center = Offset(w / 2f, h / 2f),
                    radius = max(w, h) * 0.7f
                )
            )
        }
        VideoFilter.VINTAGE_VHS -> {
            // Scanlines effect
            for (y in 0 until h.toInt() step 8) {
                drawLine(
                    color = Color.Black.copy(alpha = 0.18f),
                    start = Offset(0f, y.toFloat()),
                    end = Offset(w, y.toFloat()),
                    strokeWidth = 2f
                )
            }
            // VHS grain tint
            drawRect(color = Color(0xFF553311).copy(alpha = 0.15f), blendMode = BlendMode.ColorBurn)
        }
        VideoFilter.FILM_NOIR -> {
            // High contrast b&w
            drawRect(color = Color.Black.copy(alpha = 0.5f), blendMode = BlendMode.Color)
            drawRect(
                brush = Brush.radialGradient(
                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                    center = Offset(w / 2f, h / 2f),
                    radius = min(w, h) * 0.8f
                )
            )
        }
        VideoFilter.CYBER_NEON -> {
            drawRect(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF8A2387).copy(alpha = 0.3f), Color(0xFF00E5FF).copy(alpha = 0.25f))
                ),
                blendMode = BlendMode.Screen
            )
        }
        VideoFilter.WARM_SUNSET -> {
            drawRect(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFFB300).copy(alpha = 0.35f), Color(0xFFFF5722).copy(alpha = 0.2f)),
                    center = Offset(w * 0.5f, h * 0.3f),
                    radius = w * 0.8f
                ),
                blendMode = BlendMode.Softlight
            )
        }
        VideoFilter.ULTRA_VIVID -> {
            drawRect(color = Color(0xFFFF0055).copy(alpha = 0.15f), blendMode = BlendMode.ColorDodge)
        }
        VideoFilter.SEPIA -> {
            drawRect(color = Color(0xFF704214).copy(alpha = 0.35f), blendMode = BlendMode.Color)
        }
        VideoFilter.GLITCH -> {
            // Horizontal glitch slices
            val glitchY = (h * ((pulse * 3f) % 1f))
            drawRect(
                color = Color(0xFF00FFFF).copy(alpha = 0.4f),
                topLeft = Offset(10f, glitchY),
                size = Size(w - 20f, 14f)
            )
            drawRect(
                color = Color(0xFFFF007F).copy(alpha = 0.4f),
                topLeft = Offset(-10f, glitchY + 8f),
                size = Size(w, 10f)
            )
        }
        VideoFilter.ORIGINAL -> {}
    }
}

private fun DrawScope.drawAnimatedTextOverlay(
    textOverlay: TextOverlay,
    elapsed: Float,
    totalDuration: Float,
    canvasWidth: Float,
    canvasHeight: Float
) {
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            color = textOverlay.textColorHex.toInt()
            textSize = (textOverlay.fontSizeSp * 2.2f).coerceIn(32f, 72f)
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setShadowLayer(12f, 0f, 4f, android.graphics.Color.BLACK)
        }

        // Handle animation: POP, TYPEWRITER, FADE, NEON_PULSE, BOUNCE
        var displayText = textOverlay.text
        var alphaMultiplier = 1.0f
        var yOffset = 0f

        when (textOverlay.animation) {
            TextAnimation.TYPEWRITER -> {
                val charsToShow = ((elapsed / (totalDuration * 0.7f)) * textOverlay.text.length)
                    .toInt().coerceIn(1, textOverlay.text.length)
                displayText = textOverlay.text.take(charsToShow)
            }
            TextAnimation.POP -> {
                if (elapsed < 0.3f) {
                    val progress = elapsed / 0.3f
                    paint.textSize = paint.textSize * (0.6f + (0.4f * progress))
                }
            }
            TextAnimation.FADE -> {
                if (elapsed < 0.4f) {
                    alphaMultiplier = elapsed / 0.4f
                } else if (elapsed > totalDuration - 0.4f) {
                    alphaMultiplier = (totalDuration - elapsed) / 0.4f
                }
            }
            TextAnimation.BOUNCE -> {
                yOffset = sin(elapsed * 6f) * 16f
            }
            TextAnimation.NEON_PULSE -> {
                val glow = (sin(elapsed * 8f) + 1f) / 2f
                paint.setShadowLayer(24f * glow + 4f, 0f, 0f, android.graphics.Color.parseColor("#00E5FF"))
            }
            TextAnimation.NONE -> {}
        }

        paint.alpha = (paint.alpha * alphaMultiplier.coerceIn(0f, 1f)).toInt()

        val centerX = canvasWidth / 2f
        val centerY = (canvasHeight * textOverlay.positionYPercent) + yOffset

        // Background badge if enabled
        textOverlay.backgroundColorHex?.let { bgHex ->
            val bgPaint = android.graphics.Paint().apply {
                color = bgHex.toInt()
                isAntiAlias = true
            }
            val textWidth = paint.measureText(displayText)
            val padX = 24f
            val padY = 16f
            val rect = android.graphics.RectF(
                centerX - textWidth / 2f - padX,
                centerY - paint.textSize + padY,
                centerX + textWidth / 2f + padX,
                centerY + padY * 2
            )
            canvas.nativeCanvas.drawRoundRect(rect, 16f, 16f, bgPaint)
        }

        canvas.nativeCanvas.drawText(displayText, centerX, centerY, paint)
    }
}

private fun DrawScope.drawAnimatedSticker(
    sticker: StickerOverlay,
    elapsed: Float,
    canvasWidth: Float,
    canvasHeight: Float,
    pulse: Float
) {
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            textSize = (64f * sticker.scale * (1f + sin(elapsed * 4f) * 0.08f)).coerceIn(36f, 120f)
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
        val x = canvasWidth * sticker.posXPercent
        val y = canvasHeight * sticker.posYPercent
        canvas.nativeCanvas.drawText(sticker.emoji, x, y, paint)
    }
}

private fun findActiveClip(clips: List<VideoClip>, timeSec: Float): Pair<VideoClip, Float>? {
    var accumulated = 0f
    for (clip in clips) {
        val effective = clip.effectiveDuration
        if (timeSec in accumulated..(accumulated + effective)) {
            val localTime = (timeSec - accumulated) * clip.speed
            return Pair(clip, localTime)
        }
        accumulated += effective
    }
    return clips.lastOrNull()?.let { Pair(it, it.effectiveDuration) }
}

private fun formatTime(sec: Float): String {
    val totalSec = sec.toInt().coerceAtLeast(0)
    val minutes = totalSec / 60
    val seconds = totalSec % 60
    val millis = ((sec - totalSec) * 10).toInt().coerceIn(0, 9)
    return String.format("%02d:%02d.%d", minutes, seconds, millis)
}
