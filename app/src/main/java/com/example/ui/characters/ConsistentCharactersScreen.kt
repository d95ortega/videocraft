package com.example.ui.characters

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CharacterProfile
import com.example.ui.theme.*

@Composable
fun ConsistentCharactersScreen(
    modifier: Modifier = Modifier
) {
    var charactersList by remember {
        mutableStateOf(
            listOf(
                CharacterProfile(
                    name = "Elena Cruz",
                    role = "Protagonista",
                    apparentAge = "29 años",
                    physicalDescription = "Complexión ágil y atlética, mirada analítica, marcas de intemperie y cabello recogido práctico.",
                    wardrobe = "Traje térmico de expedición modular con sensores bio-climáticos y arnés de escalada reforzado.",
                    personality = "Intrépida, perspicaz, con profundo respeto por civilizaciones antiguas y gran temple bajo presión.",
                    voiceTone = "Firme, calmada y observadora",
                    avatarColorHex = 0xFF00E5FFL,
                    scenesCount = 4
                ),
                CharacterProfile(
                    name = "Guardián Aethelgard",
                    role = "Antagonista / Protector",
                    apparentAge = "Atemporal (Entidad Sintética)",
                    physicalDescription = "Figura bípeda imponente de aleación de titanio negro con líneas de luz azul geotérmica pulsante.",
                    wardrobe = "Blindaje ceremonial grabado con jeroglíficos energéticos milenarios.",
                    personality = "Inflexible protector del santuario, programado para evitar cualquier intrusión del mundo exterior.",
                    voiceTone = "Grave, resonante y sintetizada",
                    avatarColorHex = 0xFFFF3B5CL,
                    scenesCount = 2
                ),
                CharacterProfile(
                    name = "Comandante Kai",
                    role = "Mentor / Navegante",
                    apparentAge = "45 años",
                    physicalDescription = "Cicatriz en la ceja izquierda, barba canosa recortada, porte militar relajado.",
                    wardrobe = "Uniforme de vuelo táctico desgastado con insignias de la Alianza Estelar.",
                    personality = "Veterano cínico pero paternal, leal hasta el final.",
                    voiceTone = "Áspera y confiada",
                    avatarColorHex = 0xFF7F5AF0L,
                    scenesCount = 3
                )
            )
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(14.dp)
            .testTag("consistent_characters_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Group, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Personajes Consistentes",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Consistencia de vestuario, rasgos y personalidad en múltiples planos",
                    color = TextSecondaryDark,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nuevo Personaje", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // AI Consistency Notice Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ElectricViolet.copy(alpha = 0.15f),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Las descripciones de vestuario y rasgos físicos se inyectan automáticamente en cada plano para preservar la identidad del personaje entre escenas.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Characters List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(charactersList) { char ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar circle
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(char.avatarColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = char.name,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = CoralPink.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = char.role,
                                            color = CoralPink,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${char.apparentAge} • Voz: ${char.voiceTone}",
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )
                            }

                            Surface(
                                color = StudioSurfaceDark,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${char.scenesCount} Escenas",
                                    color = NeonCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Físico: ${char.physicalDescription}",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vestuario: ${char.wardrobe}",
                            color = BrightAmber,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var nameInput by remember { mutableStateOf("") }
        var roleInput by remember { mutableStateOf("Protagonista") }
        var wardrobeInput by remember { mutableStateOf("") }
        var descInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = StudioSurfaceElevated,
            title = { Text("Nuevo Personaje para la Película", color = Color.White) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nombre del Personaje") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = roleInput,
                        onValueChange = { roleInput = it },
                        label = { Text("Rol (Protagonista, Antagonista, etc.)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = wardrobeInput,
                        onValueChange = { wardrobeInput = it },
                        label = { Text("Vestuario característico") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("Rasgos físicos distintivos") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            charactersList = charactersList + CharacterProfile(
                                name = nameInput.trim(),
                                role = roleInput.trim(),
                                wardrobe = wardrobeInput.ifEmpty { "Traje de aventura adaptable" },
                                physicalDescription = descInput.ifEmpty { "Rasgos determinados y mirada penetrante" },
                                personality = "Valiente y decidido",
                                avatarColorHex = 0xFFFF512FL
                            )
                        }
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPink)
                ) {
                    Text("Guardar Personaje")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar", color = TextSecondaryDark)
                }
            }
        )
    }
}
