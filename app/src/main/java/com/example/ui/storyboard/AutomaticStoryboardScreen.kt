package com.example.ui.storyboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.model.AiMovieProject
import com.example.model.MovieScene
import com.example.ui.theme.*

@Composable
fun AutomaticStoryboardScreen(
    currentMovie: AiMovieProject,
    onOpenInStudio: (AiMovieProject) -> Unit,
    modifier: Modifier = Modifier
) {
    var scenes by remember { mutableStateOf(currentMovie.scenes) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(14.dp)
            .testTag("automatic_storyboard_screen")
    ) {
        val isWide = maxWidth >= 760.dp
        val gridColumns = if (isWide) GridCells.Adaptive(minSize = 340.dp) else GridCells.Fixed(1)

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesomeMotion, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Storyboard Visual Automático",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isWide) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = ElectricViolet.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "MURAL DE PRODUCCIÓN PC",
                                    color = ElectricViolet,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${scenes.size} Escenas secuenciadas • ${currentMovie.title}",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        val updated = currentMovie.copy(scenes = scenes)
                        onOpenInStudio(updated)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Exportar a Estudio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = gridColumns,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(scenes, key = { it.id }) { sc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, StudioCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = CoralPink.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "ESCENA ${sc.sceneNumber}",
                                            color = CoralPink,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = sc.actName,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${sc.durationSec.toInt()}s",
                                        color = BrightAmber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // Duplicate button
                                    IconButton(
                                        onClick = {
                                            val idx = scenes.indexOf(sc)
                                            if (idx != -1) {
                                                val copy = sc.copy(
                                                    id = java.util.UUID.randomUUID().toString(),
                                                    sceneNumber = scenes.size + 1
                                                )
                                                scenes = scenes.toMutableList().apply { add(idx + 1, copy) }
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicar", tint = TextSecondaryDark, modifier = Modifier.size(14.dp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    // Delete button
                                    if (scenes.size > 1) {
                                        IconButton(
                                            onClick = {
                                                scenes = scenes.filterNot { it.id == sc.id }
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = TextSecondaryDark, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Visual prompt & banner preview
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(sc.primaryColorHex), Color(sc.secondaryColorHex))
                                        )
                                    )
                                    .padding(8.dp),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Text(
                                    text = sc.visualPrompt,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    maxLines = 2
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Camera & Lighting specs tags
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(color = StudioSurfaceDark, shape = RoundedCornerShape(4.dp)) {
                                    Text(sc.shotType, color = NeonCyan, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                }
                                Surface(color = StudioSurfaceDark, shape = RoundedCornerShape(4.dp)) {
                                    Text(sc.cameraMotion.displayName, color = ElectricViolet, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                }
                                Surface(color = StudioSurfaceDark, shape = RoundedCornerShape(4.dp)) {
                                    Text(sc.atmosphere.displayName, color = BrightAmber, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${sc.speaker}: \"${sc.dialogue}\"",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
