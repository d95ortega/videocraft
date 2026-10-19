package com.example.ui.templates

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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Style
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
import com.example.data.VideoTemplatesData
import com.example.model.VideoProject
import com.example.model.VideoTemplate
import com.example.ui.theme.*

@Composable
fun TemplatesScreen(
    onSelectTemplate: (VideoTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Todos") }
    val categories = listOf("Todos", "Redes Sociales", "Cinematográfico", "Celebración", "Negocios")

    val filteredTemplates = remember(selectedCategory) {
        if (selectedCategory == "Todos") {
            VideoTemplatesData.templates
        } else {
            VideoTemplatesData.templates.filter { it.category == selectedCategory }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(16.dp)
            .testTag("templates_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Style, contentDescription = null, tint = CoralPink, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Plantillas Listas",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Crea videos virales y cinematográficos en 1 toque",
            color = TextSecondaryDark,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Categories Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CoralPink.copy(alpha = 0.2f),
                        selectedLabelColor = CoralPink,
                        containerColor = StudioSurfaceElevated,
                        labelColor = TextSecondaryDark
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) CoralPink else Color.Transparent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Templates List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredTemplates) { template ->
                val firstClip = template.initialClips.firstOrNull()
                val col1 = Color(firstClip?.stockScene?.primaryColorHex ?: 0xFF8A2387L)
                val col2 = Color(firstClip?.stockScene?.secondaryColorHex ?: 0xFFE94057L)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectTemplate(template) }
                        .testTag("template_card_${template.id}"),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Thumbnail banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(Brush.horizontalGradient(listOf(col1, col2)))
                                .padding(12.dp)
                        ) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.65f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Text(
                                    text = template.aspectRatio.tag,
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                Text(
                                    text = template.title,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "♫ ${template.defaultMusic}",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Info & Action
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = template.description,
                                    color = TextSecondaryDark,
                                    fontSize = 12.sp,
                                    maxLines = 2
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = { onSelectTemplate(template) },
                                colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Usar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
