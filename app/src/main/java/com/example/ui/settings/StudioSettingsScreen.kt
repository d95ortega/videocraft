package com.example.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiAiClient
import com.example.data.ApiKeyManager
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun StudioSettingsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var inputKey by remember { mutableStateOf(ApiKeyManager.getCustomApiKey(context)) }
    var isKeyVisible by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf<String?>(null) }
    var testStatus by remember { mutableStateOf<String?>(null) }
    var isTestingConnection by remember { mutableStateOf(false) }

    val activeKey = remember(inputKey, saveMessage) {
        ApiKeyManager.getActiveApiKey(context)
    }
    val hasKey = activeKey.isNotBlank() && !activeKey.contains("MY_GEMINI_API_KEY")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
            .testTag("studio_settings_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Configuración & IA Gemini",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Conecta tu clave de Google Gemini para habilitar generación de IA real en tu teléfono",
            color = TextSecondaryDark,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // API Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, if (hasKey) MintGreen.copy(alpha = 0.4f) else BrightAmber.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ESTADO DE IA GEMINI", color = TextSecondaryDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        color = if (hasKey) MintGreen.copy(alpha = 0.2f) else BrightAmber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (hasKey) "● Conectado a Gemini AI" else "○ Modo Local (Sin Clave)",
                            color = if (hasKey) MintGreen else BrightAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (hasKey) {
                        "Clave activa detectada (${activeKey.take(7)}...${activeKey.takeLast(4)}). El Guionista IA y la generación de películas se conectan en vivo con la API oficial de Google Gemini 2.5 Flash."
                    } else {
                        "Actualmente la app no tiene una clave API de Gemini configurada. Puedes ingresar tu clave gratuita a continuación para desbloquear generación 100% real por IA."
                    },
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Manual Key Input Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceDark),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Ingresar Clave de API de Gemini",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pega aquí tu clave API de Google AI Studio (comienza con 'AIzaSy...'). Se guardará en la memoria local de tu dispositivo.",
                    color = TextSecondaryDark,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = inputKey,
                    onValueChange = {
                        inputKey = it
                        saveMessage = null
                        testStatus = null
                    },
                    label = { Text("Clave API de Gemini") },
                    placeholder = { Text("AIzaSy...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gemini_api_key_input"),
                    singleLine = true,
                    visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                            Icon(
                                if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isKeyVisible) "Ocultar" else "Mostrar",
                                tint = TextSecondaryDark
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedLabelColor = NeonCyan,
                        unfocusedLabelColor = TextSecondaryDark
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            ApiKeyManager.saveCustomApiKey(context, inputKey)
                            saveMessage = "¡Clave guardada exitosamente en el dispositivo!"
                        }
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            ApiKeyManager.saveCustomApiKey(context, inputKey)
                            saveMessage = "¡Clave guardada exitosamente en el dispositivo!"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_api_key_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Guardar Clave", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val keyToTest = if (inputKey.isNotBlank()) inputKey else activeKey
                            if (keyToTest.isBlank()) {
                                testStatus = "⚠️ Por favor ingresa una clave antes de probar."
                                return@OutlinedButton
                            }
                            isTestingConnection = true
                            testStatus = "Verificando con Google Gemini..."
                            coroutineScope.launch {
                                val result = GeminiAiClient.testConnection(keyToTest)
                                isTestingConnection = false
                                if (result.isSuccess) {
                                    testStatus = "✅ ¡Conexión Exitosa con Google Gemini 2.5 Flash!"
                                } else {
                                    val err = result.exceptionOrNull()?.message ?: "Error desconocido"
                                    testStatus = "❌ Falló la conexión: $err"
                                }
                            }
                        },
                        enabled = !isTestingConnection,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_gemini_connection_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isTestingConnection) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = NeonCyan, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Probando...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Probar Conexión", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (saveMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = saveMessage!!,
                        color = MintGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (testStatus != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = testStatus!!,
                        color = if (testStatus!!.startsWith("✅")) MintGreen else CoralPink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = StudioCardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Get Free Key Button
                OutlinedButton(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/apikey"))
                        context.startActivity(browserIntent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, StudioCardBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrightAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Obtener Clave Gratuita en Google AI Studio", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Clarification Card: How AI Works in VideoCraft
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("¿CÓMO FUNCIONA LA IA EN ESTA APP?", color = BrightAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Guiones y Dirección (Gemini): Cuando ingresas tu clave, Google Gemini escribe títulos, sinopsis, personajes con motivaciones y diálogos escena por escena adaptados exactamente a tu prompt.\n\n" +
                            "2. Generación Cinemática: La app procesa el guion y compone las escenas con movimientos de cámara (Dolly, Pan, Zoom), iluminación dinámica, efectos de partículas y foley/audio en tiempo real.\n\n" +
                            "3. Video Generativo Pesado (Sora / Kling): Los modelos que generan píxeles de video hiperrealista requieren granjas de servidores con GPUs masivas y suscripciones de pago por segundo. VideoCraft integra la dirección con IA y renderiza el resultado completo sin depender de servidores de video costosos.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // System Specs
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceDark),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("ESPECIFICACIONES TÉCNICAS", color = TextSecondaryDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Modelo de IA: Google Gemini 2.5 Flash / 1.5 Flash", color = Color.White, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• Motor de Dirección: CineAI Procedural Hardware-Accelerated 60fps", color = Color.White, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• Exportación: Guiones formateados y proyecto a almacenamiento de Documents", color = Color.White, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• Almacenamiento Seguro: SharedPreferences cifrado local en el dispositivo", color = Color.White, fontSize = 11.sp)
            }
        }
    }
}
