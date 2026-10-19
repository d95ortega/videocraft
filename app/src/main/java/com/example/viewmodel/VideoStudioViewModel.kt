package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.CineAiDirectorEngine
import com.example.data.AudioCatalog
import com.example.data.StudioAudioSynthesizer
import com.example.database.ProjectJsonParser
import com.example.database.VideoCraftDatabase
import com.example.model.*
import com.example.ui.ai.AiScriptResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class StudioScreenTab {
    GENERATOR_IA,
    THEATER,
    STUDIO,
    TEMPLATES,
    PROJECTS
}

class VideoStudioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VideoCraftDatabase.getDatabase(application)
    private val dao = db.projectDao()
    private val audioSynth = StudioAudioSynthesizer()

    // Current active movie generated from prompt
    private val _currentMovie = MutableStateFlow<AiMovieProject?>(
        CineAiDirectorEngine.generateMovieFromPrompt(
            prompt = "Una detective androide buscando la verdad en Neo-Tokyo bajo lluvia de neón",
            selectedGenre = CinemaGenre.SCI_FI,
            productionType = MovieProductionType.CINEMATIC_TRAILER
        )
    )
    val currentMovie: StateFlow<AiMovieProject?> = _currentMovie.asStateFlow()

    private val _savedMovies = MutableStateFlow<List<AiMovieProject>>(emptyList())
    val savedMovies: StateFlow<List<AiMovieProject>> = _savedMovies.asStateFlow()

    private val _currentProject = MutableStateFlow(
        CineAiDirectorEngine.convertMovieToVideoProject(_currentMovie.value!!)
    )
    val currentProject: StateFlow<VideoProject> = _currentProject.asStateFlow()

    private val _savedProjects = MutableStateFlow<List<VideoProject>>(emptyList())
    val savedProjects: StateFlow<List<VideoProject>> = _savedProjects.asStateFlow()

    private val _currentTimeSec = MutableStateFlow(0f)
    val currentTimeSec: StateFlow<Float> = _currentTimeSec.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _selectedClipIndex = MutableStateFlow(0)
    val selectedClipIndex: StateFlow<Int> = _selectedClipIndex.asStateFlow()

    private val _currentTab = MutableStateFlow(StudioScreenTab.GENERATOR_IA)
    val currentTab: StateFlow<StudioScreenTab> = _currentTab.asStateFlow()

    private var playbackJob: Job? = null
    private var audioJob: Job? = null

    init {
        // Load saved projects from Room
        viewModelScope.launch {
            dao.getAllProjects().collect { entities ->
                val projects = entities.map { ProjectJsonParser.toModel(it) }
                _savedProjects.value = projects
            }
        }
    }

    fun setTab(tab: StudioScreenTab) {
        if (_isPlaying.value) {
            pause()
        }
        _currentTab.value = tab
    }

    fun onMovieGenerated(movie: AiMovieProject) {
        pause()
        _currentMovie.value = movie
        val converted = CineAiDirectorEngine.convertMovieToVideoProject(movie)
        _currentProject.value = converted
        _savedMovies.value = listOf(movie) + _savedMovies.value.filterNot { it.id == movie.id }
        _currentTimeSec.value = 0f
        _selectedClipIndex.value = 0
        _currentTab.value = StudioScreenTab.THEATER
        persistCurrentProject()
    }

    fun openMovieInEditor(movie: AiMovieProject) {
        pause()
        _currentMovie.value = movie
        val converted = CineAiDirectorEngine.convertMovieToVideoProject(movie)
        _currentProject.value = converted
        _currentTimeSec.value = 0f
        _selectedClipIndex.value = 0
        _currentTab.value = StudioScreenTab.STUDIO
        persistCurrentProject()
    }

    fun togglePlay() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        _isPlaying.value = true
        val total = _currentProject.value.totalDurationSec
        if (_currentTimeSec.value >= total) {
            _currentTimeSec.value = 0f
        }

        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val stepSec = 0.05f
            val stepMs = 50L
            while (_isPlaying.value && _currentTimeSec.value < total) {
                delay(stepMs)
                val next = _currentTimeSec.value + stepSec
                if (next >= total) {
                    _currentTimeSec.value = 0f
                    _isPlaying.value = false
                    break
                } else {
                    _currentTimeSec.value = next
                }
            }
        }

        // Start native audio synthesis if audio track present and not muted
        val track = _currentProject.value.audioTrack
        if (track != null && !track.isMuted) {
            audioJob?.cancel()
            audioJob = viewModelScope.launch {
                val remaining = (total - _currentTimeSec.value).coerceAtLeast(0.5f)
                audioSynth.playTrackLoop(track, remaining)
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
        audioJob?.cancel()
        audioJob = null
        audioSynth.stop()
    }

    fun seekTo(seconds: Float) {
        val total = _currentProject.value.totalDurationSec
        _currentTimeSec.value = seconds.coerceIn(0f, total)
    }

    fun selectClip(index: Int) {
        _selectedClipIndex.value = index.coerceIn(0, (_currentProject.value.clips.size - 1).coerceAtLeast(0))
    }

    fun updateProjectTitle(newTitle: String) {
        val updated = _currentProject.value.copy(title = newTitle, updatedAt = System.currentTimeMillis())
        _currentProject.value = updated
        persistCurrentProject()
    }

    fun updateClip(index: Int, clip: VideoClip) {
        val currentClips = _currentProject.value.clips.toMutableList()
        if (index in currentClips.indices) {
            currentClips[index] = clip
            val updated = _currentProject.value.copy(
                clips = currentClips,
                updatedAt = System.currentTimeMillis()
            )
            _currentProject.value = updated
            persistCurrentProject()
        }
    }

    fun addClip(clip: VideoClip) {
        val updatedClips = _currentProject.value.clips + clip
        val updated = _currentProject.value.copy(
            clips = updatedClips,
            updatedAt = System.currentTimeMillis()
        )
        _currentProject.value = updated
        _selectedClipIndex.value = updatedClips.size - 1
        persistCurrentProject()
    }

    fun deleteClip(index: Int) {
        val currentClips = _currentProject.value.clips.toMutableList()
        if (currentClips.size > 1 && index in currentClips.indices) {
            currentClips.removeAt(index)
            val updated = _currentProject.value.copy(
                clips = currentClips,
                updatedAt = System.currentTimeMillis()
            )
            _currentProject.value = updated
            _selectedClipIndex.value = (_selectedClipIndex.value - 1).coerceAtLeast(0)
            persistCurrentProject()
        }
    }

    fun addTextOverlay(textOverlay: TextOverlay) {
        val updated = _currentProject.value.copy(
            textOverlays = _currentProject.value.textOverlays + textOverlay,
            updatedAt = System.currentTimeMillis()
        )
        _currentProject.value = updated
        persistCurrentProject()
    }

    fun removeTextOverlay(id: String) {
        val updated = _currentProject.value.copy(
            textOverlays = _currentProject.value.textOverlays.filterNot { it.id == id },
            updatedAt = System.currentTimeMillis()
        )
        _currentProject.value = updated
        persistCurrentProject()
    }

    fun addSticker(sticker: StickerOverlay) {
        val updated = _currentProject.value.copy(
            stickers = _currentProject.value.stickers + sticker,
            updatedAt = System.currentTimeMillis()
        )
        _currentProject.value = updated
        persistCurrentProject()
    }

    fun removeSticker(id: String) {
        val updated = _currentProject.value.copy(
            stickers = _currentProject.value.stickers.filterNot { it.id == id },
            updatedAt = System.currentTimeMillis()
        )
        _currentProject.value = updated
        persistCurrentProject()
    }

    fun setAudioTrack(track: AudioTrack?) {
        val updated = _currentProject.value.copy(
            audioTrack = track,
            updatedAt = System.currentTimeMillis()
        )
        _currentProject.value = updated
        persistCurrentProject()
    }

    fun playSfx(freqHz: Int) {
        viewModelScope.launch {
            audioSynth.playSfx(freqHz)
        }
    }

    fun setAspectRatio(ratio: AspectRatioType) {
        val updated = _currentProject.value.copy(
            aspectRatio = ratio,
            updatedAt = System.currentTimeMillis()
        )
        _currentProject.value = updated
        persistCurrentProject()
    }

    fun createNewProject() {
        pause()
        val newProj = VideoProject(
            title = "Video Proyecto #${(100..999).random()}"
        )
        _currentProject.value = newProj
        _currentTimeSec.value = 0f
        _selectedClipIndex.value = 0
        _currentTab.value = StudioScreenTab.STUDIO
        persistCurrentProject()
    }

    fun loadProject(project: VideoProject) {
        pause()
        _currentProject.value = project
        _currentTimeSec.value = 0f
        _selectedClipIndex.value = 0
        _currentTab.value = StudioScreenTab.STUDIO
    }

    fun deleteSavedProject(id: String) {
        viewModelScope.launch {
            dao.deleteById(id)
            if (_currentProject.value.id == id) {
                createNewProject()
            }
        }
    }

    fun applyTemplate(template: VideoTemplate) {
        pause()
        val matchedAudio = AudioCatalog.availableTracks.firstOrNull {
            it.title.contains(template.defaultMusic.take(5), ignoreCase = true)
        } ?: AudioCatalog.availableTracks.first()

        val newProject = VideoProject(
            title = template.title,
            aspectRatio = template.aspectRatio,
            clips = template.initialClips,
            textOverlays = template.initialTexts,
            stickers = template.initialStickers,
            audioTrack = matchedAudio,
            updatedAt = System.currentTimeMillis()
        )
        _currentProject.value = newProject
        _currentTimeSec.value = 0f
        _selectedClipIndex.value = 0
        _currentTab.value = StudioScreenTab.STUDIO
        persistCurrentProject()
    }

    fun applyAiScript(script: AiScriptResult) {
        pause()
        val clips = script.scenes.mapIndexed { idx, scene ->
            VideoClip(
                name = scene.title,
                durationSec = scene.durationSec,
                endTrimSec = scene.durationSec,
                stockScene = scene.stockScene,
                filter = scene.filter,
                transitionIn = if (idx == 0) TransitionType.NONE else TransitionType.ZOOM_IN
            )
        }

        var accum = 0f
        val texts = mutableListOf<TextOverlay>()
        // Hook text
        texts.add(
            TextOverlay(
                text = script.hook,
                startTimeSec = 0f,
                endTimeSec = 2.5f,
                textColorHex = 0xFFFFD700L,
                fontSizeSp = 24,
                animation = TextAnimation.BOUNCE,
                positionYPercent = 0.3f
            )
        )

        script.scenes.forEach { scene ->
            texts.add(
                TextOverlay(
                    text = scene.overlayText,
                    startTimeSec = accum,
                    endTimeSec = accum + scene.durationSec,
                    textColorHex = 0xFFFFFFFFL,
                    fontSizeSp = 20,
                    animation = TextAnimation.POP,
                    positionYPercent = 0.7f
                )
            )
            accum += scene.durationSec
        }

        // Call to action text
        texts.add(
            TextOverlay(
                text = script.callToAction,
                startTimeSec = (accum - 2.5f).coerceAtLeast(0f),
                endTimeSec = accum,
                textColorHex = 0xFFFF3B5CL,
                fontSizeSp = 22,
                animation = TextAnimation.POP,
                positionYPercent = 0.5f
            )
        )

        val updated = _currentProject.value.copy(
            title = script.topic,
            clips = clips,
            textOverlays = texts,
            audioTrack = AudioCatalog.availableTracks.first(),
            updatedAt = System.currentTimeMillis()
        )
        _currentProject.value = updated
        _currentTimeSec.value = 0f
        _selectedClipIndex.value = 0
        persistCurrentProject()
    }

    fun onExportCompleted(exportedProject: VideoProject) {
        _currentProject.value = exportedProject
        persistCurrentProject()
    }

    private fun persistCurrentProject() {
        viewModelScope.launch {
            val entity = ProjectJsonParser.toEntity(_currentProject.value)
            dao.insertOrUpdate(entity)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioSynth.stop()
    }
}
