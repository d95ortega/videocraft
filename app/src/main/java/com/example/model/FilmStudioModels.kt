package com.example.model

import java.util.UUID

enum class FilmStudioSection(val title: String, val category: String) {
    STUDIO("Estudio Cinematográfico", "Producción"),
    SCREENPLAY("Guionista IA", "Pre-producción"),
    CHARACTERS("Personajes Consistentes", "Pre-producción"),
    LOCATIONS("Escenarios & Sets", "Pre-producción"),
    STORYBOARD("Storyboard Visual", "Producción"),
    THEATER("Monitor de Cine", "Visualización"),
    AUDIO_POST("Audio & Banda Sonora", "Post-producción"),
    TIMELINE_EDITOR("Editor & Montaje", "Post-producción"),
    PROJECTS("Mis Proyectos", "Biblioteca"),
    CREDITS("Créditos & Planes", "Cuenta"),
    SETTINGS("Configuración & API", "Sistema")
}

data class CharacterProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String = "Protagonista", // Protagonista, Antagonista, Secundario, Mentor
    val apparentAge: String = "28 años",
    val physicalDescription: String,
    val wardrobe: String,
    val personality: String,
    val voiceTone: String = "Determinada y serena",
    val avatarColorHex: Long = 0xFF7F5AF0L,
    val scenesCount: Int = 1
)

data class LocationProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val environmentType: String = "Exterior / Montañas Olvidadas",
    val visualDescription: String,
    val lightingMood: String = "Luz dorada de atardecer filtrada entre neblina",
    val primaryColorHex: Long = 0xFF0F2027L
)

enum class ShotTypeEnum(val label: String, val desc: String) {
    CLOSE_UP("Primer Plano (Close-up)", "Enfocado en las emociones del rostro"),
    MEDIUM_SHOT("Plano Medio", "Desde la cintura, equilibra personaje y acción"),
    WIDE_SHOT("Plano General (Wide)", "Muestra la inmensidad del entorno"),
    OVERHEAD("Plano Cenital (Top-down)", "Cámara mirando directamente hacia abajo"),
    LOW_ANGLE("Contrapicado (Low angle)", "Otorga poder, grandeza y monumentalidad"),
    DUTCH_ANGLE("Plano Holandés", "Inclinado dramático para tensión o peligro")
}

enum class CameraMoveEnum(val label: String, val desc: String) {
    DOLLY_IN("Dolly In", "Acercamiento frontal cinematográfico"),
    DOLLY_OUT("Dolly Out", "Alejamiento para revelar el entorno"),
    TRAVELLING("Travelling Lateral", "Sigue al personaje en paralelo"),
    PAN("Panorámica Horizontal", "Barrido suave de izquierda a derecha"),
    ORBIT("Órbita 360°", "Giro circular alrededor del sujeto"),
    SLOW_MOTION("Cámara Lenta (60fps)", "Resalta la épica y el detalle"),
    STATIC("Cámara Fija", "Composición clásica y contemplativa")
}

enum class LightingStyleEnum(val label: String, val colorTemp: String) {
    NEON_CYBER("Neón Cyberpunk", "Cian y Magenta eléctrico (7500K)"),
    GOLDEN_HOUR("Hora Dorada", "Cálido atardecer natural (3200K)"),
    CHIAROSCURO("Claroscuro Dramático", "Alto contraste con sombras profundas"),
    LUNAR_COLD("Luz Lunar Fría", "Azul oscuro nocturno (6500K)"),
    STUDIO_SOFT("Difusa Suave", "Iluminación cinematográfica balanceada")
}

enum class VisualStyleEnum(val label: String, val aspectHint: String) {
    ANAMORPHIC_35MM("Cine Anamórfico 35mm (Arri Alexa)", "Look cinematográfico con bokeh ovalado"),
    IMAX_70MM("IMAX 70mm Hiperrealista", "Máxima nitidez, grano orgánico y profundidad"),
    CYBERPUNK_NEON("Cyberpunk Futurista 2077", "Reflejos holográficos y lluvia de neón"),
    ANIME_MASTERPIECE("Anime Cinematográfico", "Estilo Studio Ghibli / Makoto Shinkai"),
    DARK_FANTASY("Fantasía Oscura Épica", "Atmósfera medieval con niebla mística"),
    FILM_NOIR("Film Noir Clásico", "Monocromático de alto contraste y sombras"),
    BBC_DOCUMENTARY("Documental de Naturaleza BBC", "Gran angular fotorrealista de alta definición")
}

data class ScreenplaySceneData(
    val sceneNumber: Int,
    val sceneHeading: String, // INT. o EXT. LUGAR - DÍA/NOCHE
    val visualAction: String,
    val characterDialogues: List<Pair<String, String>>, // Personaje -> Diálogo
    val shotSuggestion: ShotTypeEnum = ShotTypeEnum.WIDE_SHOT,
    val cameraMoveSuggestion: CameraMoveEnum = CameraMoveEnum.DOLLY_IN,
    val lightingSuggestion: LightingStyleEnum = LightingStyleEnum.GOLDEN_HOUR,
    val durationSec: Float = 6.0f,
    val soundEffects: String = "Viento suave entre ruinas ancestrales",
    val isApproved: Boolean = true
)

data class FullScreenplay(
    val title: String,
    val logline: String,
    val synopsis: String,
    val genre: CinemaGenre,
    val visualStyle: VisualStyleEnum,
    val protagonist: CharacterProfile,
    val antagonist: CharacterProfile?,
    val scenes: List<ScreenplaySceneData>
)
