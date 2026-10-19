package com.example.ui.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AudioCatalog
import com.example.model.*
import com.example.ui.theme.*

enum class EditorActiveTab(val label: String, val icon: ImageVector) {
    FILTERS("Filtros", Icons.Default.AutoAwesome),
    TEXT("Texto", Icons.Default.Title),
    TRANSITIONS("Transición", Icons.Default.CompareArrows),
    TRIM_SPEED("Ajustes", Icons.Default.Speed),
    AUDIO("Música", Icons.Default.MusicNote),
    STICKERS("Stickers", Icons.Default.EmojiEmotions),
    RATIO("Formato", Icons.Default.AspectRatio)
}

@Composable
fun EditorToolbarPanels(
    project: VideoProject,
    selectedClipIndex: Int,
    onUpdateClip: (Int, VideoClip) -> Unit,
    onAddTextOverlay: (TextOverlay) -> Unit,
    onRemoveTextOverlay: (String) -> Unit,
    onAddSticker: (StickerOverlay) -> Unit,
    onRemoveSticker: (String) -> Unit,
    onSelectAudio: (AudioTrack?) -> Unit,
    onPlaySfx: (Int) -> Unit,
    onChangeAspectRatio: (AspectRatioType) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(EditorActiveTab.FILTERS) }
    var showAddTextDialog by remember { mutableStateOf(false) }

    val currentClip = project.clips.getOrNull(selectedClipIndex) ?: project.clips.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioSurfaceDark)
            .padding(vertical = 8.dp)
    ) {
        // Horizontal Toolbar Navigation Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(EditorActiveTab.entries.toTypedArray()) { tab ->
                val isSelected = tab == activeTab
                FilterChip(
                    selected = isSelected,
                    onClick = { activeTab = tab },
                    label = { Text(tab.label, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isSelected) CoralPink else TextSecondaryDark
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CoralPink.copy(alpha = 0.2f),
                        selectedLabelColor = CoralPink,
                        containerColor = StudioSurfaceElevated,
                        labelColor = TextSecondaryDark
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) CoralPink else Color.Transparent
                    ),
                    modifier = Modifier.testTag("editor_tab_${tab.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tool Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = 220.dp)
                .padding(horizontal = 12.dp)
        ) {
            when (activeTab) {
                EditorActiveTab.FILTERS -> {
                    if (currentClip != null) {
                        FiltersPanel(
                            currentFilter = currentClip.filter,
                            onSelectFilter = { filter ->
                                onUpdateClip(selectedClipIndex, currentClip.copy(filter = filter))
                            }
                        )
                    }
                }
                EditorActiveTab.TEXT -> {
                    TextOverlaysPanel(
                        textOverlays = project.textOverlays,
                        onAddClick = { showAddTextDialog = true },
                        onRemove = onRemoveTextOverlay
                    )
                }
                EditorActiveTab.TRANSITIONS -> {
                    if (currentClip != null) {
                        TransitionsPanel(
                            currentTransition = currentClip.transitionIn,
                            onSelectTransition = { trans ->
                                onUpdateClip(selectedClipIndex, currentClip.copy(transitionIn = trans))
                            }
                        )
                    }
                }
                EditorActiveTab.TRIM_SPEED -> {
                    if (currentClip != null) {
                        TrimAndSpeedPanel(
                            clip = currentClip,
                            onUpdateClip = { updated ->
                                onUpdateClip(selectedClipIndex, updated)
                            }
                        )
                    }
                }
                EditorActiveTab.AUDIO -> {
                    AudioPanel(
                        currentTrack = project.audioTrack,
                        onSelectTrack = onSelectAudio,
                        onPlaySfx = onPlaySfx
                    )
                }
                EditorActiveTab.STICKERS -> {
                    StickersPanel(
                        stickers = project.stickers,
                        onAddSticker = onAddSticker,
                        onRemoveSticker = onRemoveSticker
                    )
                }
                EditorActiveTab.RATIO -> {
                    AspectRatioPanel(
                        currentRatio = project.aspectRatio,
                        onSelectRatio = onChangeAspectRatio
                    )
                }
            }
        }
    }

    if (showAddTextDialog) {
        AddTextDialog(
            maxDuration = project.totalDurationSec,
            onDismiss = { showAddTextDialog = false },
            onConfirm = { newOverlay ->
                onAddTextOverlay(newOverlay)
                showAddTextDialog = false
            }
        )
    }
}

@Composable
private fun FiltersPanel(
    currentFilter: VideoFilter,
    onSelectFilter: (VideoFilter) -> Unit
) {
    Column {
        Text(
            text = "Filtro Seleccionado: ${currentFilter.displayName}",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(VideoFilter.entries.toTypedArray()) { filter ->
                val isSelected = filter == currentFilter
                Card(
                    modifier = Modifier
                        .width(100.dp)
                        .height(80.dp)
                        .clickable { onSelectFilter(filter) }
                        .testTag("filter_card_${filter.name.lowercase()}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) CoralPink.copy(alpha = 0.25f) else StudioSurfaceElevated
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) CoralPink else StudioCardBorder
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = when (filter) {
                                VideoFilter.ORIGINAL -> Icons.Default.FilterNone
                                VideoFilter.CINEMATIC -> Icons.Default.MovieFilter
                                VideoFilter.VINTAGE_VHS -> Icons.Default.Tv
                                VideoFilter.FILM_NOIR -> Icons.Default.Contrast
                                VideoFilter.CYBER_NEON -> Icons.Default.Bolt
                                VideoFilter.WARM_SUNSET -> Icons.Default.WbSunny
                                VideoFilter.ULTRA_VIVID -> Icons.Default.ColorLens
                                VideoFilter.SEPIA -> Icons.Default.PhotoCamera
                                VideoFilter.GLITCH -> Icons.Default.GraphicEq
                            },
                            contentDescription = null,
                            tint = if (isSelected) CoralPink else NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = filter.displayName,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransitionsPanel(
    currentTransition: TransitionType,
    onSelectTransition: (TransitionType) -> Unit
) {
    Column {
        Text(
            text = "Efecto de Entrada: ${currentTransition.displayName}",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(TransitionType.entries.toTypedArray()) { trans ->
                val isSelected = trans == currentTransition
                Card(
                    modifier = Modifier
                        .width(110.dp)
                        .height(80.dp)
                        .clickable { onSelectTransition(trans) }
                        .testTag("transition_card_${trans.name.lowercase()}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ElectricViolet.copy(alpha = 0.25f) else StudioSurfaceElevated
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) ElectricViolet else StudioCardBorder
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = when (trans) {
                                TransitionType.NONE -> Icons.Default.Close
                                TransitionType.FADE_BLACK -> Icons.Default.Gradient
                                TransitionType.DISSOLVE -> Icons.Default.BlurOn
                                TransitionType.SLIDE_LEFT -> Icons.Default.ArrowBack
                                TransitionType.SLIDE_RIGHT -> Icons.Default.ArrowForward
                                TransitionType.ZOOM_IN -> Icons.Default.ZoomIn
                                TransitionType.FLASH_WHITE -> Icons.Default.FlashOn
                                TransitionType.WIPE_DOWN -> Icons.Default.ArrowDownward
                            },
                            contentDescription = null,
                            tint = if (isSelected) ElectricViolet else NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = trans.displayName,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrimAndSpeedPanel(
    clip: VideoClip,
    onUpdateClip: (VideoClip) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Speed selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Velocidad de Clip:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { spd ->
                    val isSelected = clip.speed == spd
                    Surface(
                        modifier = Modifier.clickable { onUpdateClip(clip.copy(speed = spd)) },
                        color = if (isSelected) CoralPink else StudioSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isSelected) CoralPink else StudioCardBorder)
                    ) {
                        Text(
                            text = "${spd}x",
                            color = if (isSelected) Color.White else TextSecondaryDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Duration Trim Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Duración del Clip", color = Color.White, fontSize = 13.sp)
            Text(
                "${String.format("%.1f", clip.effectiveDuration)}s",
                color = CoralPink,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = clip.endTrimSec.coerceIn(0.5f, clip.durationSec.coerceAtLeast(1f)),
            onValueChange = { newEnd ->
                onUpdateClip(clip.copy(endTrimSec = newEnd))
            },
            valueRange = 0.5f..clip.durationSec.coerceAtLeast(1f),
            colors = SliderDefaults.colors(
                thumbColor = CoralPink,
                activeTrackColor = CoralPink,
                inactiveTrackColor = StudioCardBorder
            )
        )
    }
}

@Composable
private fun TextOverlaysPanel(
    textOverlays: List<TextOverlay>,
    onAddClick: () -> Unit,
    onRemove: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Títulos y Textos", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_text_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nuevo Texto", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (textOverlays.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Agrega subtítulos o títulos llamativos a tu video", color = TextSecondaryDark, fontSize = 12.sp)
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(textOverlays) { item ->
                    Surface(
                        color = StudioSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text(
                                    "${String.format("%.1f", item.startTimeSec)}s - ${String.format("%.1f", item.endTimeSec)}s • ${item.animation.displayName}",
                                    color = NeonCyan,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onRemove(item.id) },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioPanel(
    currentTrack: AudioTrack?,
    onSelectTrack: (AudioTrack?) -> Unit,
    onPlaySfx: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Pista Musical de Fondo", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(AudioCatalog.availableTracks) { track ->
                val isSelected = currentTrack?.id == track.id
                Surface(
                    modifier = Modifier.clickable { onSelectTrack(if (isSelected) null else track) },
                    color = if (isSelected) ElectricViolet.copy(alpha = 0.3f) else StudioSurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (isSelected) ElectricViolet else StudioCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.VolumeUp else Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = if (isSelected) ElectricViolet else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(track.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${track.genre} • ${track.bpm} BPM", color = TextSecondaryDark, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("Efectos de Sonido Rápidos (SFX)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AudioCatalog.soundEffects.forEach { (name, freq) ->
                Surface(
                    modifier = Modifier.clickable { onPlaySfx(freq) },
                    color = StudioSurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(name, color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StickersPanel(
    stickers: List<StickerOverlay>,
    onAddSticker: (StickerOverlay) -> Unit,
    onRemoveSticker: (String) -> Unit
) {
    val emojis = listOf("🔥", "🎬", "✨", "⚡", "🚀", "💯", "🥳", "👑", "❤️", "💥", "🍿", "📸", "🎧", "💪", "🏆")
    Column {
        Text("Toca un emoji para agregarlo como sticker al video", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(emojis) { emoji ->
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(StudioSurfaceElevated)
                        .border(1.dp, StudioCardBorder, CircleShape)
                        .clickable {
                            onAddSticker(
                                StickerOverlay(
                                    emoji = emoji,
                                    startTimeSec = 0f,
                                    endTimeSec = 3f,
                                    posXPercent = (0.3f + (Math.random() * 0.4f)).toFloat(),
                                    posYPercent = (0.3f + (Math.random() * 0.4f)).toFloat()
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 22.sp)
                }
            }
        }

        if (stickers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Stickers en video:", color = TextSecondaryDark, fontSize = 11.sp)
                stickers.forEach { s ->
                    Surface(
                        color = StudioSurfaceElevated,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(s.emoji, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Eliminar",
                                tint = TextSecondaryDark,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { onRemoveSticker(s.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AspectRatioPanel(
    currentRatio: AspectRatioType,
    onSelectRatio: (AspectRatioType) -> Unit
) {
    Column {
        Text("Formato de Pantalla del Video", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AspectRatioType.entries.forEach { ratio ->
                val isSelected = ratio == currentRatio
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clickable { onSelectRatio(ratio) }
                        .testTag("ratio_card_${ratio.tag.replace(':', '_')}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) CoralPink.copy(alpha = 0.25f) else StudioSurfaceElevated
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) CoralPink else StudioCardBorder
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            ratio.tag,
                            color = if (isSelected) CoralPink else Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            ratio.label.split(" ").firstOrNull() ?: "",
                            color = TextSecondaryDark,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddTextDialog(
    maxDuration: Float,
    onDismiss: () -> Unit,
    onConfirm: (TextOverlay) -> Unit
) {
    var textInput by remember { mutableStateOf("¡Gran Momento!") }
    var selectedAnim by remember { mutableStateOf(TextAnimation.POP) }
    var startSec by remember { mutableFloatStateOf(0f) }
    var endSec by remember { mutableFloatStateOf(3f.coerceAtMost(maxDuration)) }
    var textColorHex by remember { mutableLongStateOf(0xFFFFFFFFL) }

    val colors = listOf(
        0xFFFFFFFFL to "Blanco",
        0xFFFFD700L to "Amarillo",
        0xFFFF3B5CL to "Coral",
        0xFF00E5FFL to "Cian",
        0xFF2CB67DL to "Verde",
        0xFF7F5AF0L to "Violeta"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StudioSurfaceElevated,
        title = { Text("Añadir Texto al Video", color = Color.White) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("Texto a mostrar") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CoralPink,
                        unfocusedBorderColor = StudioCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("text_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Color del texto:", color = TextSecondaryDark, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { (hex, _) ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .border(
                                    width = if (textColorHex == hex) 2.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable { textColorHex = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Animación:", color = TextSecondaryDark, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(TextAnimation.entries.toTypedArray()) { anim ->
                        val isSel = anim == selectedAnim
                        Surface(
                            modifier = Modifier.clickable { selectedAnim = anim },
                            color = if (isSel) CoralPink else StudioSurfaceDark,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSel) CoralPink else StudioCardBorder)
                        ) {
                            Text(
                                anim.displayName,
                                color = if (isSel) Color.White else TextSecondaryDark,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textInput.isNotBlank()) {
                        onConfirm(
                            TextOverlay(
                                text = textInput.trim(),
                                startTimeSec = startSec,
                                endTimeSec = endSec.coerceAtLeast(startSec + 1f),
                                textColorHex = textColorHex,
                                animation = selectedAnim
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                modifier = Modifier.testTag("confirm_add_text_button")
            ) {
                Text("Añadir al Video")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondaryDark)
            }
        }
    )
}
