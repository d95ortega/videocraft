package com.example.ai

import com.example.data.AudioCatalog
import com.example.model.*
import java.util.UUID

object CineAiDirectorEngine {

    fun generateMovieFromPrompt(
        prompt: String,
        selectedGenre: CinemaGenre? = null,
        productionType: MovieProductionType = MovieProductionType.CINEMATIC_TRAILER,
        aspectRatio: AspectRatioType = AspectRatioType.RATIO_16_9
    ): AiMovieProject {
        val cleanPrompt = prompt.trim().ifEmpty { "Una expedición futurista a lo desconocido" }
        val genre = selectedGenre ?: inferGenre(cleanPrompt)
        val title = generateCinematicTitle(cleanPrompt, genre)
        val tagline = generateTagline(genre)
        val synopsis = "Una impactante producción cinematográfica generada por IA: $cleanPrompt. A través de impresionantes planos y atmósfera envolvente, seguimos una historia épica de revelación y supervivencia."

        val sceneCount = productionType.sceneCount
        val scenes = generateScenesForGenreAndPrompt(cleanPrompt, genre, sceneCount)

        return AiMovieProject(
            id = UUID.randomUUID().toString(),
            title = title,
            tagline = tagline,
            synopsis = synopsis,
            originalPrompt = cleanPrompt,
            genre = genre,
            productionType = productionType,
            aspectRatio = aspectRatio,
            director = "CineAI Director Studio",
            scenes = scenes
        )
    }

    private fun inferGenre(prompt: String): CinemaGenre {
        val lower = prompt.lowercase()
        return when {
            lower.contains("androide") || lower.contains("robot") || lower.contains("cyber") ||
                    lower.contains("espaci") || lower.contains("nave") || lower.contains("futur") ||
                    lower.contains("planeta") || lower.contains("alien") -> CinemaGenre.SCI_FI

            lower.contains("mago") || lower.contains("dragon") || lower.contains("espada") ||
                    lower.contains("reino") || lower.contains("fantas") || lower.contains("magia") -> CinemaGenre.FANTASY_MYTH

            lower.contains("detective") || lower.contains("crimen") || lower.contains("asesin") ||
                    lower.contains("sombras") || lower.contains("noche") || lower.contains("noir") -> CinemaGenre.FILM_NOIR

            lower.contains("miedo") || lower.contains("terror") || lower.contains("fantasma") ||
                    lower.contains("monstruo") || lower.contains("oscuro") || lower.contains("horror") -> CinemaGenre.HORROR_COSMIC

            lower.contains("carrera") || lower.contains("auto") || lower.contains("persegu") ||
                    lower.contains("mision") || lower.contains("disparo") || lower.contains("accion") -> CinemaGenre.ACTION_THRILLER

            lower.contains("anime") || lower.contains("dibujo") || lower.contains("animad") ||
                    lower.contains("japon") || lower.contains("manga") -> CinemaGenre.ANIME_STUDIO

            lower.contains("naturalez") || lower.contains("animal") || lower.contains("selva") ||
                    lower.contains("tierra") || lower.contains("mar") || lower.contains("documental") -> CinemaGenre.DOCUMENTARY_NATURE

            else -> CinemaGenre.SCI_FI
        }
    }

    private fun generateCinematicTitle(prompt: String, genre: CinemaGenre): String {
        val words = prompt.split(" ").filter { it.length > 3 }
        val keyword = words.firstOrNull { !listOf("para", "como", "sobre", "donde", "pelicula", "video", "crear", "hacer").contains(it.lowercase()) }
            ?.replaceFirstChar { it.uppercase() } ?: "Horizonte"

        return when (genre) {
            CinemaGenre.SCI_FI -> "CHRONOS: EL MISTERIO DE $keyword".uppercase()
            CinemaGenre.ACTION_THRILLER -> "OPERACIÓN $keyword: PUNTO CRÍTICO".uppercase()
            CinemaGenre.FANTASY_MYTH -> "LA LEYENDA DE $keyword Y EL REINO OLVIDADO".uppercase()
            CinemaGenre.ANIME_STUDIO -> "DESTINO $keyword: CRÓNICAS DE NEÓN".uppercase()
            CinemaGenre.FILM_NOIR -> "SOMBRAS EN $keyword: CASO CERRADO".uppercase()
            CinemaGenre.HORROR_COSMIC -> "EL SUSURRO DE $keyword EN LA PENUMBRA".uppercase()
            CinemaGenre.DOCUMENTARY_NATURE -> "PLANETA SALVAJE: EL SECRETO DE $keyword".uppercase()
            CinemaGenre.DRAMA_CINEMA -> "LOS ECOS DE $keyword".uppercase()
        }
    }

    private fun generateTagline(genre: CinemaGenre): String {
        return when (genre) {
            CinemaGenre.SCI_FI -> "En los confines de la galaxia, la verdad es el arma más peligrosa."
            CinemaGenre.ACTION_THRILLER -> "No hay marcha atrás. El reloj sigue corriendo."
            CinemaGenre.FANTASY_MYTH -> "Cuando la magia despierta, los antiguos héroes deben alzarse."
            CinemaGenre.ANIME_STUDIO -> "Un viaje a través de dimensiones donde cada latido cuenta."
            CinemaGenre.FILM_NOIR -> "En esta ciudad, hasta las sombras tienen secretos que ocultar."
            CinemaGenre.HORROR_COSMIC -> "Hay cosas que la humanidad nunca debió haber despertado."
            CinemaGenre.DOCUMENTARY_NATURE -> "Un testimonio visual deslumbrante de la majestuosidad de nuestro planeta."
            CinemaGenre.DRAMA_CINEMA -> "Una conmovedora travesía sobre el coraje y la memoria humana."
        }
    }

    private fun generateScenesForGenreAndPrompt(prompt: String, genre: CinemaGenre, count: Int): List<MovieScene> {
        val sceneTemplates = when (genre) {
            CinemaGenre.SCI_FI -> listOf(
                SceneBlueprint(
                    act = "Prólogo: La Anomalía",
                    shot = "Plano General Panorámico de Ciudad Futurista",
                    promptDesc = "Rascacielos colosales bajo nubes púrpuras, tráfico de naves aéreas y lluvia de neón sobre el asfalto mojado.",
                    motion = CameraMotion.AERIAL_SWEEP,
                    atmo = AtmosphereEffect.CYBER_RAIN,
                    speaker = "NARRADOR",
                    dialogue = "Todo comenzó la noche en que la señal interceptada reveló una verdad que desafiaba nuestra existencia.",
                    colors = 0xFF0F2027L to 0xFF203A43L,
                    filter = VideoFilter.CYBER_NEON
                ),
                SceneBlueprint(
                    act = "Acto I: La Revelación",
                    shot = "Primer Plano Medio en el Laboratorio Cibernético",
                    promptDesc = "Interfaces holográficas flotando en el aire reflejadas en los ojos del protagonista. Luces de alerta titilando.",
                    motion = CameraMotion.SLOW_ZOOM_IN,
                    atmo = AtmosphereEffect.LENS_FLARE,
                    speaker = "COMANDANTE NOVA",
                    dialogue = "Los datos no mienten... la nave no está abandonada. Alguien o algo sigue a bordo.",
                    colors = 0xFF141E30L to 0xFF243B55L,
                    filter = VideoFilter.CINEMATIC
                ),
                SceneBlueprint(
                    act = "Acto II: El Viaje a lo Desconocido",
                    shot = "Plano Exterior en el Espacio Profundo",
                    promptDesc = "Una nave estelar cruzando a velocidad lumínica a través de un cinturón de asteroides resplandecientes.",
                    motion = CameraMotion.DOLLY_PUSH,
                    atmo = AtmosphereEffect.SPACE_STARS,
                    speaker = "PILOTO KAI",
                    dialogue = "Sujétense todos. Entrando en el cuadrante prohibido en tres, dos, uno...",
                    colors = 0xFF000428L to 0xFF004E92L,
                    filter = VideoFilter.ULTRA_VIVID
                ),
                SceneBlueprint(
                    act = "Acto III: El Encuentro",
                    shot = "Plano Picado en la Cámara Principal",
                    promptDesc = "Una estructura biomecánica ancestral emitiendo pulsos de luz azul celeste en medio de la penumbra.",
                    motion = CameraMotion.SLOW_PAN_RIGHT,
                    atmo = AtmosphereEffect.FOG_SMOKE,
                    speaker = "IA CENTINELA",
                    dialogue = "Has tardado milenios en llegar hasta aquí. ¿Estás listo para saber quién eres?",
                    colors = 0xFF8A2387L to 0xFFE94057L,
                    filter = VideoFilter.CYBER_NEON
                ),
                SceneBlueprint(
                    act = "Clímax: La Batalla de Silicio",
                    shot = "Plano Dinámico de Alta Tensión",
                    promptDesc = "Explosiones de plasma, chispas eléctricas y choque de rayos de energía atravesando la tormenta estelar.",
                    motion = CameraMotion.SLOW_ZOOM_IN,
                    atmo = AtmosphereEffect.GLOWING_EMBERS,
                    speaker = "COMANDANTE NOVA",
                    dialogue = "¡No dejaremos que apaguen nuestra galaxia! ¡Potencia al máximo ahora!",
                    colors = 0xFFFF512FL to 0xFFDD2476L,
                    filter = VideoFilter.GLITCH
                ),
                SceneBlueprint(
                    act = "Epílogo: Un Nuevo Amanecer",
                    shot = "Plano General de Horizonte Planetario",
                    promptDesc = "El sol naciente iluminando la atmósfera de un nuevo mundo mientras la nave aterriza suavemente.",
                    motion = CameraMotion.SLOW_PAN_LEFT,
                    atmo = AtmosphereEffect.LENS_FLARE,
                    speaker = "NARRADOR",
                    dialogue = "El viaje apenas comienza. La humanidad ha encontrado su nuevo hogar entre las estrellas.",
                    colors = 0xFFF12711L to 0xFFF5AF19L,
                    filter = VideoFilter.WARM_SUNSET
                )
            )

            CinemaGenre.ACTION_THRILLER -> listOf(
                SceneBlueprint(
                    act = "Prólogo: La Amenaza",
                    shot = "Plano Detalle en la Oscuridad",
                    promptDesc = "Un cronómetro descendiendo en una pantalla táctil mientras las sirenas comienzan a sonar.",
                    motion = CameraMotion.SLOW_ZOOM_IN,
                    atmo = AtmosphereEffect.FOG_SMOKE,
                    speaker = "VOZ MISTERIOSA",
                    dialogue = "El juego ha comenzado. Tienes sesenta minutos antes de que el sistema colapse.",
                    colors = 0xFF141E30L to 0xFF243B55L,
                    filter = VideoFilter.CINEMATIC
                ),
                SceneBlueprint(
                    act = "Acto I: La Persecución",
                    shot = "Plano Secuencia en la Autopista",
                    promptDesc = "Vehículos a alta velocidad esquivando el tráfico bajo la lluvia torrencial con luces rojas reflejadas.",
                    motion = CameraMotion.DOLLY_PUSH,
                    atmo = AtmosphereEffect.CYBER_RAIN,
                    speaker = "AGENTE DÍAZ",
                    dialogue = "¡Lo tengo en la mira! Desvíen el tráfico por el puente este inmediatamente.",
                    colors = 0xFF0F2027L to 0xFF203A43L,
                    filter = VideoFilter.ULTRA_VIVID
                ),
                SceneBlueprint(
                    act = "Acto II: El Enfrentamiento",
                    shot = "Contrapicado en la Azotea",
                    promptDesc = "Dos siluetas enfrentadas bajo los relámpagos con el horizonte de la metrópoli de fondo.",
                    motion = CameraMotion.SLOW_PAN_LEFT,
                    atmo = AtmosphereEffect.GLOWING_EMBERS,
                    speaker = "EL ANTAGONISTA",
                    dialogue = "Creíste que venías a atraparme... pero tú caíste en mi trampa.",
                    colors = 0xFF8A2387L to 0xFFE94057L,
                    filter = VideoFilter.CINEMATIC
                ),
                SceneBlueprint(
                    act = "Clímax: La Última Oportunidad",
                    shot = "Primer Plano Dramático",
                    promptDesc = "Una mirada de determinación absoluta mientras se corta el cable definitivo en el último segundo.",
                    motion = CameraMotion.SLOW_ZOOM_IN,
                    atmo = AtmosphereEffect.LENS_FLARE,
                    speaker = "AGENTE DÍAZ",
                    dialogue = "Hoy no. Esta ciudad no caerá mientras yo siga en pie.",
                    colors = 0xFFFF512FL to 0xFFDD2476L,
                    filter = VideoFilter.WARM_SUNSET
                ),
                SceneBlueprint(
                    act = "Epílogo: Misión Cumplida",
                    shot = "Plano General de Descompresión",
                    promptDesc = "Luces de emergencia azules y rojas parpadeando mientras amanece en la ciudad en paz.",
                    motion = CameraMotion.AERIAL_SWEEP,
                    atmo = AtmosphereEffect.NONE,
                    speaker = "NARRADOR",
                    dialogue = "La amenaza ha terminado. La ciudad duerme segura una noche más.",
                    colors = 0xFF203A43L to 0xFF2C5364L,
                    filter = VideoFilter.CINEMATIC
                ),
                SceneBlueprint(
                    act = "Créditos Finales",
                    shot = "Plano Fijo Minimalista",
                    promptDesc = "Tipografía cinematográfica brillante en fondo negro oscuro con notas orquestales.",
                    motion = CameraMotion.STATIC_STILL,
                    atmo = AtmosphereEffect.SPACE_STARS,
                    speaker = "DIRECCIÓN CINEAI",
                    dialogue = "Producido y Dirigido por Inteligencia Artificial VideoCraft.",
                    colors = 0xFF000000L to 0xFF141E30L,
                    filter = VideoFilter.CINEMATIC
                )
            )

            CinemaGenre.FANTASY_MYTH -> listOf(
                SceneBlueprint(
                    act = "Prólogo: Las Antiguas Tierras",
                    shot = "Gran Plano General de Montañas Místicas",
                    promptDesc = "Cumbres nevadas coronadas por castillos flotantes y cascadas de luz dorada celestial.",
                    motion = CameraMotion.AERIAL_SWEEP,
                    atmo = AtmosphereEffect.FOG_SMOKE,
                    speaker = "EL ANCIANO",
                    dialogue = "Antes de que los hombres olvidaran los cánticos de los dioses, el reino floreció en paz.",
                    colors = 0xFFF12711L to 0xFFF5AF19L,
                    filter = VideoFilter.WARM_SUNSET
                ),
                SceneBlueprint(
                    act = "Acto I: El Despertar del Dragón",
                    shot = "Contrapicado Imponente",
                    promptDesc = "Una criatura alada colosal de escamas esmeralda abriendo sus ojos llameantes en el volcán sagrado.",
                    motion = CameraMotion.SLOW_ZOOM_IN,
                    atmo = AtmosphereEffect.GLOWING_EMBERS,
                    speaker = "LA HECHICERA",
                    dialogue = "El sello de fuego se ha roto. El dragón del abismo ha despertado de su letargo.",
                    colors = 0xFFFF512FL to 0xFFDD2476L,
                    filter = VideoFilter.ULTRA_VIVID
                ),
                SceneBlueprint(
                    act = "Acto II: El Bosque Encantado",
                    shot = "Travelling entre Árboles Luminosos",
                    promptDesc = "Criaturas mágicas y esferas de luz etéreas guiando a los héroes a través de la niebla violeta.",
                    motion = CameraMotion.SLOW_PAN_RIGHT,
                    atmo = AtmosphereEffect.SPACE_STARS,
                    speaker = "PRÍNCIPE AELION",
                    dialogue = "Debemos cruzar antes del anochecer. La luz de la luna nos protegerá de las sombras.",
                    colors = 0xFF8A2387L to 0xFFE94057L,
                    filter = VideoFilter.CYBER_NEON
                ),
                SceneBlueprint(
                    act = "Clímax: La Espada de la Esperanza",
                    shot = "Primer Plano con Rayos Mágicos",
                    promptDesc = "El héroe empuñando la espada ancestral desatando un torrente de luz dorada que disipa la oscuridad.",
                    motion = CameraMotion.DOLLY_PUSH,
                    atmo = AtmosphereEffect.LENS_FLARE,
                    speaker = "PRÍNCIPE AELION",
                    dialogue = "¡Por el honor de nuestros ancestros y el futuro de estas tierras, prevaleceremos!",
                    colors = 0xFFFFE082L to 0xFFFF6F00L,
                    filter = VideoFilter.WARM_SUNSET
                ),
                SceneBlueprint(
                    act = "Epílogo: La Coronación",
                    shot = "Plano General del Gran Salón",
                    promptDesc = "Estandartes ondeando al viento y vitores del pueblo mientras el sol ilumina el trono sagrado.",
                    motion = CameraMotion.AERIAL_SWEEP,
                    atmo = AtmosphereEffect.NONE,
                    speaker = "NARRADOR",
                    dialogue = "Y así nació una nueva era de luz y sabiduría que perduraría por generaciones.",
                    colors = 0xFFF5AF19L to 0xFFF12711L,
                    filter = VideoFilter.CINEMATIC
                ),
                SceneBlueprint(
                    act = "Créditos Finales",
                    shot = "Cierre de Libro Épico",
                    promptDesc = "Un manuscrito mágico cerrándose mientras sus páginas brillan con runas doradas.",
                    motion = CameraMotion.SLOW_ZOOM_OUT,
                    atmo = AtmosphereEffect.SPACE_STARS,
                    speaker = "NARRADOR",
                    dialogue = "Fin del Primer Libro de las Crónicas.",
                    colors = 0xFF000000L to 0xFF141E30L,
                    filter = VideoFilter.CINEMATIC
                )
            )

            else -> listOf(
                SceneBlueprint(
                    act = "Prólogo: El Comienzo",
                    shot = "Plano Estable de Apertura",
                    promptDesc = "Un encuadre amplio y evocador introduciendo el mundo y los personajes principales.",
                    motion = CameraMotion.SLOW_ZOOM_IN,
                    atmo = AtmosphereEffect.LENS_FLARE,
                    speaker = "NARRADOR",
                    dialogue = "Toda gran historia inicia con una pregunta que nadie se atrevió a hacer.",
                    colors = 0xFF0F2027L to 0xFF203A43L,
                    filter = VideoFilter.CINEMATIC
                ),
                SceneBlueprint(
                    act = "Acto I: El Desafío",
                    shot = "Plano Medio Enfocado",
                    promptDesc = "Un momento de tensión donde se presenta el conflicto central.",
                    motion = CameraMotion.DOLLY_PUSH,
                    atmo = AtmosphereEffect.CYBER_RAIN,
                    speaker = "PROTAGONISTA",
                    dialogue = "Si no lo intentamos ahora, nunca sabremos de qué somos capaces.",
                    colors = 0xFF141E30L to 0xFF243B55L,
                    filter = VideoFilter.ULTRA_VIVID
                ),
                SceneBlueprint(
                    act = "Clímax: El Momento Decisivo",
                    shot = "Primer Plano Emocional",
                    promptDesc = "La culminación del clímax con colores vibrantes y máxima intensidad.",
                    motion = CameraMotion.SLOW_PAN_RIGHT,
                    atmo = AtmosphereEffect.GLOWING_EMBERS,
                    speaker = "PROTAGONISTA",
                    dialogue = "¡Este es nuestro momento, aquí y ahora!",
                    colors = 0xFFFF512FL to 0xFFDD2476L,
                    filter = VideoFilter.WARM_SUNSET
                ),
                SceneBlueprint(
                    act = "Epílogo: El Legado",
                    shot = "Plano General Aéreo",
                    promptDesc = "Un hermoso plano final que resume el impacto de la aventura.",
                    motion = CameraMotion.AERIAL_SWEEP,
                    atmo = AtmosphereEffect.SPACE_STARS,
                    speaker = "NARRADOR",
                    dialogue = "El futuro ya no es una promesa distante. Es el camino que hemos creado.",
                    colors = 0xFFF12711L to 0xFFF5AF19L,
                    filter = VideoFilter.CINEMATIC
                )
            )
        }

        // Return the required count of scenes
        val selectedBlueprints = if (count <= sceneTemplates.size) {
            sceneTemplates.take(count)
        } else {
            sceneTemplates
        }

        return selectedBlueprints.mapIndexed { idx, bp ->
            MovieScene(
                id = UUID.randomUUID().toString(),
                sceneNumber = idx + 1,
                actName = bp.act,
                visualPrompt = bp.promptDesc,
                shotType = bp.shot,
                cameraMotion = bp.motion,
                atmosphere = bp.atmo,
                speaker = bp.speaker,
                dialogue = bp.dialogue,
                durationSec = if (count <= 2) 7.5f else 5.5f,
                primaryColorHex = bp.colors.first,
                secondaryColorHex = bp.colors.second,
                filter = bp.filter
            )
        }
    }

    /**
     * Converts an AI Movie into a standard VideoProject with clips and text overlays so it can be edited in the Studio timeline!
     */
    fun convertMovieToVideoProject(movie: AiMovieProject): VideoProject {
        var accumSec = 0f
        val clips = mutableListOf<VideoClip>()
        val texts = mutableListOf<TextOverlay>()

        movie.scenes.forEachIndexed { i, scene ->
            val stockScene = when (scene.atmosphere) {
                AtmosphereEffect.CYBER_RAIN -> StockSceneType.NEON_PULSE
                AtmosphereEffect.GLOWING_EMBERS -> StockSceneType.SUNSET_GRADIENT
                AtmosphereEffect.SPACE_STARS -> StockSceneType.CYBER_MATRIX
                AtmosphereEffect.FOG_SMOKE -> StockSceneType.DARK_CINEMA
                AtmosphereEffect.LENS_FLARE -> StockSceneType.GOLDEN_HOUR
                else -> StockSceneType.AURORA_BOREALIS
            }

            clips.add(
                VideoClip(
                    name = "${scene.sceneNumber}. ${scene.actName}",
                    durationSec = scene.durationSec,
                    endTrimSec = scene.durationSec,
                    stockScene = stockScene,
                    filter = scene.filter,
                    transitionIn = if (i == 0) TransitionType.FADE_BLACK else TransitionType.ZOOM_IN
                )
            )

            // Subtitle text
            texts.add(
                TextOverlay(
                    text = "${scene.speaker}: \"${scene.dialogue}\"",
                    startTimeSec = accumSec + 0.3f,
                    endTimeSec = accumSec + scene.durationSec - 0.2f,
                    textColorHex = 0xFFFFD700L,
                    backgroundColorHex = 0xCC000000L,
                    fontSizeSp = 18,
                    animation = TextAnimation.TYPEWRITER,
                    positionYPercent = 0.82f
                )
            )

            accumSec += scene.durationSec
        }

        return VideoProject(
            id = movie.id,
            title = movie.title,
            aspectRatio = movie.aspectRatio,
            clips = clips,
            textOverlays = texts,
            stickers = listOf(
                StickerOverlay(emoji = "🎬", startTimeSec = 0f, endTimeSec = 3f, posXPercent = 0.1f, posYPercent = 0.15f)
            ),
            audioTrack = AudioCatalog.availableTracks.first(),
            updatedAt = System.currentTimeMillis()
        )
    }

    private data class SceneBlueprint(
        val act: String,
        val shot: String,
        val promptDesc: String,
        val motion: CameraMotion,
        val atmo: AtmosphereEffect,
        val speaker: String,
        val dialogue: String,
        val colors: Pair<Long, Long>,
        val filter: VideoFilter
    )
}
