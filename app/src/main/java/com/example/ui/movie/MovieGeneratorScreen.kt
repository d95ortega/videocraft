package com.example.ui.movie

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun MovieGeneratorScreen(
    onMovieGenerated: (AiMovieProject) -> Unit,
    modifier: Modifier = Modifier
) {
    var promptInput by remember {
        mutableStateOf("Una película de ciencia ficción cyberpunk donde una detective androide descubre un misterio en Neo-Tokyo bajo lluvia de neón")
    }
    var selectedGenre by remember { mutableStateOf(CinemaGenre.SCI_FI) }
    var selectedProductionType by remember { mutableStateOf(MovieProductionType.CINEMATIC_TRAILER) }
    var selectedRatio by remember { mutableStateOf(AspectRatioType.RATIO_16_9) }
    var isGenerating by remember { mutableStateOf(false) }
    var generationProgress by remember { mutableFloatStateOf(0f) }
    var currentStageText by remember { mutableStateOf("") }

    val inspirationIdeas = listOf(
        "🚀 Odisea Espacial" to "Una tripulación solitaria que descubre una señal alienígena en los anillos de Saturno",
        "🏙️ Neo-Tokyo 2099" to "Una detective androide persiguiendo una IA rebelde en los bajos fondos bajo lluvia de neón",
        "⚔️ Fantasía Épica" to "El último guardián despierta al dragón sagrado para proteger el reino de las sombras",
        "🕵️ Misterio Noir" to "Un detective privado investiga la desaparición de una heredera en la lluviosa noche de 1947",
        "🏎️ Alta Velocidad" to "Una persecución automovilística a toda velocidad esquivando el tráfico nocturno",
        "🌿 Planeta Salvaje" to "Un documental sobre las criaturas bioluminiscentes de las profundidades marinas"
    )

    // Animated Generation Process
    LaunchedEffect(isGenerating) {
        if (isGenerating) {
            val stages = listOf(
                "Escribiendo guión, personajes y diálogos..." to 0.25f,
                "Diseñando planos cinematográficos y escenografía..." to 0.55f,
                "Configurando movimientos de cámara y atmósfera..." to 0.80f,
                "Componiendo banda sonora orquestal y efectos SFX..." to 1.0f
            )

            for ((stageText, targetProgress) in stages) {
                currentStageText = stageText
                while (generationProgress < targetProgress) {
                    delay(80)
                    generationProgress += 0.04f
                }
            }

            delay(200)
            val movie = com.example.ai.CineAiDirectorEngine.generateMovieFromPrompt(
                prompt = promptInput,
                selectedGenre = selectedGenre,
                productionType = selectedProductionType,
                aspectRatio = selectedRatio
            )
            isGenerating = false
            generationProgress = 0f
            onMovieGenerated(movie)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("movie_generator_screen")
    ) {
        // Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF2C0B4D), Color(0xFF140D2E), Color(0xFF0F2027))
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CoralPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DIRECTOR DE CINE IA",
                            color = BrightAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Genera Películas desde tu Prompt",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Escribe cualquier idea o trama y la IA dirigirá las escenas, planos de cámara, iluminación, diálogos y banda sonora.",
                    color = TextSecondaryDark,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prompt Input Box
        Text(
            text = "Escribe tu Prompt de Video o Película:",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = promptInput,
            onValueChange = { promptInput = it },
            placeholder = { Text("Ej: Una nave espacial averiada cuyos tripulantes descubren un planeta con ruinas de cristal...") },
            minLines = 3,
            maxLines = 5,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = CoralPink,
                unfocusedBorderColor = StudioCardBorder,
                focusedContainerColor = StudioSurfaceElevated,
                unfocusedContainerColor = StudioSurfaceElevated
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("movie_prompt_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Inspiration Ideas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Inspiración Rápida:", color = TextSecondaryDark, fontSize = 12.sp)
            Text(
                text = "Toca para usar",
                color = CoralPink,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(inspirationIdeas) { (title, promptText) ->
                Surface(
                    modifier = Modifier.clickable {
                        promptInput = promptText
                    },
                    color = StudioSurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Production Format Selector (Express Clip, Trailer, Full Movie)
        Text(
            text = "Escala de Producción:",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MovieProductionType.entries.forEach { type ->
                val isSel = type == selectedProductionType
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedProductionType = type }
                        .testTag("production_type_${type.name.lowercase()}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSel) CoralPink.copy(alpha = 0.18f) else StudioSurfaceElevated
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(if (isSel) 1.5.dp else 1.dp, if (isSel) CoralPink else StudioCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = type.displayName,
                                    color = if (isSel) CoralPink else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = if (isSel) CoralPink else StudioSurfaceDark,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${type.sceneCount} Escenas",
                                        color = if (isSel) Color.White else TextSecondaryDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = type.description,
                                color = TextSecondaryDark,
                                fontSize = 11.sp
                            )
                        }

                        RadioButton(
                            selected = isSel,
                            onClick = { selectedProductionType = type },
                            colors = RadioButtonDefaults.colors(selectedColor = CoralPink)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cinema Genre Selector
        Text(
            text = "Género Cinematográfico:",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(CinemaGenre.entries.toTypedArray()) { genre ->
                val isSel = genre == selectedGenre
                FilterChip(
                    selected = isSel,
                    onClick = { selectedGenre = genre },
                    label = { Text("${genre.emoji} ${genre.displayName}", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricViolet.copy(alpha = 0.25f),
                        selectedLabelColor = ElectricViolet,
                        containerColor = StudioSurfaceElevated,
                        labelColor = TextSecondaryDark
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSel,
                        borderColor = if (isSel) ElectricViolet else Color.Transparent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Aspect Ratio Selector (Cinema 16:9 vs Vertical 9:16)
        Text(
            text = "Formato de Pantalla:",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                AspectRatioType.RATIO_16_9 to "16:9 Cine / YouTube",
                AspectRatioType.RATIO_9_16 to "9:16 Shorts / TikTok",
                AspectRatioType.RATIO_1_1 to "1:1 Cuadrado"
            ).forEach { (ratio, label) ->
                val isSel = ratio == selectedRatio
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedRatio = ratio },
                    color = if (isSel) NeonCyan.copy(alpha = 0.2f) else StudioSurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (isSel) NeonCyan else StudioCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = ratio.tag,
                            color = if (isSel) NeonCyan else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = label.split(" ").firstOrNull() ?: "",
                            color = TextSecondaryDark,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Progress Loading Box or Generate Button
        if (isGenerating) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CoralPink)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { generationProgress },
                        color = CoralPink,
                        trackColor = StudioSurfaceDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentStageText,
                        color = BrightAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${(generationProgress * 100).toInt()}% completado",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            Button(
                onClick = {
                    if (promptInput.isNotBlank()) {
                        isGenerating = true
                        generationProgress = 0.05f
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_movie_button")
            ) {
                Icon(Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (selectedProductionType) {
                        MovieProductionType.FULL_MOVIE -> "Generar Película Completa"
                        MovieProductionType.CINEMATIC_TRAILER -> "Generar Tráiler de Cine"
                        MovieProductionType.EXPRESS_CLIP -> "Generar Video Express"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
