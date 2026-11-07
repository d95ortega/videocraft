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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ai.CineAiDirectorEngine
import com.example.ai.CineAiScreenplayEngine
import com.example.ai.GeminiAiClient
import com.example.data.ApiKeyManager
import com.example.model.*
import com.example.ui.player.VideoCanvasPlayer
import com.example.ui.theme.*
import com.example.viewmodel.VideoStudioViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    // Camera Direction Panel States
    var selectedShotType by remember { mutableStateOf(ShotTypeEnum.WIDE_SHOT) }
    var selectedCameraMove by remember { mutableStateOf(CameraMoveEnum.DOLLY_IN) }
    var selectedLighting by remember { mutableStateOf(LightingStyleEnum.NEON_CYBER) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var isGenerating by remember { mutableStateOf(false) }
    var generationTitle by remember { mutableStateOf("") }
    var generationProgress by remember { mutableFloatStateOf(0f) }
    var generationStage by remember { mutableStateOf("") }

    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val label = promptInput.take(15).ifEmpty { "Galería" }
            val newClip = VideoClip(
                name = "Mi Medio: $label",
                uri = uri.toString(),
                durationSec = selectedDurationSec,
                endTrimSec = selectedDurationSec,
                filter = when (selectedStyle) {
                    VisualStyleEnum.CYBERPUNK_NEON -> VideoFilter.CYBER_NEON
                    VisualStyleEnum.FILM_NOIR -> VideoFilter.FILM_NOIR
                    else -> VideoFilter.CINEMATIC
                },
                transitionIn = TransitionType.ZOOM_IN
            )
            viewModel.addClip(newClip)
            val startTime = project.totalDurationSec
            viewModel.seekTo(startTime)
            viewModel.play()
            coroutineScope.launch {
                snackbarHostState.showSnackbar("📸 Foto/Video de tu celular añadido y reproduciéndose en el monitor.")
            }
        }
    }

    val onGenerateScene: () -> Unit = {
        coroutineScope.launch {
            isGenerating = true
            generationTitle = "Generando Toma con IA"
            val stages = listOf(
                "Analizando prompt y composición..." to 0.25f,
                "Configurando ${selectedShotType.label.split(" ").first()} con ${selectedCameraMove.label}..." to 0.60f,
                "Sintetizando iluminación ${selectedLighting.label}..." to 0.85f,
                "¡Plano cinemático listo!" to 1.0f
            )
            for ((text, prog) in stages) {
                generationStage = text
                generationProgress = prog
                delay(280)
            }
            delay(150)
            val clipName = "Toma: ${selectedShotType.label.split(" ").first()} • ${promptInput.take(16)}"
            val newClip = VideoClip(
                name = clipName,
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
            val prevDuration = project.totalDurationSec
            viewModel.addClip(newClip)
            viewModel.addTextOverlay(
                TextOverlay(
                    text = promptInput.take(55),
                    startTimeSec = prevDuration,
                    endTimeSec = prevDuration + selectedDurationSec - 0.4f,
                    textColorHex = 0xFFFFD700L,
                    fontSizeSp = 18,
                    animation = TextAnimation.TYPEWRITER,
                    positionYPercent = 0.84f
                )
            )
            viewModel.seekTo(prevDuration)
            viewModel.play()
            isGenerating = false
            snackbarHostState.showSnackbar("🎬 ¡Nueva toma creada y reproduciéndose en el monitor superior!")
        }
    }

    val context = LocalContext.current

    val onGenerateFullMovie: () -> Unit = {
        coroutineScope.launch {
            isGenerating = true
            generationTitle = "Produciendo Película con IA"
            val key = ApiKeyManager.getActiveApiKey(context)
            val hasKey = key.isNotBlank() && !key.contains("MY_GEMINI_API_KEY")

            generationStage = if (hasKey) "Conectando con Google Gemini 2.5 Flash..." else "Estructurando narrativa cinematográfica..."
            generationProgress = 0.25f
            delay(280)

            val movie = if (hasKey) {
                generationStage = "Generando planos, personajes y diálogos con Gemini..."
                generationProgress = 0.60f
                val result = GeminiAiClient.generateAiMovie(key, promptInput, selectedGenre, 4)
                if (result.isSuccess) {
                    result.getOrThrow()
                } else {
                    val err = result.exceptionOrNull()?.message ?: "Error de red"
                    generationStage = "Aviso API: $err. Usando motor CineAI..."
                    delay(300)
                    CineAiDirectorEngine.generateMovieFromPrompt(
                        prompt = promptInput,
                        selectedGenre = selectedGenre,
                        productionType = MovieProductionType.CINEMATIC_TRAILER
                    )
                }
            } else {
                generationStage = "Componiendo planos de cámara e iluminación..."
                generationProgress = 0.70f
                delay(300)
                CineAiDirectorEngine.generateMovieFromPrompt(
                    prompt = promptInput,
                    selectedGenre = selectedGenre,
                    productionType = MovieProductionType.CINEMATIC_TRAILER
                )
            }

            generationStage = "Sintetizando banda sonora y mezclas..."
            generationProgress = 1.0f
            delay(200)

            viewModel.onMovieGenerated(movie)
            viewModel.play()
            isGenerating = false
            val origin = if (hasKey) "Google Gemini AI (En vivo)" else "Motor CineAI"
            snackbarHostState.showSnackbar("🎬 ¡Película '${movie.title}' creada por $origin y reproduciéndose!")
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .testTag("cinematic_director_studio")
    ) {
        val isDesktopWorkstation = maxWidth >= 840.dp

        if (isDesktopWorkstation) {
            // WIDESCREEN / COMPUTER WORKSTATION LAYOUT (Dual-pane like DaVinci/Premiere)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // LEFT PANE: Large Cinema Monitor + Telemetry + Clip Info
                Column(
                    modifier = Modifier
                        .weight(1.15f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StudioHeaderRow(isDesktop = true)

                    MonitorCinematograficoCard(
                        project = project,
                        currentTime = currentTime,
                        isPlaying = isPlaying,
                        viewModel = viewModel,
                        selectedShotType = selectedShotType,
                        selectedCameraMove = selectedCameraMove
                    )

                    // Desktop Telemetry & Active Scene Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, StudioCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ESTADO DEL MONITOR DE PRODUCCIÓN", color = TextSecondaryDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Formato ${project.aspectRatio.tag} • ${project.clips.size} Tomas en Película • Duración: ${String.format("%.1f", project.totalDurationSec)}s",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Surface(
                                color = MintGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, MintGreen.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "Hardware Acelerado 60fps",
                                    color = MintGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // RIGHT PANE: Panel de Dirección + Panel de Dirección de Cámara
                Column(
                    modifier = Modifier
                        .weight(0.85f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PanelDeDireccionCard(
                        promptInput = promptInput,
                        onPromptChange = { promptInput = it },
                        selectedGenre = selectedGenre,
                        selectedStyle = selectedStyle,
                        onStyleChange = { selectedStyle = it },
                        selectedModel = selectedModel,
                        selectedDurationSec = selectedDurationSec,
                        onDurationChange = { selectedDurationSec = it },
                        onGenerateScene = onGenerateScene,
                        onGenerateFullMovie = onGenerateFullMovie,
                        onPickMedia = {
                            mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                        }
                    )

                    PanelDireccionCamaraCard(
                        selectedShotType = selectedShotType,
                        onShotTypeChange = { selectedShotType = it },
                        selectedCameraMove = selectedCameraMove,
                        onCameraMoveChange = { selectedCameraMove = it },
                        selectedLighting = selectedLighting,
                        onLightingChange = { selectedLighting = it }
                    )
                }
            }
        } else {
            // MOBILE SINGLE COLUMN LAYOUT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StudioHeaderRow(isDesktop = false)

                MonitorCinematograficoCard(
                    project = project,
                    currentTime = currentTime,
                    isPlaying = isPlaying,
                    viewModel = viewModel,
                    selectedShotType = selectedShotType,
                    selectedCameraMove = selectedCameraMove
                )

                PanelDeDireccionCard(
                    promptInput = promptInput,
                    onPromptChange = { promptInput = it },
                    selectedGenre = selectedGenre,
                    selectedStyle = selectedStyle,
                    onStyleChange = { selectedStyle = it },
                    selectedModel = selectedModel,
                    selectedDurationSec = selectedDurationSec,
                    onDurationChange = { selectedDurationSec = it },
                    onGenerateScene = onGenerateScene,
                    onGenerateFullMovie = onGenerateFullMovie,
                    onPickMedia = {
                        mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                    }
                )

                PanelDireccionCamaraCard(
                    selectedShotType = selectedShotType,
                    onShotTypeChange = { selectedShotType = it },
                    selectedCameraMove = selectedCameraMove,
                    onCameraMoveChange = { selectedCameraMove = it },
                    selectedLighting = selectedLighting,
                    onLightingChange = { selectedLighting = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (isGenerating) {
            AlertDialog(
                onDismissRequest = {},
                containerColor = StudioSurfaceElevated,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = CoralPink,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(generationTitle, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(
                            text = generationStage,
                            color = TextSecondaryDark,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { generationProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CoralPink,
                            trackColor = StudioSurfaceDark
                        )
                    }
                },
                confirmButton = {}
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun StudioHeaderRow(isDesktop: Boolean) {
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isDesktop) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = NeonCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "PC WORKSTATION",
                            color = NeonCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
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
}

@Composable
private fun MonitorCinematograficoCard(
    project: VideoProject,
    currentTime: Float,
    isPlaying: Boolean,
    viewModel: VideoStudioViewModel,
    selectedShotType: ShotTypeEnum,
    selectedCameraMove: CameraMoveEnum
) {
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
}

@Composable
private fun PanelDeDireccionCard(
    promptInput: String,
    onPromptChange: (String) -> Unit,
    selectedGenre: CinemaGenre,
    selectedStyle: VisualStyleEnum,
    onStyleChange: (VisualStyleEnum) -> Unit,
    selectedModel: String,
    selectedDurationSec: Float,
    onDurationChange: (Float) -> Unit,
    onGenerateScene: () -> Unit,
    onGenerateFullMovie: () -> Unit,
    onPickMedia: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isEnhancing by remember { mutableStateOf(false) }

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

                Button(
                    onClick = {
                        coroutineScope.launch {
                            isEnhancing = true
                            val key = ApiKeyManager.getActiveApiKey(context)
                            if (key.isNotBlank() && !key.contains("MY_GEMINI_API_KEY")) {
                                val result = GeminiAiClient.enhancePrompt(key, promptInput, selectedGenre, selectedStyle)
                                if (result.isSuccess) {
                                    onPromptChange(result.getOrThrow())
                                } else {
                                    val fallback = CineAiScreenplayEngine.enhancePromptWithAi(promptInput, selectedGenre, selectedStyle)
                                    onPromptChange(fallback)
                                }
                            } else {
                                val fallback = CineAiScreenplayEngine.enhancePromptWithAi(promptInput, selectedGenre, selectedStyle)
                                onPromptChange(fallback)
                            }
                            isEnhancing = false
                        }
                    },
                    enabled = !isEnhancing,
                    colors = ButtonDefaults.buttonColors(containerColor = BrightAmber.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    border = BorderStroke(1.dp, BrightAmber)
                ) {
                    if (isEnhancing) {
                        CircularProgressIndicator(modifier = Modifier.size(12.dp), color = BrightAmber, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Optimizando...", color = BrightAmber, fontSize = 11.sp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mejorar con IA", color = BrightAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = promptInput,
                onValueChange = onPromptChange,
                label = { Text("Instrucción o idea cinematográfica (Prompt)") },
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

            Text("Estilo Visual Cinematográfico:", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(VisualStyleEnum.entries.toTypedArray()) { style ->
                    val isSel = style == selectedStyle
                    Surface(
                        modifier = Modifier.clickable { onStyleChange(style) },
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
                                    .clickable { onDurationChange(sec) },
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

            // Primary Action: Generar Película Completa
            Button(
                onClick = onGenerateFullMovie,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("generate_full_movie_button")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("🎬 Generar Película Completa con IA", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Actions: Generar Plano Individual o Cargar de Galería
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onGenerateScene,
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.1f)
                        .height(46.dp)
                        .testTag("generate_scene_button")
                ) {
                    Icon(Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Añadir Toma", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onPickMedia,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, NeonCyan),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                    modifier = Modifier
                        .weight(0.9f)
                        .height(46.dp)
                        .testTag("pick_device_media_button")
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Foto/Video Cel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = StudioSurfaceDark,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, StudioCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tip: 'Generar Película Completa' crea la historia de tu prompt con actos, personajes, diálogos y banda sonora en el monitor.",
                        color = TextSecondaryDark,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PanelDireccionCamaraCard(
    selectedShotType: ShotTypeEnum,
    onShotTypeChange: (ShotTypeEnum) -> Unit,
    selectedCameraMove: CameraMoveEnum,
    onCameraMoveChange: (CameraMoveEnum) -> Unit,
    selectedLighting: LightingStyleEnum,
    onLightingChange: (LightingStyleEnum) -> Unit
) {
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

            Text("Tipo de Encuadre / Plano:", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(ShotTypeEnum.entries.toTypedArray()) { shot ->
                    val isSel = shot == selectedShotType
                    Surface(
                        modifier = Modifier.clickable { onShotTypeChange(shot) },
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

            Text("Movimiento de Cámara:", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(CameraMoveEnum.entries.toTypedArray()) { move ->
                    val isSel = move == selectedCameraMove
                    Surface(
                        modifier = Modifier.clickable { onCameraMoveChange(move) },
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

            Text("Esquema de Iluminación:", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(LightingStyleEnum.entries.toTypedArray()) { light ->
                    val isSel = light == selectedLighting
                    Surface(
                        modifier = Modifier.clickable { onLightingChange(light) },
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
}
