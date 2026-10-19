package com.example.ui.studio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.CineAiScreenplayEngine
import com.example.model.*
import com.example.ui.player.VideoCanvasPlayer
import com.example.ui.theme.*
import com.example.viewmodel.VideoStudioViewModel

@Composable
fun CinematicDirectorStudioScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.currentProject.collectAsState()
    val currentTime by viewModel.currentTimeSec.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    var promptInput by remember {
        mutableStateOf("Plano general de una nave estelar aterrizando sobre una metrópoli de rascacielos biomecánicos bajo lluvia de neón")
    }
    var selectedGenre by remember { mutableStateOf(CinemaGenre.SCI_FI) }
    var selectedStyle by remember { mutableStateOf(VisualStyleEnum.ANAMORPHIC_35MM) }
    var selectedModel by remember { mutableStateOf("Gemini 3.8 Flash Director") }
    var selectedDurationSec by remember { mutableFloatStateOf(6f) }
    var selectedRatio by remember { mutableStateOf(AspectRatioType.RATIO_16_9) }
    var motionIntensity by remember { mutableStateOf("Cinemático Medio") }
    var seedNumber by remember { mutableStateOf("482910") }

    // Camera Direction Panel States
    var selectedShotType by remember { mutableStateOf(ShotTypeEnum.WIDE_SHOT) }
    var selectedCameraMove by remember { mutableStateOf(CameraMoveEnum.DOLLY_IN) }
    var selectedLighting by remember { mutableStateOf(LightingStyleEnum.NEON_CYBER) }
    var depthOfField by remember { mutableStateOf("f/1.8 Cine Bokeh") }

    var isGeneratingScene by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
            .testTag("cinematic_director_studio")
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VideoCameraBack, contentDescription = null, tint = CoralPink, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Estudio Cinematográfico Profesional",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Dirección visual, ópticas de cámara y generación asistida por IA",
                    color = TextSecondaryDark,
                    fontSize = 11.sp
                )
            }

            Surface(
                color = ElectricViolet.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("240 Créditos", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // AREA B: MONITOR CINEMATOGRÁFICO (Live Viewport & Player)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("monitor_cinematografico_card"),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) Color.Red else MintGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MONITOR PRINCIPAL • 4K ULTRA HD",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "${selectedShotType.label.split(" ").first()} • ${selectedCameraMove.label}",
                        color = NeonCyan,
                        fontSize = 10.sp
                    )
                }

                VideoCanvasPlayer(
                    project = project,
                    currentTimeSec = currentTime,
                    isPlaying = isPlaying,
                    onTogglePlay = { viewModel.togglePlay() },
                    onSeek = { viewModel.seekTo(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AREA A: PANEL DE DIRECCIÓN (Prompts, Model, Style, Quality)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("panel_de_direccion_card"),
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
                        text = "A. PANEL DE DIRECCIÓN",
                        color = CoralPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    // Prompt enhancer button
                    Button(
                        onClick = {
                            promptInput = CineAiScreenplayEngine.enhancePromptWithAi(promptInput, selectedGenre, selectedStyle)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrightAmber.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        border = BorderStroke(1.dp, BrightAmber)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mejorar con IA", color = BrightAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    label = { Text("Instrucción visual del plano (Prompt)") },
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CoralPink,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedContainerColor = StudioSurfaceDark,
                        unfocusedContainerColor = StudioSurfaceDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Selector de Estilo Visual
                Text("Estilo Visual Cinematográfico:", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(VisualStyleEnum.entries.toTypedArray()) { style ->
                        val isSel = style == selectedStyle
                        Surface(
                            modifier = Modifier.clickable { selectedStyle = style },
                            color = if (isSel) CoralPink.copy(alpha = 0.25f) else StudioSurfaceDark,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSel) CoralPink else StudioCardBorder)
                        ) {
                            Text(
                                text = style.label,
                                color = if (isSel) CoralPink else Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selectores de Parámetros de Generación (Modelo, Duración, Ratio, Semilla)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Modelo de IA:", color = TextSecondaryDark, fontSize = 10.sp)
                        Surface(
                            color = StudioSurfaceDark,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, StudioCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = selectedModel,
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                maxLines = 1
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Duración de Plano:", color = TextSecondaryDark, fontSize = 10.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(4f, 6f, 8f).forEach { sec ->
                                val isSel = selectedDurationSec == sec
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedDurationSec = sec },
                                    color = if (isSel) CoralPink else StudioSurfaceDark,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${sec.toInt()}s",
                                        color = if (isSel) Color.White else TextSecondaryDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Generate Button
                Button(
                    onClick = {
                        isGeneratingScene = true
                        val newClip = VideoClip(
                            name = "Toma: ${selectedShotType.label.split(" ").first()}",
                            durationSec = selectedDurationSec,
                            endTrimSec = selectedDurationSec,
                            stockScene = when (selectedGenre) {
                                CinemaGenre.SCI_FI -> StockSceneType.NEON_PULSE
                                CinemaGenre.FANTASY_MYTH -> StockSceneType.AURORA_BOREALIS
                                CinemaGenre.ACTION_THRILLER -> StockSceneType.CYBER_MATRIX
                                else -> StockSceneType.DARK_CINEMA
                            },
                            filter = when (selectedStyle) {
                                VisualStyleEnum.CYBERPUNK_NEON -> VideoFilter.CYBER_NEON
                                VisualStyleEnum.FILM_NOIR -> VideoFilter.FILM_NOIR
                                else -> VideoFilter.CINEMATIC
                            },
                            transitionIn = TransitionType.ZOOM_IN
                        )
                        viewModel.addClip(newClip)
                        isGeneratingScene = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("generate_scene_button")
                ) {
                    Icon(Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generar y Añadir Plano a la Película", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AREA C: PANEL DE DIRECCIÓN DE CÁMARA (Lentes, Planos, Movimiento, Luz)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("panel_direccion_camara_card"),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "C. DIRECCIÓN DE CÁMARA & COMPOSICIÓN",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 1. Tipo de Plano (Primer plano, Plano Medio, General, Cenital, Contrapicado)
                Text("Tipo de Encuadre / Plano:", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(ShotTypeEnum.entries.toTypedArray()) { shot ->
                        val isSel = shot == selectedShotType
                        Surface(
                            modifier = Modifier.clickable { selectedShotType = shot },
                            color = if (isSel) NeonCyan.copy(alpha = 0.25f) else StudioSurfaceDark,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSel) NeonCyan else StudioCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(
                                    text = shot.label,
                                    color = if (isSel) NeonCyan else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(shot.desc, color = TextSecondaryDark, fontSize = 9.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Movimiento de Cámara (Travelling, Dolly, Panorámica, Órbita, etc.)
                Text("Movimiento de Cámara:", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(CameraMoveEnum.entries.toTypedArray()) { move ->
                        val isSel = move == selectedCameraMove
                        Surface(
                            modifier = Modifier.clickable { selectedCameraMove = move },
                            color = if (isSel) ElectricViolet.copy(alpha = 0.25f) else StudioSurfaceDark,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSel) ElectricViolet else StudioCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(
                                    text = move.label,
                                    color = if (isSel) ElectricViolet else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(move.desc, color = TextSecondaryDark, fontSize = 9.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Iluminación y Temperatura de Color
                Text("Esquema de Iluminación:", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(LightingStyleEnum.entries.toTypedArray()) { light ->
                        val isSel = light == selectedLighting
                        Surface(
                            modifier = Modifier.clickable { selectedLighting = light },
                            color = if (isSel) BrightAmber.copy(alpha = 0.25f) else StudioSurfaceDark,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSel) BrightAmber else StudioCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(
                                    text = light.label,
                                    color = if (isSel) BrightAmber else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(light.colorTemp, color = TextSecondaryDark, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
