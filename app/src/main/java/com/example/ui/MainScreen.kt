package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FilmStudioSection
import com.example.ui.characters.ConsistentCharactersScreen
import com.example.ui.credits.CreditsAndPlansScreen
import com.example.ui.export.ExportVideoDialog
import com.example.ui.movie.CinemaTheaterScreen
import com.example.ui.pc.PcModeScreen
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

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    // BackHandler: return to Studio or close drawer on back
    BackHandler(enabled = drawerState.isOpen || activeSection != FilmStudioSection.STUDIO) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            activeSection = FilmStudioSection.STUDIO
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.Spacebar -> {
                            viewModel.togglePlay()
                            true
                        }
                        Key.DirectionLeft -> {
                            val cur = viewModel.currentTimeSec.value
                            viewModel.seekTo((cur - 1.5f).coerceAtLeast(0f))
                            true
                        }
                        Key.DirectionRight -> {
                            val cur = viewModel.currentTimeSec.value
                            viewModel.seekTo((cur + 1.5f).coerceAtMost(project.totalDurationSec))
                            true
                        }
                        Key.Escape -> {
                            if (activeSection != FilmStudioSection.STUDIO) {
                                activeSection = FilmStudioSection.STUDIO
                                true
                            } else {
                                false
                            }
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
    ) {
        val isDesktop = maxWidth >= 760.dp

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
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("film_studio_scaffold"),
                containerColor = StudioDarkBg,
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isDesktop) "VideoCraft AI Film Studio • ${project.title}" else project.title,
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
                            // PC Mode shortcut button
                            IconButton(
                                onClick = { activeSection = FilmStudioSection.PC_MODE },
                                modifier = Modifier.testTag("pc_mode_topbar_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LaptopMac,
                                    contentDescription = "Modo PC & Web",
                                    tint = if (activeSection == FilmStudioSection.PC_MODE) NeonCyan else Color.White
                                )
                            }

                            // Credits Pill
                            Surface(
                                color = BrightAmber.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BrightAmber.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .padding(end = 6.dp)
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
                    if (!isDesktop) {
                        // Bottom Bar for mobile devices
                        NavigationBar(
                            containerColor = StudioSurfaceDark,
                            contentColor = Color.White,
                            tonalElevation = 8.dp
                        ) {
                            val quickTabs = listOf(
                                Triple(FilmStudioSection.STUDIO, "Estudio", Icons.Default.VideoCameraBack),
                                Triple(FilmStudioSection.SCREENPLAY, "Guionista", Icons.Default.AutoAwesome),
                                Triple(FilmStudioSection.STORYBOARD, "Storyboard", Icons.Default.AutoAwesomeMotion),
                                Triple(FilmStudioSection.PC_MODE, "PC/Web", Icons.Default.LaptopMac),
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
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Desktop Navigation Rail on large screens / PC
                    if (isDesktop) {
                        NavigationRail(
                            containerColor = StudioSurfaceDark,
                            contentColor = Color.White,
                            modifier = Modifier.width(80.dp)
                        ) {
                            val railTabs = listOf(
                                Triple(FilmStudioSection.STUDIO, "Estudio", Icons.Default.VideoCameraBack),
                                Triple(FilmStudioSection.SCREENPLAY, "Guion", Icons.Default.AutoAwesome),
                                Triple(FilmStudioSection.CHARACTERS, "Elenco", Icons.Default.Group),
                                Triple(FilmStudioSection.STORYBOARD, "Board", Icons.Default.AutoAwesomeMotion),
                                Triple(FilmStudioSection.THEATER, "Teatro", Icons.Default.Theaters),
                                Triple(FilmStudioSection.TIMELINE_EDITOR, "Montaje", Icons.Default.MovieCreation),
                                Triple(FilmStudioSection.PROJECTS, "Proyectos", Icons.Default.Folder),
                                Triple(FilmStudioSection.PC_MODE, "PC", Icons.Default.LaptopMac)
                            )

                            railTabs.forEach { (sec, label, icon) ->
                                val isSelected = activeSection == sec
                                NavigationRailItem(
                                    selected = isSelected,
                                    onClick = { activeSection = sec },
                                    icon = {
                                        Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp))
                                    },
                                    label = { Text(label, fontSize = 9.sp) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = CoralPink,
                                        selectedTextColor = CoralPink,
                                        unselectedIconColor = TextSecondaryDark,
                                        unselectedTextColor = TextSecondaryDark,
                                        indicatorColor = CoralPink.copy(alpha = 0.15f)
                                    )
                                )
                            }
                        }
                    }

                    // Content Area
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
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
                            FilmStudioSection.PC_MODE -> {
                                PcModeScreen(onNavigateToStudio = {
                                    activeSection = FilmStudioSection.STUDIO
                                })
                            }
                            FilmStudioSection.SETTINGS, FilmStudioSection.LOCATIONS, FilmStudioSection.AUDIO_POST -> {
                                StudioSettingsScreen()
                            }
                        }
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
