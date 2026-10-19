package com.example.ui.pc

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.theme.*

@Composable
fun PcModeScreen(
    onNavigateToStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(14.dp)
            .testTag("pc_mode_screen")
    ) {
        val widthDp = maxWidth.value.toInt()
        val isWide = maxWidth >= 840.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LaptopMac, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VideoCraft en tu Computadora (PC & Web)",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Guía completa, atajos de teclado y opciones de ejecución en PC, Windows y Mac",
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onNavigateToStudio,
                        colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.VideoCameraBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ir al Estudio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Live Diagnostics Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isWide) MintGreen else BrightAmber)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isWide) "MODO WORKSTATION ESCRITORIO PC ACTIVO" else "MODO DISPOSITIVO COMPACTO",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Surface(
                                color = if (isWide) MintGreen.copy(alpha = 0.15f) else BrightAmber.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Ancho: ${widthDp}dp",
                                    color = if (isWide) MintGreen else BrightAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isWide) {
                                "✓ Tu pantalla tiene ancho suficiente para el diseño profesional de doble panel estilo DaVinci / Premiere Pro: monitor en vivo a la izquierda y paneles de dirección y cámara a la derecha."
                            } else {
                                "Tip para PC: Si estás en tu computadora, puedes expandir el navegador a pantalla completa (F11) o arrastrar el panel hacia los lados para desbloquear la vista de doble columna."
                            },
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Keyboard Shortcuts Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Keyboard, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ATAJOS DE TECLADO PARA COMPUTADORA (PC / LAPTOP)",
                                color = BrightAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val shortcuts = listOf(
                            "Barra Espaciadora" to "Reproducir / Pausar el monitor cinematográfico en vivo",
                            "Flecha Izquierda (←)" to "Retroceder 1.5s en la línea de tiempo del plano",
                            "Flecha Derecha (→)" to "Avanzar 1.5s en la línea de tiempo del plano",
                            "Tecla Escape / Backspace" to "Cerrar menú lateral o regresar al Estudio",
                            "F11 (Navegador)" to "Activar pantalla completa para máxima inmersión",
                            "Clic & Rueda de Ratón" to "Desplazamiento horizontal y zoom en el visor"
                        )

                        shortcuts.forEach { (key, desc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = StudioDarkBg,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, StudioCardBorder)
                                ) {
                                    Text(
                                        text = key,
                                        color = NeonCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = desc,
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // How to run on PC Guide
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "3 FORMAS DE UTILIZAR VIDEOCRAFT EN TU COMPUTADORA",
                            color = CoralPink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Method 1
                        StepCard(
                            stepNumber = "1",
                            title = "Directamente en tu Navegador Web (¡Ya funcionando!)",
                            badge = "Inmediato",
                            badgeColor = MintGreen,
                            content = "No necesitas instalar nada extra para empezar. Gracias a la tecnología de Streaming Web de Google AI Studio, la app ya está corriendo en tu PC. Puedes usar el ratón, teclado, copiar y pegar prompts con Ctrl+C / Ctrl+V, y exportar videos directamente."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Method 2
                        StepCard(
                            stepNumber = "2",
                            title = "Como Aplicación Nativa en Windows 11",
                            badge = "Nativo Windows",
                            badgeColor = NeonCyan,
                            content = "Windows 11 incluye el 'Subsistema de Windows para Android' (WSA). Puedes generar el APK desde AI Studio (menú Configuración > Exportar APK), y abrirlo directamente en Windows como si fuera una aplicación .exe independiente con su propia ventana y aceleración gráfica por hardware."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Method 3
                        StepCard(
                            stepNumber = "3",
                            title = "Emuladores de PC (Windows / Mac / Linux)",
                            badge = "Universal",
                            badgeColor = ElectricViolet,
                            content = "VideoCraft es 100% compatible con BlueStacks 5, LDPlayer, NoxPlayer o el emulador oficial de Android Studio (Pixel Tablet o Desktop Profile). Solo arrastras el APK generado a la ventana del emulador y listo."
                        )
                    }
                }
            }

            // Development and GitHub Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EXPORTAR PROYECTO O ABRIR EN ANDROID STUDIO (PC)",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Si eres desarrollador y deseas tener el código en tu computador localmente:\n\n1. En el menú superior de AI Studio, haz clic en 'Export' o conecta con tu cuenta de GitHub.\n2. Clona el repositorio con 'git clone' en tu PC.\n3. Abre la carpeta con Android Studio (disponible gratis para Windows, Mac y Linux).\n4. Presiona 'Run' para ejecutarlo en tu emulador local o dispositivo conectado por USB.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun StepCard(
    stepNumber: String,
    title: String,
    badge: String,
    badgeColor: Color,
    content: String
) {
    Surface(
        color = StudioSurfaceDark,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, StudioCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(CoralPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stepNumber, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(content, color = TextSecondaryDark, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}
