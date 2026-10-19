package com.example.model

import java.util.UUID

enum class MovieProductionType(val displayName: String, val sceneCount: Int, val description: String) {
    EXPRESS_CLIP("Corto / Video Rápido", 2, "Ideal para Shorts, TikTok y clips de alto impacto (15s - 25s)"),
    CINEMATIC_TRAILER("Tráiler de Cine", 4, "Ritmo dinámico, tensión creciente y presentación de trama (40s - 60s)"),
    FULL_MOVIE("Película / Cortometraje", 6, "Narrativa completa: Prólogo, Desarrollo, Giro, Clímax y Desenlace (1.5 - 2 min)")
}

enum class CinemaGenre(val displayName: String, val emoji: String, val defaultMusic: String) {
    SCI_FI("Ciencia Ficción", "🚀", "Synthwave Odyssey"),
    ACTION_THRILLER("Acción & Suspenso", "💥", "Pulse of Danger"),
    FANTASY_MYTH("Fantasía Épica", "⚔️", "Kingdom of Legends"),
    ANIME_STUDIO("Anime & Animación", "✨", "Tokyo Neon Dreams"),
    FILM_NOIR("Cine Negro & Misterio", "🕵️", "Midnight Rain Noir"),
    HORROR_COSMIC("Terror & Misterio", "👁️", "Shadows in the Dark"),
    DOCUMENTARY_NATURE("Documental", "🌿", "Earth Chronicles"),
    DRAMA_CINEMA("Drama Cinematográfico", "🎭", "Emotional Resonance")
}

enum class CameraMotion(val displayName: String) {
    SLOW_PAN_RIGHT("Panorámica Derecha"),
    SLOW_PAN_LEFT("Panorámica Izquierda"),
    SLOW_ZOOM_IN("Zoom Lento de Acercamiento"),
    SLOW_ZOOM_OUT("Zoom Lento de Alejamiento"),
    DOLLY_PUSH("Dolly Hacia Adelante"),
    AERIAL_SWEEP("Barrido Aéreo"),
    STATIC_STILL("Plano Fijo Estable")
}

enum class AtmosphereEffect(val displayName: String) {
    NONE("Ninguno"),
    CYBER_RAIN("Lluvia Neón"),
    GLOWING_EMBERS("Chispas / Cenizas Flotantes"),
    SPACE_STARS("Estrellas y Polvo Espacial"),
    FOG_SMOKE("Niebla y Humo Denso"),
    LENS_FLARE("Destellos de Lente Anamórficos")
}

data class MovieScene(
    val id: String = UUID.randomUUID().toString(),
    val sceneNumber: Int,
    val actName: String, // e.g. "Prólogo", "Acto I: La Revelación", "Clímax", "Desenlace"
    val visualPrompt: String,
    val shotType: String, // e.g. "Plano General Aéreo", "Primer Plano Dramático", "Plano Detalle"
    val cameraMotion: CameraMotion = CameraMotion.SLOW_ZOOM_IN,
    val atmosphere: AtmosphereEffect = AtmosphereEffect.NONE,
    val speaker: String = "NARRADOR",
    val dialogue: String,
    val durationSec: Float = 6.0f,
    val primaryColorHex: Long = 0xFF141E30L,
    val secondaryColorHex: Long = 0xFF243B55L,
    val filter: VideoFilter = VideoFilter.CINEMATIC,
    val sfxCue: String? = null
)

data class AiMovieProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val tagline: String,
    val synopsis: String,
    val originalPrompt: String,
    val genre: CinemaGenre,
    val productionType: MovieProductionType,
    val aspectRatio: AspectRatioType = AspectRatioType.RATIO_16_9,
    val director: String = "CineAI Studio",
    val scenes: List<MovieScene>,
    val createdAt: Long = System.currentTimeMillis(),
    val isExported: Boolean = false
) {
    val totalDurationSec: Float
        get() = scenes.sumOf { it.durationSec.toDouble() }.toFloat().coerceAtLeast(1f)
}
