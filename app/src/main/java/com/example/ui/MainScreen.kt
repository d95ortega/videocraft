package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import com.example.model.FilmStudioSection
import com.example.ui.characters.ConsistentCharactersScreen
import com.example.ui.credits.CreditsAndPlansScreen
import com.example.ui.export.ExportVideoDialog
import com.example.ui.movie.CinemaTheaterScreen
import com.example.ui.projects.ProjectsGalleryScreen
import com.example.ui.screenplay.AiScreenplayWriterScreen
import com.example.ui.settings.StudioSettingsScreen
import com.example.ui.sidebar.FilmStudioDrawerContent
import com.example.ui.storyboard.AutomaticStoryboardScreen
import com.example.ui.studio.CinematicDirectorStudioScreen
import com.example.ui.studio.VideoStudioScreen
import com.example.ui.theme.*
import com.example.viewmodel.VideoStudioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: VideoStudioViewModel,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var activeSection by remember { mutableStateOf(FilmStudioSection.STUDIO) }

    val project by viewModel.currentProject.collectAsState()
    val currentMovie by viewModel.currentMovie.collectAsState()
    val savedProjects by viewModel.savedProjects.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }

    // BackHandler: return to Studio or close drawer on back
    BackHandler(enabled = drawerState.isOpen || activeSection != FilmStudioSection.STUDIO) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            activeSection = FilmStudioSection.STUDIO
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FilmStudioDrawerContent(
                currentSection = activeSection,
                onSelectSection = { section ->
                    activeSection = section
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .testTag("film_studio_scaffold"),
            containerColor = StudioDarkBg,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = project.title,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = MintGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Guardado ✓",
                                    color = MintGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            },
                            modifier = Modifier.testTag("drawer_menu_button")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú del Estudio", tint = Color.White)
                        }
                    },
                    actions = {
                        // Credits Pill
                        Surface(
                            color = BrightAmber.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BrightAmber.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { activeSection = FilmStudioSection.CREDITS }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("240", color = BrightAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Export Button
                        Button(
                            onClick = { showExportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralPink),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Exportar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = StudioSurfaceDark
                    )
                )
            },
            bottomBar = {
                // Bottom Bar for fast access to key workflow hubs
                NavigationBar(
                    containerColor = StudioSurfaceDark,
                    contentColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    val quickTabs = listOf(
                        Triple(FilmStudioSection.STUDIO, "Estudio", Icons.Default.VideoCameraBack),
                        Triple(FilmStudioSection.SCREENPLAY, "Guionista", Icons.Default.AutoAwesome),
                        Triple(FilmStudioSection.STORYBOARD, "Storyboard", Icons.Default.AutoAwesomeMotion),
                        Triple(FilmStudioSection.THEATER, "Cine Teatro", Icons.Default.Theaters),
                        Triple(FilmStudioSection.TIMELINE_EDITOR, "Montaje", Icons.Default.MovieCreation)
                    )

                    quickTabs.forEach { (sec, label, icon) ->
                        val isSelected = activeSection == sec
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { activeSection = sec },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
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
                            modifier = Modifier.testTag("nav_quick_${sec.name.lowercase()}")
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
                when (activeSection) {
                    FilmStudioSection.STUDIO -> {
                        CinematicDirectorStudioScreen(viewModel = viewModel)
                    }
                    FilmStudioSection.SCREENPLAY -> {
                        AiScreenplayWriterScreen(
                            onLoadMovieToStudio = { movie ->
                                viewModel.onMovieGenerated(movie)
                                activeSection = FilmStudioSection.THEATER
                            }
                        )
                    }
                    FilmStudioSection.CHARACTERS -> {
                        ConsistentCharactersScreen()
                    }
                    FilmStudioSection.STORYBOARD -> {
                        if (currentMovie != null) {
                            AutomaticStoryboardScreen(
                                currentMovie = currentMovie!!,
                                onOpenInStudio = { updatedMovie ->
                                    viewModel.openMovieInEditor(updatedMovie)
                                    activeSection = FilmStudioSection.STUDIO
                                }
                            )
                        } else {
                            CinematicDirectorStudioScreen(viewModel = viewModel)
                        }
                    }
                    FilmStudioSection.THEATER -> {
                        if (currentMovie != null) {
                            CinemaTheaterScreen(
                                movie = currentMovie!!,
                                onOpenInEditor = { movie ->
                                    viewModel.openMovieInEditor(movie)
                                    activeSection = FilmStudioSection.TIMELINE_EDITOR
                                },
                                onExportMovie = { movie ->
                                    viewModel.openMovieInEditor(movie)
                                    showExportDialog = true
                                }
                            )
                        } else {
                            CinematicDirectorStudioScreen(viewModel = viewModel)
                        }
                    }
                    FilmStudioSection.TIMELINE_EDITOR -> {
                        VideoStudioScreen(viewModel = viewModel)
                    }
                    FilmStudioSection.PROJECTS -> {
                        ProjectsGalleryScreen(
                            projects = savedProjects,
                            onOpenProject = { proj ->
                                viewModel.loadProject(proj)
                                activeSection = FilmStudioSection.STUDIO
                            },
                            onDeleteProject = { id ->
                                viewModel.deleteSavedProject(id)
                            },
                            onCreateNew = {
                                viewModel.createNewProject()
                                activeSection = FilmStudioSection.STUDIO
                            }
                        )
                    }
                    FilmStudioSection.CREDITS -> {
                        CreditsAndPlansScreen()
                    }
                    FilmStudioSection.SETTINGS, FilmStudioSection.LOCATIONS, FilmStudioSection.AUDIO_POST -> {
                        StudioSettingsScreen()
                    }
                }
            }
        }
    }

    if (showExportDialog) {
        ExportVideoDialog(
            project = project,
            onDismiss = { showExportDialog = false },
            onExportComplete = { exported ->
                viewModel.onExportCompleted(exported)
            }
        )
    }
}
