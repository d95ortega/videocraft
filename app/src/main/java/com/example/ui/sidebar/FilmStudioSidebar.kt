package com.example.ui.sidebar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FilmStudioSection
import com.example.ui.theme.*

data class DrawerItem(
    val section: FilmStudioSection,
    val icon: ImageVector
)

@Composable
fun FilmStudioDrawerContent(
    currentSection: FilmStudioSection,
    onSelectSection: (FilmStudioSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        DrawerItem(FilmStudioSection.STUDIO, Icons.Default.VideoCameraBack),
        DrawerItem(FilmStudioSection.SCREENPLAY, Icons.Default.AutoAwesome),
        DrawerItem(FilmStudioSection.CHARACTERS, Icons.Default.Group),
        DrawerItem(FilmStudioSection.STORYBOARD, Icons.Default.AutoAwesomeMotion),
        DrawerItem(FilmStudioSection.THEATER, Icons.Default.Theaters),
        DrawerItem(FilmStudioSection.TIMELINE_EDITOR, Icons.Default.MovieCreation),
        DrawerItem(FilmStudioSection.PROJECTS, Icons.Default.Folder),
        DrawerItem(FilmStudioSection.CREDITS, Icons.Default.Bolt),
        DrawerItem(FilmStudioSection.PC_MODE, Icons.Default.LaptopMac),
        DrawerItem(FilmStudioSection.SETTINGS, Icons.Default.Settings)
    )

    ModalDrawerSheet(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight(),
        drawerContainerColor = StudioSurfaceDark,
        drawerContentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Brand Logo Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CoralPink),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MovieFilter, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "VIDEOCRAFT",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "AI Film Studio Pro",
                        color = BrightAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(color = StudioCardBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Items
            items.forEach { item ->
                val isSelected = currentSection == item.section
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.section.title,
                            tint = if (isSelected) CoralPink else TextSecondaryDark,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.section.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextSecondaryDark
                        )
                    },
                    selected = isSelected,
                    onClick = { onSelectSection(item.section) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = CoralPink.copy(alpha = 0.15f),
                        unselectedContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .testTag("drawer_item_${item.section.name.lowercase()}")
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(color = StudioCardBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Credits Summary in Footer
            Surface(
                color = StudioSurfaceElevated,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, StudioCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(BrightAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("240 Créditos Disponibles", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Plan Creador Pro Activo", color = TextSecondaryDark, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}
