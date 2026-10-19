package com.example.ai

import com.example.model.*
import java.util.UUID

object CineAiScreenplayEngine {

    /**
     * Enhances a simple prompt into an industry-grade director prompt with lighting, camera optics and composition
     */
    fun enhancePromptWithAi(simplePrompt: String, genre: CinemaGenre, style: VisualStyleEnum): String {
        val trimmed = simplePrompt.trim().ifEmpty { "Una escena cinematográfica de alto impacto" }
        val optics = when (style) {
            VisualStyleEnum.ANAMORPHIC_35MM -> "shot on 35mm anamorphic lens, subtle blue horizontal flares, shallow depth of field, f/1.4, cinematic natural grain"
            VisualStyleEnum.IMAX_70MM -> "shot on IMAX 70mm, 8k resolution, razor sharp focus, expansive vista, ultra-realistic textures, master lighting"
            VisualStyleEnum.CYBERPUNK_NEON -> "cyberpunk aesthetic, volumetric neon fog, reflective wet asphalt, cyan and magenta color grade, chromatic aberration"
            VisualStyleEnum.ANIME_MASTERPIECE -> "handcrafted anime style, Makoto Shinkai lighting, vibrant color palette, emotive sky with cloud formations"
            VisualStyleEnum.DARK_FANTASY -> "dark fantasy grim atmosphere, torchlight reflections, rolling mist, moody desaturated tones with deep blacks"
            VisualStyleEnum.FILM_NOIR -> "classic film noir, dramatic chiaroscuro high contrast lighting, venetian blind shadows, silver gelatin look"
            VisualStyleEnum.BBC_DOCUMENTARY -> "hyper-realistic nature documentary, wildlife cinematography, golden hour backlight, 4k ultra slow motion"
        }

        return "$trimmed, $optics, master composition, photorealistic cinematic lighting, 24fps film look, award winning cinematography"
    }

    /**
     * Generates a complete structured screenplay from a prompt idea
     */
    fun generateFullScreenplay(
        ideaPrompt: String,
        genre: CinemaGenre = CinemaGenre.SCI_FI,
        style: VisualStyleEnum = VisualStyleEnum.ANAMORPHIC_35MM
    ): FullScreenplay {
        val prompt = ideaPrompt.trim().ifEmpty {
            "Una exploradora descubre una ciudad futurista escondida entre montañas donde una civilización ancestral ha permanecido oculta."
        }

        val isMountainExplorer = prompt.contains("explorador", ignoreCase = true) ||
                prompt.contains("ciudad", ignoreCase = true) ||
                prompt.contains("montaña", ignoreCase = true)

        val title = if (isMountainExplorer) {
            "AURORA: LA CIUDAD DE LAS CUMBRES SILENCIOSAS"
        } else {
            "ECOS DE LA FRONTERA PERDIDA"
        }

        val logline = if (isMountainExplorer) {
            "Guiada por un artefacto que desafía la física, una cartógrafa solitaria descubre una metrópoli biomecánica oculta en la cordillera, despertando a sus milenarios guardianes."
        } else {
            "En un mundo donde la memoria es la moneda de cambio, un rebelde debe descifrar la última señal que salvará a su pueblo del olvido eterno."
        }

        val synopsis = "Tras semanas de travesía en terreno extremo, la protagonista atraviesa un paso de montaña considerado intransitable. Al disiparse la tormenta de nieve, se revela ante ella una civilización tecnológica intacta, impulsada por energía geotérmica azul. Pero el contacto no pasa desapercibido: el sistema de defensa central se activa y la exploradora debe demostrar que su misión es de paz antes de que sea demasiado tarde."

        val protagonist = CharacterProfile(
            name = if (isMountainExplorer) "Elena Cruz" else "Kael Vance",
            role = "Protagonista",
            apparentAge = "29 años",
            physicalDescription = "Complexión ágil y atlética, mirada analítica, marcas de intemperie en el rostro y cabello recogido práctico.",
            wardrobe = "Traje térmico de expedición modular con sensores bio-climáticos y arnés de escalada reforzado.",
            personality = "Intrépida, perspicaz, con profundo respeto por las civilizaciones antiguas y gran temple bajo presión.",
            voiceTone = "Firme, calmada y observadora",
            avatarColorHex = 0xFF00E5FFL,
            scenesCount = 4
        )

        val antagonist = CharacterProfile(
            name = "Guardián Aethelgard",
            role = "Antagonista / Protector",
            apparentAge = "Atemporal (Entidad Sintética)",
            physicalDescription = "Figura bípeda imponente de aleación de titanio negro con líneas de luz azul geotérmica pulsante.",
            wardrobe = "Blindaje ceremonial grabado con jeroglíficos energéticos milenarios.",
            personality = "Inflexible protector del santuario, programado para evitar cualquier intrusión del mundo exterior.",
            voiceTone = "Grave, resonante y sintetizada",
            avatarColorHex = 0xFFFF3B5CL,
            scenesCount = 2
        )

        val scenes = listOf(
            ScreenplaySceneData(
                sceneNumber = 1,
                sceneHeading = "EXT. PASO DE MONTAÑA CONGELADO - DÍA / HORA DORADA",
                visualAction = "El viento aúlla suavemente disipando la niebla helada. Elena avanza con piolets sobre una cornisa de roca negra. Su respiración se condensa en el aire. De pronto, se detiene y levanta la mirada.",
                characterDialogues = listOf(
                    "ELENA" to "Las coordenadas eran exactas... el mapa de mi abuelo no mentía."
                ),
                shotSuggestion = ShotTypeEnum.WIDE_SHOT,
                cameraMoveSuggestion = CameraMoveEnum.TRAVELLING,
                lightingSuggestion = LightingStyleEnum.GOLDEN_HOUR,
                durationSec = 6.0f,
                soundEffects = "Viento helado sibilante, crujido de botas sobre nieve virgen"
            ),
            ScreenplaySceneData(
                sceneNumber = 2,
                sceneHeading = "EXT. MIRADOR DEL VALLE OCULTO - CONTINUO",
                visualAction = "La cámara asciende vertiginosamente sobre la cresta rocosa revelando un abismo colosal: en el fondo del valle verde y protegido, una metrópoli de torres blancas y filamentos de luz azul se extiende hasta el horizonte.",
                characterDialogues = listOf(
                    "ELENA" to "Dios mío... Una civilización entera oculta del mundo durante siglos."
                ),
                shotSuggestion = ShotTypeEnum.OVERHEAD,
                cameraMoveSuggestion = CameraMoveEnum.DOLLY_IN,
                lightingSuggestion = LightingStyleEnum.NEON_CYBER,
                durationSec = 7.0f,
                soundEffects = "Murmullo de energía armónica distante, estruendo sutil de cascadas"
            ),
            ScreenplaySceneData(
                sceneNumber = 3,
                sceneHeading = "INT. PUERTA DEL TEMPLO GEOTÉRMICO - ATARDECER",
                visualAction = "Elena apoya su mano con guante sobre un pilar tallado. Un pulso de luz cyan recorre las venas del suelo. Las compuertas hexagonales se abren con vapor de presión.",
                characterDialogues = listOf(
                    "ELENA" to "No vengo como conquistadora. Solo busco aprender."
                ),
                shotSuggestion = ShotTypeEnum.CLOSE_UP,
                cameraMoveSuggestion = CameraMoveEnum.DOLLY_IN,
                lightingSuggestion = LightingStyleEnum.CHIAROSCURO,
                durationSec = 5.5f,
                soundEffects = "Descarga neumática suave, zumbido resonante de frecuencias bajas"
            ),
            ScreenplaySceneData(
                sceneNumber = 4,
                sceneHeading = "INT. GRAN CÁMARA DE LOS ANCESTROS - CONTINUO",
                visualAction = "Desde la penumbra, los ojos del Guardián Aethelgard se iluminan en rojo de alerta. Su espada de plasma despierta con un destello cegador.",
                characterDialogues = listOf(
                    "AETHELGARD" to "Intrusa de la superficie. Tu especie prometió jamás regresar.",
                    "ELENA" to "Nuestra especie lo olvidó todo. Pero yo he venido a recordarlo."
                ),
                shotSuggestion = ShotTypeEnum.LOW_ANGLE,
                cameraMoveSuggestion = CameraMoveEnum.ORBIT,
                lightingSuggestion = LightingStyleEnum.NEON_CYBER,
                durationSec = 7.5f,
                soundEffects = "Ignición de filo de plasma, latido dramático de tambores de tensión"
            ),
            ScreenplaySceneData(
                sceneNumber = 5,
                sceneHeading = "EXT. CIUDAD DE LAS CUMBRES - CREPÚSCULO",
                visualAction = "Elena sostiene en alto el medallón de su familia. El símbolo concuerda exactamente con el escudo en el pecho del guardián. La luz del coloso cambia lentamente de rojo a celeste de tregua.",
                characterDialogues = listOf(
                    "AETHELGARD" to "Portas la marca del Primer Arquitecto... Sé bienvenida a tu verdadero hogar.",
                    "ELENA" to "Es hora de que nuestros dos mundos vuelvan a hablar."
                ),
                shotSuggestion = ShotTypeEnum.WIDE_SHOT,
                cameraMoveSuggestion = CameraMoveEnum.DOLLY_OUT,
                lightingSuggestion = LightingStyleEnum.GOLDEN_HOUR,
                durationSec = 8.0f,
                soundEffects = "Acorde orquestal triunfal, campanas cristalinas al viento"
            )
        )

        return FullScreenplay(
            title = title,
            logline = logline,
            synopsis = synopsis,
            genre = genre,
            visualStyle = style,
            protagonist = protagonist,
            antagonist = antagonist,
            scenes = scenes
        )
    }

    /**
     * Converts a Screenplay into an AiMovieProject ready for Cinema playback and timeline editing
     */
    fun convertScreenplayToMovieProject(screenplay: FullScreenplay): AiMovieProject {
        val scenes = screenplay.scenes.map { sc ->
            val speaker = sc.characterDialogues.firstOrNull()?.first ?: "NARRADOR"
            val dialogue = sc.characterDialogues.firstOrNull()?.second ?: sc.visualAction

            val (col1, col2) = when (sc.lightingSuggestion) {
                LightingStyleEnum.NEON_CYBER -> 0xFF8A2387L to 0xFF00E5FFL
                LightingStyleEnum.GOLDEN_HOUR -> 0xFFF12711L to 0xFFF5AF19L
                LightingStyleEnum.CHIAROSCURO -> 0xFF0F2027L to 0xFF203A43L
                LightingStyleEnum.LUNAR_COLD -> 0xFF000428L to 0xFF004E92L
                LightingStyleEnum.STUDIO_SOFT -> 0xFF2C3E50L to 0xFF4CA1AFL
            }

            val motion = when (sc.cameraMoveSuggestion) {
                CameraMoveEnum.DOLLY_IN -> CameraMotion.DOLLY_PUSH
                CameraMoveEnum.DOLLY_OUT -> CameraMotion.SLOW_ZOOM_OUT
                CameraMoveEnum.TRAVELLING -> CameraMotion.SLOW_PAN_RIGHT
                CameraMoveEnum.PAN -> CameraMotion.SLOW_PAN_LEFT
                CameraMoveEnum.ORBIT -> CameraMotion.AERIAL_SWEEP
                CameraMoveEnum.SLOW_MOTION -> CameraMotion.SLOW_ZOOM_IN
                CameraMoveEnum.STATIC -> CameraMotion.STATIC_STILL
            }

            val atmosphere = when (sc.lightingSuggestion) {
                LightingStyleEnum.NEON_CYBER -> AtmosphereEffect.CYBER_RAIN
                LightingStyleEnum.GOLDEN_HOUR -> AtmosphereEffect.LENS_FLARE
                LightingStyleEnum.CHIAROSCURO -> AtmosphereEffect.FOG_SMOKE
                LightingStyleEnum.LUNAR_COLD -> AtmosphereEffect.SPACE_STARS
                LightingStyleEnum.STUDIO_SOFT -> AtmosphereEffect.NONE
            }

            MovieScene(
                id = UUID.randomUUID().toString(),
                sceneNumber = sc.sceneNumber,
                actName = sc.sceneHeading,
                visualPrompt = sc.visualAction,
                shotType = "${sc.shotSuggestion.label} • ${sc.cameraMoveSuggestion.label}",
                cameraMotion = motion,
                atmosphere = atmosphere,
                speaker = speaker,
                dialogue = dialogue,
                durationSec = sc.durationSec,
                primaryColorHex = col1,
                secondaryColorHex = col2,
                filter = when (screenplay.visualStyle) {
                    VisualStyleEnum.CYBERPUNK_NEON -> VideoFilter.CYBER_NEON
                    VisualStyleEnum.FILM_NOIR -> VideoFilter.FILM_NOIR
                    VisualStyleEnum.DARK_FANTASY -> VideoFilter.CINEMATIC
                    else -> VideoFilter.WARM_SUNSET
                },
                sfxCue = sc.soundEffects
            )
        }

        return AiMovieProject(
            id = UUID.randomUUID().toString(),
            title = screenplay.title,
            tagline = screenplay.logline,
            synopsis = screenplay.synopsis,
            originalPrompt = screenplay.logline,
            genre = screenplay.genre,
            productionType = MovieProductionType.FULL_MOVIE,
            aspectRatio = AspectRatioType.RATIO_16_9,
            director = "CineAI Studio Pro",
            scenes = scenes
        )
    }
}
