package com.example.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ui.theme.*

@Composable
fun StudioSettingsScreen(
    modifier: Modifier = Modifier
) {
    val hasApiKey = remember {
        try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotEmpty() && !key.contains("MY_GEMINI_API_KEY")
        } catch (_: Exception) {
            false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(14.dp)
            .testTag("studio_settings_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Configuración & API",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Parámetros del estudio, backend y conexión con modelos de IA",
            color = TextSecondaryDark,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // API Key Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ESTADO DE API GEMINI", color = TextSecondaryDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        color = if (hasApiKey) MintGreen.copy(alpha = 0.2f) else BrightAmber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (hasApiKey) "Conectado Directo" else "Motor Local CineAI Activo",
                            color = if (hasApiKey) MintGreen else BrightAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (hasApiKey) {
                        "La clave GEMINI_API_KEY se encuentra inyectada de forma segura vía BuildConfig. Las solicitudes de guion e ideas se comunican con los endpoints oficiales."
                    } else {
                        "El estudio está operando con el Motor Neuronal CineAI incorporado. Para conectar tu propia clave de Gemini, configúrala en el panel Secrets de AI Studio."
                    },
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // System specs card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("ESPECIFICACIONES DE RENDERIZADO", color = TextSecondaryDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Motor de Visualización: Android Canvas Hardware Accelerated 60fps", color = Color.White, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• Formatos de Salida: MP4 H.264 (1080p Full HD y 4K Ultra)", color = Color.White, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• Base de Datos Local: Room SQLite Persistent Storage", color = Color.White, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• Síntesis de Audio: PCM Software Audio Synthesizer Nativo", color = Color.White, fontSize = 11.sp)
            }
        }
    }
}
