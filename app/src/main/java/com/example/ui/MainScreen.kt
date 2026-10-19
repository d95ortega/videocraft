package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiMovieProject
import com.example.ui.movie.CinemaTheaterScreen
import com.example.ui.movie.MovieGeneratorScreen
import com.example.ui.projects.ProjectsGalleryScreen
import com.example.ui.studio.VideoStudioScreen
import com.example.ui.templates.TemplatesScreen
import com.example.ui.theme.*
import com.example.viewmodel.StudioScreenTab
import com.example.viewmodel.VideoStudioViewModel

data class BottomNavItem(
    val tab: StudioScreenTab,
    val label: String,
    val icon: ImageVector
)

@Composable
fun MainScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val savedProjects by viewModel.savedProjects.collectAsState()
    val currentMovie by viewModel.currentMovie.collectAsState()

    // Back handling: if in non-generator tab, return to generator on Back
    BackHandler(enabled = currentTab != StudioScreenTab.GENERATOR_IA) {
        viewModel.setTab(StudioScreenTab.GENERATOR_IA)
    }

    val navItems = listOf(
        BottomNavItem(StudioScreenTab.GENERATOR_IA, "Crear IA", Icons.Default.AutoAwesome),
        BottomNavItem(StudioScreenTab.THEATER, "Cine Teatro", Icons.Default.Theaters),
        BottomNavItem(StudioScreenTab.STUDIO, "Estudio", Icons.Default.MovieCreation),
        BottomNavItem(StudioScreenTab.TEMPLATES, "Plantillas", Icons.Default.AutoAwesomeMosaic),
        BottomNavItem(StudioScreenTab.PROJECTS, "Filmoteca", Icons.Default.Folder)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = StudioDarkBg,
        bottomBar = {
            NavigationBar(
                containerColor = StudioSurfaceDark,
                contentColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { item ->
                    val isSelected = currentTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CoralPink,
                            selectedTextColor = CoralPink,
                            unselectedIconColor = TextSecondaryDark,
                            unselectedTextColor = TextSecondaryDark,
                            indicatorColor = CoralPink.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_item_${item.tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                StudioScreenTab.GENERATOR_IA -> {
                    MovieGeneratorScreen(
                        onMovieGenerated = { generatedMovie ->
                            viewModel.onMovieGenerated(generatedMovie)
                        }
                    )
                }
                StudioScreenTab.THEATER -> {
                    if (currentMovie != null) {
                        CinemaTheaterScreen(
                            movie = currentMovie!!,
                            onOpenInEditor = { movie ->
                                viewModel.openMovieInEditor(movie)
                            },
                            onExportMovie = { movie ->
                                viewModel.openMovieInEditor(movie)
                            }
                        )
                    } else {
                        MovieGeneratorScreen(
                            onMovieGenerated = { generatedMovie ->
                                viewModel.onMovieGenerated(generatedMovie)
                            }
                        )
                    }
                }
                StudioScreenTab.STUDIO -> {
                    VideoStudioScreen(viewModel = viewModel)
                }
                StudioScreenTab.TEMPLATES -> {
                    TemplatesScreen(
                        onSelectTemplate = { template ->
                            viewModel.applyTemplate(template)
                        }
                    )
                }
                StudioScreenTab.PROJECTS -> {
                    ProjectsGalleryScreen(
                        projects = savedProjects,
                        onOpenProject = { project ->
                            viewModel.loadProject(project)
                        },
                        onDeleteProject = { id ->
                            viewModel.deleteSavedProject(id)
                        },
                        onCreateNew = {
                            viewModel.setTab(StudioScreenTab.GENERATOR_IA)
                        }
                    )
                }
            }
        }
    }
}
