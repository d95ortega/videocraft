package com.example.model

import java.util.UUID

enum class AspectRatioType(val label: String, val ratioWidth: Float, val ratioHeight: Float, val tag: String) {
    RATIO_9_16("9:16 Shorts / Reels", 9f, 16f, "9:16"),
    RATIO_16_9("16:9 YouTube / Cine", 16f, 9f, "16:9"),
    RATIO_1_1("1:1 Cuadrado", 1f, 1f, "1:1"),
    RATIO_4_5("4:5 Feed Vertical", 4f, 5f, "4:5")
}

enum class StockSceneType(val title: String, val primaryColorHex: Long, val secondaryColorHex: Long) {
    SUNSET_GRADIENT("Atardecer Mágico", 0xFFFF512FL, 0xFFDD2476L),
    NEON_PULSE("Pulso Neón", 0xFF8A2387L, 0xFFE94057L),
    OCEAN_DEEP("Océano Profundo", 0xFF00C9FFL, 0xFF92FE9DL),
    RETRO_GRID("Cuadrícula Synthwave", 0xFF2B580CL, 0xFF639A67L),
    CYBER_MATRIX("Cyberpunk 2077", 0xFF141E30L, 0xFF243B55L),
    GOLDEN_HOUR("Hora Dorada", 0xFFF12711L, 0xFFF5AF19L),
    AURORA_BOREALIS("Aurora Boreal", 0xFF02AAB0L, 0xFF00CDACL),
    DARK_CINEMA("Cine Nocturno", 0xFF0F2027L, 0xFF203A43L)
}

enum class VideoFilter(val displayName: String, val iconDesc: String) {
    ORIGINAL("Original", "Sin filtro"),
    CINEMATIC("Cine Teal & Orange", "Look cinematográfico"),
    VINTAGE_VHS("Retro VHS 90s", "Textura analógica"),
    FILM_NOIR("Blanco y Negro Noir", "Alto contraste monocromático"),
    CYBER_NEON("Cyber Neón", "Tonos violetas y cian eléctricos"),
    WARM_SUNSET("Atardecer Dorado", "Tonos cálidos de verano"),
    ULTRA_VIVID("Ultra Vívido", "Saturación vibrante"),
    SEPIA("Sepia Vintage", "Tono cálido nostálgico"),
    GLITCH("Glitch POP", "Aberración cromática")
}

enum class TransitionType(val displayName: String) {
    NONE("Corte Directo"),
    FADE_BLACK("Fundido a Negro"),
    DISSOLVE("Disolución Cruzada"),
    SLIDE_LEFT("Deslizar Izquierda"),
    SLIDE_RIGHT("Deslizar Derecha"),
    ZOOM_IN("Zoom Dinámico"),
    FLASH_WHITE("Flash Blanco"),
    WIPE_DOWN("Cortinilla")
}

enum class TextAnimation(val displayName: String) {
    NONE("Fijo"),
    POP("Aparición Pop"),
    TYPEWRITER("Máquina de Escribir"),
    FADE("Desvanecer"),
    NEON_PULSE("Pulso Neón"),
    BOUNCE("Rebote Alegre")
}

data class TextOverlay(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val startTimeSec: Float = 0f,
    val endTimeSec: Float = 3f,
    val textColorHex: Long = 0xFFFFFFFFL,
    val backgroundColorHex: Long? = 0xAA000000L,
    val fontSizeSp: Int = 24,
    val animation: TextAnimation = TextAnimation.POP,
    val positionYPercent: Float = 0.5f // 0f top, 1f bottom
)

data class StickerOverlay(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val startTimeSec: Float = 0f,
    val endTimeSec: Float = 3f,
    val posXPercent: Float = 0.5f,
    val posYPercent: Float = 0.5f,
    val scale: Float = 1.0f
)

data class VideoClip(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val uri: String? = null,
    val durationSec: Float = 3.5f,
    val startTrimSec: Float = 0f,
    val endTrimSec: Float = 3.5f,
    val filter: VideoFilter = VideoFilter.ORIGINAL,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val stockScene: StockSceneType = StockSceneType.SUNSET_GRADIENT,
    val transitionIn: TransitionType = TransitionType.NONE
) {
    val effectiveDuration: Float
        get() = ((endTrimSec - startTrimSec).coerceAtLeast(0.5f)) / speed
}

data class AudioTrack(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val artist: String,
    val genre: String,
    val durationSec: Float = 30f,
    val bpm: Int = 120,
    val volume: Float = 0.8f,
    val isMuted: Boolean = false,
    val noteFrequencies: List<Int> = listOf(261, 329, 392, 523) // C4, E4, G4, C5
)

data class VideoProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Mi Nuevo Video",
    val aspectRatio: AspectRatioType = AspectRatioType.RATIO_9_16,
    val clips: List<VideoClip> = listOf(
        VideoClip(
            name = "Clip de Apertura",
            stockScene = StockSceneType.NEON_PULSE,
            filter = VideoFilter.CINEMATIC,
            durationSec = 3.0f,
            endTrimSec = 3.0f
        ),
        VideoClip(
            name = "Momento Destacado",
            stockScene = StockSceneType.SUNSET_GRADIENT,
            filter = VideoFilter.WARM_SUNSET,
            durationSec = 4.0f,
            endTrimSec = 4.0f,
            transitionIn = TransitionType.ZOOM_IN
        )
    ),
    val textOverlays: List<TextOverlay> = listOf(
        TextOverlay(
            text = "✨ CREA TU HISTORIA ✨",
            startTimeSec = 0f,
            endTimeSec = 2.5f,
            fontSizeSp = 22,
            animation = TextAnimation.POP,
            positionYPercent = 0.3f
        )
    ),
    val stickers: List<StickerOverlay> = listOf(
        StickerOverlay(
            emoji = "🔥",
            startTimeSec = 0.5f,
            endTimeSec = 3.5f,
            posXPercent = 0.8f,
            posYPercent = 0.2f
        )
    ),
    val audioTrack: AudioTrack? = AudioTrack(
        id = "track_synth",
        title = "Synthwave Sunset",
        artist = "VideoCraft Beats",
        genre = "Lo-Fi Electronic",
        durationSec = 30f,
        bpm = 128
    ),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isExported: Boolean = false,
    val exportedUri: String? = null
) {
    val totalDurationSec: Float
        get() = clips.sumOf { it.effectiveDuration.toDouble() }.toFloat().coerceAtLeast(0.5f)
}

data class VideoTemplate(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val aspectRatio: AspectRatioType,
    val defaultMusic: String,
    val initialClips: List<VideoClip>,
    val initialTexts: List<TextOverlay>,
    val initialStickers: List<StickerOverlay>
)

data class ExportSettings(
    val resolution: String = "1080p Full HD",
    val fps: Int = 30,
    val format: String = "MP4 (H.264)",
    val quality: String = "Alta Calidad"
)

sealed interface RenderProgressState {
    data object Idle : RenderProgressState
    data class Processing(val progress: Float, val currentStage: String) : RenderProgressState
    data class Success(val projectTitle: String, val filePath: String, val durationSec: Float) : RenderProgressState
    data class Error(val message: String) : RenderProgressState
}

data class AiScriptIdea(
    val topic: String,
    val hook: String,
    val bodyPoints: List<String>,
    val callToAction: String,
    val suggestedMusic: String,
    val estimatedDuration: String
)
