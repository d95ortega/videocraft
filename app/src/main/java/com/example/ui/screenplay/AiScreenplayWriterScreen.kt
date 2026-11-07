package com.example.ui.screenplay

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.CineAiScreenplayEngine
import com.example.ai.GeminiAiClient
import com.example.data.ApiKeyManager
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiScreenplayWriterScreen(
    onLoadMovieToStudio: (AiMovieProject) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var ideaPrompt by remember {
        mutableStateOf("Una exploradora descubre una ciudad futurista escondida entre montañas. Allí encuentra una civilización que ha permanecido oculta durante siglos.")
    }
    var selectedGenre by remember { mutableStateOf(CinemaGenre.SCI_FI) }
    var selectedStyle by remember { mutableStateOf(VisualStyleEnum.ANAMORPHIC_35MM) }

    var currentScreenplay by remember {
        mutableStateOf<FullScreenplay?>(
            CineAiScreenplayEngine.generateFullScreenplay(ideaPrompt, selectedGenre, selectedStyle)
        )
    }
    var isGenerating by remember { mutableStateOf(false) }
    var generationStatusMessage by remember { mutableStateOf<String?>(null) }

    val activeKey = remember { ApiKeyManager.getActiveApiKey(context) }
    val hasApiKey = activeKey.isNotBlank() && !activeKey.contains("MY_GEMINI_API_KEY")

    val presetIdeas = listOf(
        "🏔️ Ciudad Oculta" to "Una exploradora descubre una ciudad futurista escondida entre montañas donde una civilización ancestral ha permanecido oculta.",
        "🛰️ Estación Abandonada" to "Un equipo de rescate aborda una estación espacial a la deriva y descubre que la IA creó una simulación perfecta de la Tierra.",
        "⚔️ El Fénix Renacido" to "En un reino asediado por monstruos de cristal, un joven herrero forja el arma capaz de reavivar el fuego primordial.",
        "🕵️ Neo-Tokio 2099" to "Una detective cibernética tras la pista de memorias robadas que pertenecen a los líderes de la ciudad."
    )

    val onGenerateScreenplay = {
        coroutineScope.launch {
            isGenerating = true
            generationStatusMessage = "Conectando con motor de guiones..."
            val key = ApiKeyManager.getActiveApiKey(context)

            if (key.isNotBlank() && !key.contains("MY_GEMINI_API_KEY")) {
                generationStatusMessage = "Generando con Google Gemini 2.5 Flash..."
                val result = GeminiAiClient.generateFullScreenplay(key, ideaPrompt, selectedGenre, selectedStyle)
                if (result.isSuccess) {
                    currentScreenplay = result.getOrThrow()
                    generationStatusMessage = null
                    Toast.makeText(context, "🎬 ¡Guion generado en vivo con Google Gemini!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error de red"
                    generationStatusMessage = "Aviso: $errorMsg. Usando motor CineAI local."
                    currentScreenplay = CineAiScreenplayEngine.generateFullScreenplay(ideaPrompt, selectedGenre, selectedStyle)
                    Toast.makeText(context, "Aviso de API: usando motor CineAI local.", Toast.LENGTH_LONG).show()
                }
            } else {
                currentScreenplay = CineAiScreenplayEngine.generateFullScreenplay(ideaPrompt, selectedGenre, selectedStyle)
                generationStatusMessage = null
                Toast.makeText(context, "Generado con motor CineAI local. (Configura tu clave Gemini en Configuración)", Toast.LENGTH_LONG).show()
            }
            isGenerating = false
        }
    }

    val onEnhancePrompt = {
        coroutineScope.launch {
            val key = ApiKeyManager.getActiveApiKey(context)
            if (key.isNotBlank() && !key.contains("MY_GEMINI_API_KEY")) {
                Toast.makeText(context, "Mejorando prompt con Gemini...", Toast.LENGTH_SHORT).show()
                val result = GeminiAiClient.enhancePrompt(key, ideaPrompt, selectedGenre, selectedStyle)
                if (result.isSuccess) {
                    ideaPrompt = result.getOrThrow()
                    Toast.makeText(context, "✨ ¡Prompt enriquecido con Gemini!", Toast.LENGTH_SHORT).show()
                } else {
                    ideaPrompt = CineAiScreenplayEngine.enhancePromptWithAi(ideaPrompt, selectedGenre, selectedStyle)
                }
            } else {
                ideaPrompt = CineAiScreenplayEngine.enhancePromptWithAi(ideaPrompt, selectedGenre, selectedStyle)
                Toast.makeText(context, "Prompt enriquecido con presets cinemáticos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(14.dp)
            .testTag("ai_screenplay_writer_screen")
    ) {
        val isDesktop = maxWidth >= 840.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Guionista Cinematográfico IA",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = if (hasApiKey) MintGreen.copy(alpha = 0.2f) else BrightAmber.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, if (hasApiKey) MintGreen.copy(alpha = 0.5f) else BrightAmber.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = if (hasApiKey) "● GEMINI 2.5 FLASH" else "○ MOTOR LOCAL",
                                color = if (hasApiKey) MintGreen else BrightAmber,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (hasApiKey) "Conectado a Google Gemini • Generación personalizada en tiempo real" else "Motor local activo • Agrega tu clave en Configuración para activar Gemini",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (generationStatusMessage != null) {
                Surface(
                    color = BrightAmber.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BrightAmber.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(
                        text = generationStatusMessage!!,
                        color = BrightAmber,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            if (isDesktop) {
                // DESKTOP DUAL-PANE VIEW
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left Column: Idea Prompt & Controls
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IdeaPromptCard(
                            ideaPrompt = ideaPrompt,
                            onIdeaPromptChange = { ideaPrompt = it },
                            presetIdeas = presetIdeas,
                            selectedGenre = selectedGenre,
                            onGenreSelect = { selectedGenre = it },
                            isGenerating = isGenerating,
                            onGenerate = { onGenerateScreenplay() },
                            onEnhance = { onEnhancePrompt() }
                        )
                    }

                    // Right Column: Output Screenplay
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                    ) {
                        currentScreenplay?.let { script ->
                            ScreenplayListView(
                                script = script,
                                onLoadMovieToStudio = onLoadMovieToStudio,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            } else {
                // MOBILE SINGLE PANE VIEW
                Column(modifier = Modifier.fillMaxSize()) {
                    IdeaPromptCard(
                        ideaPrompt = ideaPrompt,
                        onIdeaPromptChange = { ideaPrompt = it },
                        presetIdeas = presetIdeas,
                        selectedGenre = selectedGenre,
                        onGenreSelect = { selectedGenre = it },
                        isGenerating = isGenerating,
                        onGenerate = { onGenerateScreenplay() },
                        onEnhance = { onEnhancePrompt() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    currentScreenplay?.let { script ->
                        ScreenplayListView(
                            script = script,
                            onLoadMovieToStudio = onLoadMovieToStudio,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IdeaPromptCard(
    ideaPrompt: String,
    onIdeaPromptChange: (String) -> Unit,
    presetIdeas: List<Pair<String, String>>,
    selectedGenre: CinemaGenre,
    onGenreSelect: (CinemaGenre) -> Unit,
    isGenerating: Boolean,
    onGenerate: () -> Unit,
    onEnhance: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, StudioCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Idea o Premisa de la Película:",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    modifier = Modifier.clickable { onEnhance() },
                    color = BrightAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, BrightAmber.copy(alpha = 0.5f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mejorar con IA", color = BrightAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = ideaPrompt,
                onValueChange = onIdeaPromptChange,
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

            Spacer(modifier = Modifier.height(8.dp))

            Text("Inspiraciones rápidas:", color = TextSecondaryDark, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(presetIdeas) { (title, prompt) ->
                    Surface(
                        modifier = Modifier.clickable {
                            onIdeaPromptChange(prompt)
                        },
                        color = StudioSurfaceDark,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, StudioCardBorder)
                    ) {
                        Text(
                            text = title,
                            color = TextSecondaryDark,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onGenerate,
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("generate_screenplay_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generando Guion con IA...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generar Guion Estructurado con IA", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ScreenplayListView(
    script: FullScreenplay,
    onLoadMovieToStudio: (AiMovieProject) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Movie Overview Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CoralPink.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = script.title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "LOGLINE: ${script.logline}",
                        color = BrightAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = script.synopsis,
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val movie = CineAiScreenplayEngine.convertScreenplayToMovieProject(script)
                            onLoadMovieToStudio(movie)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("send_to_cinema_studio_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cargar en Estudio y Reproducir Película", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Characters Section
        item {
            Text("Personajes Principales:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CharacterChipCard(character = script.protagonist, modifier = Modifier.weight(1f))
                script.antagonist?.let {
                    CharacterChipCard(character = it, modifier = Modifier.weight(1f))
                }
            }
        }

        // Scenes Header
        item {
            Text("Escenas y Dirección Cinemática (${script.scenes.size} planos):", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        items(script.scenes) { scene ->
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceDark),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, StudioCardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ESCENA ${scene.sceneNumber}: ${scene.sceneHeading}",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${scene.durationSec}s",
                            color = TextSecondaryDark,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = scene.visualAction,
                        color = Color.White,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    if (scene.characterDialogues.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        scene.characterDialogues.forEach { (speaker, line) ->
                            Text(
                                text = "$speaker: \"$line\"",
                                color = BrightAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = CoralPink.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "🎥 ${scene.shotSuggestion.label}",
                                color = CoralPink,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            color = ElectricViolet.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "🧭 ${scene.cameraMoveSuggestion.label}",
                                color = ElectricViolet,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterChipCard(
    character: CharacterProfile,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceDark),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, StudioCardBorder)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(character.avatarColorHex),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.size(16.dp)
                ) {}
                Spacer(modifier = Modifier.width(6.dp))
                Text(character.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text("${character.role} • ${character.apparentAge}", color = TextSecondaryDark, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(character.personality, color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, maxLines = 2)
        }
    }
}
