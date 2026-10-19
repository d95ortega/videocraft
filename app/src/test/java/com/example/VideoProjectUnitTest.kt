package com.example

import com.example.ai.CineAiDirectorEngine
import com.example.data.AudioCatalog
import com.example.data.VideoTemplatesData
import com.example.database.ProjectJsonParser
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class VideoProjectUnitTest {

    @Test
    fun testProjectDurationCalculation() {
        val clip1 = VideoClip(name = "Clip 1", durationSec = 4.0f, startTrimSec = 0f, endTrimSec = 3.0f, speed = 1.0f)
        val clip2 = VideoClip(name = "Clip 2", durationSec = 6.0f, startTrimSec = 0f, endTrimSec = 4.0f, speed = 2.0f)

        assertEquals(3.0f, clip1.effectiveDuration, 0.01f)
        assertEquals(2.0f, clip2.effectiveDuration, 0.01f) // 4.0s / 2x speed = 2.0s

        val project = VideoProject(clips = listOf(clip1, clip2))
        assertEquals(5.0f, project.totalDurationSec, 0.01f)
    }

    @Test
    fun testProjectSerialization() {
        val project = VideoProject(
            title = "Test Cinema Video",
            aspectRatio = AspectRatioType.RATIO_16_9,
            clips = listOf(
                VideoClip(name = "Intro", durationSec = 3f, endTrimSec = 3f, filter = VideoFilter.CINEMATIC)
            ),
            textOverlays = listOf(
                TextOverlay(text = "Título Prueba", animation = TextAnimation.BOUNCE)
            ),
            stickers = listOf(
                StickerOverlay(emoji = "🔥")
            ),
            audioTrack = AudioCatalog.availableTracks.first()
        )

        val entity = ProjectJsonParser.toEntity(project)
        val deserialized = ProjectJsonParser.toModel(entity)

        assertEquals(project.title, deserialized.title)
        assertEquals(project.aspectRatio, deserialized.aspectRatio)
        assertEquals(project.clips.size, deserialized.clips.size)
        assertEquals(VideoFilter.CINEMATIC, deserialized.clips.first().filter)
        assertEquals(project.textOverlays.size, deserialized.textOverlays.size)
        assertEquals(TextAnimation.BOUNCE, deserialized.textOverlays.first().animation)
        assertEquals(project.stickers.size, deserialized.stickers.size)
        assertEquals("🔥", deserialized.stickers.first().emoji)
        assertNotNull(deserialized.audioTrack)
    }

    @Test
    fun testTemplatesCatalog() {
        assertTrue(VideoTemplatesData.templates.isNotEmpty())
        for (tmpl in VideoTemplatesData.templates) {
            assertTrue(tmpl.title.isNotBlank())
            assertTrue(tmpl.initialClips.isNotEmpty())
        }
    }

    @Test
    fun testPromptToMovieGeneration() {
        val movie = CineAiDirectorEngine.generateMovieFromPrompt(
            prompt = "Una detective androide buscando la verdad en Neo-Tokyo bajo lluvia de neón",
            selectedGenre = CinemaGenre.SCI_FI,
            productionType = MovieProductionType.FULL_MOVIE
        )

        assertNotNull(movie)
        assertTrue(movie.title.isNotBlank())
        assertEquals(CinemaGenre.SCI_FI, movie.genre)
        assertEquals(6, movie.scenes.size)
        assertTrue(movie.totalDurationSec > 20f)
        assertTrue(movie.scenes.first().dialogue.isNotBlank())

        val videoProject = CineAiDirectorEngine.convertMovieToVideoProject(movie)
        assertEquals(movie.title, videoProject.title)
        assertEquals(movie.scenes.size, videoProject.clips.size)
        assertTrue(videoProject.textOverlays.isNotEmpty())
    }
}
