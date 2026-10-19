package com.example.ui.screenplay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.CineAiScreenplayEngine
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun AiScreenplayWriterScreen(
    onLoadMovieToStudio: (AiMovieProject) -> Unit,
    modifier: Modifier = Modifier
) {
    var ideaPrompt by remember {
        mutableStateOf("Una exploradora descubre una ciudad futurista escondida entre montañas. Allí encuentra una civilización que ha permanecido oculta durante siglos.")
    }
    var selectedGenre by remember { mutableStateOf(CinemaGenre.SCI_FI) }
    var currentScreenplay by remember {
        mutableStateOf<FullScreenplay?>(
            CineAiScreenplayEngine.generateFullScreenplay(ideaPrompt, selectedGenre)
        )
    }
    var isGenerating by remember { mutableStateOf(false) }

    val presetIdeas = listOf(
        "🏔️ Ciudad Oculta" to "Una exploradora descubre una ciudad futurista escondida entre montañas donde una civilización ancestral ha permanecido oculta.",
        "🛰️ Estación Abandonada" to "Un equipo de rescate aborda una estación espacial a la deriva y descubre que la IA creó una simulación perfecta de la Tierra.",
        "⚔️ El Fénix Renacido" to "En un reino asediado por monstruos de cristal, un joven herrero forja el arma capaz de reavivar el fuego primordial.",
        "🕵️ Medianoche en Neo-Tokio" to "Una detective cibernética tras la pista de memorias robadas que pertenecen a los líderes de la ciudad."
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(14.dp)
            .testTag("ai_screenplay_writer_screen")
    ) {
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
                }
                Text(
                    text = "Impulsado por Gemini • Generación de guion profesional con diálogos y planos",
                    color = TextSecondaryDark,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Idea Prompt Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Idea o Premisa de la Película:",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = ideaPrompt,
                    onValueChange = { ideaPrompt = it },
                    minLines = 2,
                    maxLines = 3,
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

                // Presets
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(presetIdeas) { (title, prompt) ->
                        Surface(
                            modifier = Modifier.clickable {
                                ideaPrompt = prompt
                                currentScreenplay = CineAiScreenplayEngine.generateFullScreenplay(prompt, selectedGenre)
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
                    onClick = {
                        isGenerating = true
                        currentScreenplay = CineAiScreenplayEngine.generateFullScreenplay(ideaPrompt, selectedGenre)
                        isGenerating = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generar Guion Estructurado con IA", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Screenplay Output Viewer
        currentScreenplay?.let { script ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Aprobar Guion y Convertir en Película", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Scenes List
                items(script.scenes) { scene ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, StudioCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ESCENA ${scene.sceneNumber}: ${scene.sceneHeading}",
                                    color = NeonCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${scene.durationSec.toInt()}s",
                                    color = TextSecondaryDark,
                                    fontSize = 10.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = scene.visualAction,
                                color = Color.White,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            scene.characterDialogues.forEach { (speaker, dialogue) ->
                                Text(
                                    text = "$speaker: \"$dialogue\"",
                                    color = BrightAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(color = StudioSurfaceDark, shape = RoundedCornerShape(4.dp)) {
                                    Text(scene.shotSuggestion.label, color = TextSecondaryDark, fontSize = 9.sp, modifier = Modifier.padding(4.dp))
                                }
                                Surface(color = StudioSurfaceDark, shape = RoundedCornerShape(4.dp)) {
                                    Text(scene.cameraMoveSuggestion.label, color = TextSecondaryDark, fontSize = 9.sp, modifier = Modifier.padding(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
