package com.example.ui.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun AiScriptGeneratorDialog(
    onDismiss: () -> Unit,
    onApplyScript: (AiScriptResult) -> Unit
) {
    var topicInput by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedIdea by remember { mutableStateOf<AiScriptResult?>(null) }

    val trendingTopics = listOf(
        "3 Tips para Redes Sociales",
        "Lanzamiento de Producto",
        "Receta Express en 30 Segundos",
        "Rutina de Energía y Fitness",
        "Storytime Emocionante"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StudioSurfaceElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generador de Guiones IA",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
            ) {
                Text(
                    text = "¿De qué trata tu próximo video?",
                    color = TextSecondaryDark,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = topicInput,
                    onValueChange = { topicInput = it },
                    placeholder = { Text("Ej: Consejos de viaje para mochileros...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CoralPink,
                        unfocusedBorderColor = StudioCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_script_topic_input")
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("Sugerencias rápidas:", color = TextSecondaryDark, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(trendingTopics) { topic ->
                        Surface(
                            modifier = Modifier.clickable {
                                topicInput = topic
                                generatedIdea = generateScriptForTopic(topic)
                            },
                            color = StudioSurfaceDark,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, StudioCardBorder)
                        ) {
                            Text(
                                text = topic,
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Generated Result Card
                val idea = generatedIdea
                if (idea != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StudioSurfaceDark),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CoralPink.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "🎯 GANCHO (Primeros 3s):",
                                    color = CoralPink,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(idea.hook, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "🎬 ESCENAS PLANIFICADAS:",
                                    color = NeonCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                idea.scenes.forEachIndexed { i, sc ->
                                    Text(
                                        text = "${i + 1}. ${sc.title} (${sc.durationSec}s)",
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "   Texto: \"${sc.overlayText}\"",
                                        color = TextSecondaryDark,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "📢 CIERRE & LLAMADO:",
                                    color = MintGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(idea.callToAction, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Escribe un tema o elige una sugerencia para generar tu guión y estructura de video al instante.",
                            color = TextSecondaryDark,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (generatedIdea != null) {
                Button(
                    onClick = {
                        generatedIdea?.let { onApplyScript(it) }
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                    modifier = Modifier.testTag("apply_ai_script_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Aplicar al Video")
                }
            } else {
                Button(
                    onClick = {
                        val topic = topicInput.ifBlank { "Creación de Contenido" }
                        generatedIdea = generateScriptForTopic(topic)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightAmber)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generar Guión", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = TextSecondaryDark)
            }
        }
    )
}

data class AiScenePlan(
    val title: String,
    val overlayText: String,
    val durationSec: Float,
    val stockScene: StockSceneType,
    val filter: VideoFilter
)

data class AiScriptResult(
    val topic: String,
    val hook: String,
    val scenes: List<AiScenePlan>,
    val callToAction: String,
    val suggestedTrackTitle: String
)

private fun generateScriptForTopic(topic: String): AiScriptResult {
    return AiScriptResult(
        topic = topic,
        hook = "¡Lo que nadie te contó sobre $topic!",
        scenes = listOf(
            AiScenePlan(
                title = "El Problema Común",
                overlayText = "El error #1 que casi todos cometen",
                durationSec = 2.5f,
                stockScene = StockSceneType.NEON_PULSE,
                filter = VideoFilter.ULTRA_VIVID
            ),
            AiScenePlan(
                title = "La Solución Revelada",
                overlayText = "Aplica esto para resultados inmediatos 💡",
                durationSec = 3.5f,
                stockScene = StockSceneType.CYBER_MATRIX,
                filter = VideoFilter.CYBER_NEON
            ),
            AiScenePlan(
                title = "Demostración Práctica",
                overlayText = "Paso a paso fácil y rápido",
                durationSec = 3.0f,
                stockScene = StockSceneType.SUNSET_GRADIENT,
                filter = VideoFilter.WARM_SUNSET
            )
        ),
        callToAction = "¡Dale like y comparte para parte 2! 🔥",
        suggestedTrackTitle = "Electro Hook"
    )
}
