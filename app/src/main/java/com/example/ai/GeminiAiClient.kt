package com.example.ai

import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

object GeminiAiClient {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    private const val PRIMARY_MODEL = "gemini-2.5-flash"
    private const val FALLBACK_MODEL = "gemini-1.5-flash"

    /**
     * Tests whether the provided Gemini API key can connect and generate content.
     */
    suspend fun testConnection(apiKey: String): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("La clave API está vacía."))
        }

        try {
            val responseText = executeGeminiTextRequest(
                apiKey = apiKey,
                prompt = "Responde exactamente con la frase: 'CONECTADO_CON_EXITO' y una breve frase de 5 palabras sobre cine.",
                systemInstruction = "Eres un asistente de pruebas de API para el estudio cinematográfico VideoCraft AI."
            )
            Result.success(responseText.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Calls Gemini to enhance a director prompt.
     */
    suspend fun enhancePrompt(
        apiKey: String,
        simplePrompt: String,
        genre: CinemaGenre,
        style: VisualStyleEnum
    ): Result<String> = withContext(Dispatchers.IO) {
        val systemInstruction = "Eres un legendario director de fotografía y cineasta de Hollywood. Tu tarea es expandir una idea breve en un prompt cinematográfico profesional y evocador para generación visual con IA. Incluye detalles de óptica de lente (35mm/anamórfico), iluminación (volumétrica, hora dorada o neón), atmósfera, textura y composición dramática. Devuelve únicamente el texto expandido en español (máximo 4 renglones), sin explicaciones ni comillas."

        val prompt = "Idea base: \"$simplePrompt\". Género cinematográfico: ${genre.displayName}. Estilo visual deseado: ${style.label}."

        try {
            val enhanced = executeGeminiTextRequest(apiKey, prompt, systemInstruction)
            Result.success(enhanced.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates a full structured screenplay using Gemini API.
     */
    suspend fun generateFullScreenplay(
        apiKey: String,
        ideaPrompt: String,
        genre: CinemaGenre,
        style: VisualStyleEnum
    ): Result<FullScreenplay> = withContext(Dispatchers.IO) {
        val systemPrompt = """
            Eres un galardonado guionista y director cinematográfico en VideoCraft AI Film Studio.
            Debes generar un guion cinematográfico completo y profesional a partir de la idea del usuario.
            
            IMPORTANTE: Responde ÚNICAMENTE en formato JSON plano válido, sin rodeos, sin markdown envolvente (sin ```json ni ```).
            Estructura JSON requerida:
            {
              "title": "TÍTULO CINEMATOGRÁFICO EN MAYÚSCULAS",
              "logline": "Una o dos oraciones impactantes que resumen el conflicto central.",
              "synopsis": "Sinopsis dramática de 3 a 5 oraciones.",
              "protagonist": {
                "name": "Nombre",
                "role": "Protagonista",
                "apparentAge": "Edad aparente",
                "physicalDescription": "Descripción física detallada",
                "wardrobe": "Vestuario cinematográfico",
                "personality": "Personalidad y motivaciones",
                "voiceTone": "Tono de voz"
              },
              "antagonist": {
                "name": "Nombre o Entidad",
                "role": "Antagonista o Fuerza opositora",
                "apparentAge": "Edad",
                "physicalDescription": "Descripción visual",
                "wardrobe": "Indumentaria",
                "personality": "Rasgos",
                "voiceTone": "Tono vocal"
              },
              "scenes": [
                {
                  "sceneNumber": 1,
                  "sceneHeading": "EXT. o INT. LUGAR - DÍA/NOCHE/HORA DORADA",
                  "visualAction": "Descripción visual cinematográfica detallada de la acción.",
                  "speaker": "NOMBRE DEL PERSONAJE O NARRADOR",
                  "dialogue": "Línea de diálogo o pensamiento pronunciado.",
                  "shotType": "WIDE_SHOT o CLOSE_UP o MEDIUM_SHOT o OVERHEAD o LOW_ANGLE",
                  "cameraMove": "DOLLY_IN o DOLLY_OUT o TRAVELLING o PAN o ORBIT o SLOW_MOTION o STATIC",
                  "lighting": "NEON_CYBER o GOLDEN_HOUR o CHIAROSCURO o LUNAR_COLD o STUDIO_SOFT",
                  "durationSec": 6.5,
                  "soundEffects": "Indicaciones de sonido, música y foley."
                }
              ]
            }
            Genera entre 4 y 6 escenas con arcos dramáticos claros: Introducción, Conflicto, Confrontación y Desenlace.
        """.trimIndent()

        val userMessage = "Crea el guion cinematográfico para la idea: \"$ideaPrompt\". Género: ${genre.displayName}. Estilo visual: ${style.label}."

        try {
            val rawJson = executeGeminiTextRequest(apiKey, userMessage, systemPrompt)
            val parsedScreenplay = parseScreenplayJson(rawJson, genre, style)
            Result.success(parsedScreenplay)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates a movie project with coherent scenes from Gemini.
     */
    suspend fun generateAiMovie(
        apiKey: String,
        prompt: String,
        genre: CinemaGenre,
        sceneCount: Int = 4
    ): Result<AiMovieProject> = withContext(Dispatchers.IO) {
        val systemPrompt = """
            Eres un director de cine en VideoCraft AI. Genera una película cinematográfica con exactamente $sceneCount escenas a partir de la idea del usuario.
            Responde ÚNICAMENTE en JSON plano válido sin markdown:
            {
              "title": "TÍTULO CINEMATOGRÁFICO",
              "tagline": "Frase promocional épica de 1 línea",
              "synopsis": "Sinopsis de 2 a 3 oraciones",
              "scenes": [
                {
                  "sceneNumber": 1,
                  "actName": "Nombre del acto o escena",
                  "visualPrompt": "Descripción visual de la escena con iluminación y detalles",
                  "shotType": "Plano General / Primer Plano / etc.",
                  "cameraMotion": "SLOW_ZOOM_IN o AERIAL_SWEEP o DOLLY_PUSH o SLOW_PAN_RIGHT o SLOW_PAN_LEFT o STATIC_STILL",
                  "atmosphere": "CYBER_RAIN o GLOWING_EMBERS o SPACE_STARS o FOG_SMOKE o LENS_FLARE o NONE",
                  "speaker": "PERSONAJE O NARRADOR",
                  "dialogue": "Diálogo cinematográfico",
                  "durationSec": 6.0,
                  "sfxCue": "Efectos sonoros y atmósfera sonora"
                }
              ]
            }
        """.trimIndent()

        try {
            val rawJson = executeGeminiTextRequest(apiKey, "Película: \"$prompt\". Género: ${genre.displayName}.", systemPrompt)
            val movie = parseMovieJson(rawJson, prompt, genre)
            Result.success(movie)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun executeGeminiTextRequest(
        apiKey: String,
        prompt: String,
        systemInstruction: String? = null
    ): String {
        val modelsToTry = listOf(PRIMARY_MODEL, FALLBACK_MODEL)
        var lastException: Exception? = null

        for (model in modelsToTry) {
            try {
                val url = "$BASE_URL/$model:generateContent?key=$apiKey"

                val rootObj = JSONObject()
                val contentsArr = JSONArray()
                val userContent = JSONObject()
                userContent.put("role", "user")

                val partsArr = JSONArray()
                val textPart = JSONObject()
                textPart.put("text", prompt)
                partsArr.put(textPart)
                userContent.put("parts", partsArr)
                contentsArr.put(userContent)
                rootObj.put("contents", contentsArr)

                if (!systemInstruction.isNullOrBlank()) {
                    val sysObj = JSONObject()
                    val sysParts = JSONArray()
                    sysParts.put(JSONObject().put("text", systemInstruction))
                    sysObj.put("parts", sysParts)
                    rootObj.put("systemInstruction", sysObj)
                }

                // Config params
                val genConfig = JSONObject()
                genConfig.put("temperature", 0.7)
                genConfig.put("maxOutputTokens", 2048)
                rootObj.put("generationConfig", genConfig)

                val body = rootObj.toString().toRequestBody(jsonMediaType)
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    val respBody = response.body?.string() ?: ""
                    if (!response.isSuccessful) {
                        val errorDetail = try {
                            JSONObject(respBody).optJSONObject("error")?.optString("message") ?: respBody
                        } catch (_: Exception) {
                            respBody
                        }
                        throw IllegalStateException("HTTP ${response.code}: $errorDetail")
                    }

                    val respJson = JSONObject(respBody)
                    val candidates = respJson.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return parts.getJSONObject(0).optString("text")
                        }
                    }
                    throw IllegalStateException("Respuesta vacía recibida de Gemini.")
                }
            } catch (e: Exception) {
                lastException = e
            }
        }

        throw lastException ?: IllegalStateException("No se pudo conectar con Gemini.")
    }

    private fun cleanJsonMarkdown(raw: String): String {
        var clean = raw.trim()
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json")
        } else if (clean.startsWith("```")) {
            clean = clean.removePrefix("```")
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```")
        }
        return clean.trim()
    }

    private fun parseScreenplayJson(raw: String, genre: CinemaGenre, style: VisualStyleEnum): FullScreenplay {
        val clean = cleanJsonMarkdown(raw)
        val json = JSONObject(clean)

        val title = json.optString("title", "PRODUCCIÓN CINEMATOGRÁFICA").ifBlank { "PRODUCCIÓN CINEMATOGRÁFICA" }
        val logline = json.optString("logline", "Una historia cinematográfica.")
        val synopsis = json.optString("synopsis", "Sinopsis generada por IA.")

        val protObj = json.optJSONObject("protagonist")
        val protagonist = if (protObj != null) {
            CharacterProfile(
                name = protObj.optString("name", "Protagonista"),
                role = protObj.optString("role", "Protagonista"),
                apparentAge = protObj.optString("apparentAge", "30 años"),
                physicalDescription = protObj.optString("physicalDescription", "Aspecto cinematográfico."),
                wardrobe = protObj.optString("wardrobe", "Vestuario contemporáneo."),
                personality = protObj.optString("personality", "Valiente y decidido."),
                voiceTone = protObj.optString("voiceTone", "Firme"),
                avatarColorHex = 0xFF7F5AF0L
            )
        } else {
            CharacterProfile(
                name = "Elena / Protagonista",
                physicalDescription = "Presencia firme y mirada atenta.",
                wardrobe = "Traje táctico.",
                personality = "Resuelta y analítica."
            )
        }

        val antObj = json.optJSONObject("antagonist")
        val antagonist = if (antObj != null) {
            CharacterProfile(
                name = antObj.optString("name", "Antagonista"),
                role = antObj.optString("role", "Antagonista"),
                apparentAge = antObj.optString("apparentAge", "Indefinida"),
                physicalDescription = antObj.optString("physicalDescription", "Figura imponente y misteriosa."),
                wardrobe = antObj.optString("wardrobe", "Blindaje oscuro."),
                personality = antObj.optString("personality", "Calculador e implacable."),
                voiceTone = antObj.optString("voiceTone", "Grave"),
                avatarColorHex = 0xFFFF3B5CL
            )
        } else null

        val scenesList = mutableListOf<ScreenplaySceneData>()
        val scenesArr = json.optJSONArray("scenes")
        if (scenesArr != null) {
            for (i in 0 until scenesArr.length()) {
                val scObj = scenesArr.getJSONObject(i)
                val sceneNum = scObj.optInt("sceneNumber", i + 1)
                val heading = scObj.optString("sceneHeading", "EXT. ESCENA $sceneNum - DÍA")
                val visual = scObj.optString("visualAction", "Acción cinematográfica en desarrollo.")
                val speaker = scObj.optString("speaker", "PERSONAJE")
                val dialogue = scObj.optString("dialogue", "...")
                val duration = scObj.optDouble("durationSec", 6.0).toFloat()
                val sfx = scObj.optString("soundEffects", "Atmósfera cinematográfica")

                val shot = parseShotType(scObj.optString("shotType"))
                val camMove = parseCameraMove(scObj.optString("cameraMove"))
                val light = parseLighting(scObj.optString("lighting"))

                scenesList.add(
                    ScreenplaySceneData(
                        sceneNumber = sceneNum,
                        sceneHeading = heading,
                        visualAction = visual,
                        characterDialogues = listOf(speaker to dialogue),
                        shotSuggestion = shot,
                        cameraMoveSuggestion = camMove,
                        lightingSuggestion = light,
                        durationSec = duration,
                        soundEffects = sfx
                    )
                )
            }
        }

        if (scenesList.isEmpty()) {
            throw IllegalStateException("El guion generado por Gemini no contenía escenas válidas.")
        }

        return FullScreenplay(
            title = title,
            logline = logline,
            synopsis = synopsis,
            genre = genre,
            visualStyle = style,
            protagonist = protagonist,
            antagonist = antagonist,
            scenes = scenesList
        )
    }

    private fun parseMovieJson(raw: String, originalPrompt: String, genre: CinemaGenre): AiMovieProject {
        val clean = cleanJsonMarkdown(raw)
        val json = JSONObject(clean)

        val title = json.optString("title", "PELÍCULA CINEMATOGRÁFICA").ifBlank { "PELÍCULA CINEMATOGRÁFICA" }
        val tagline = json.optString("tagline", "Una obra producida con Inteligencia Artificial.")
        val synopsis = json.optString("synopsis", "Producción cinematográfica generada.")

        val scenes = mutableListOf<MovieScene>()
        val scenesArr = json.optJSONArray("scenes")
        if (scenesArr != null) {
            for (i in 0 until scenesArr.length()) {
                val scObj = scenesArr.getJSONObject(i)
                val num = scObj.optInt("sceneNumber", i + 1)
                val act = scObj.optString("actName", "Acto $num")
                val visualPrompt = scObj.optString("visualPrompt", originalPrompt)
                val shot = scObj.optString("shotType", "Plano Medio")
                val motionStr = scObj.optString("cameraMotion", "SLOW_ZOOM_IN")
                val atmoStr = scObj.optString("atmosphere", "CYBER_RAIN")
                val speaker = scObj.optString("speaker", "NARRADOR")
                val dialogue = scObj.optString("dialogue", "...")
                val dur = scObj.optDouble("durationSec", 6.0).toFloat()
                val sfx = scObj.optString("sfxCue", "Música cinematográfica")

                val motion = try {
                    CameraMotion.valueOf(motionStr)
                } catch (_: Exception) {
                    CameraMotion.SLOW_ZOOM_IN
                }

                val atmo = try {
                    AtmosphereEffect.valueOf(atmoStr)
                } catch (_: Exception) {
                    AtmosphereEffect.CYBER_RAIN
                }

                val (col1, col2) = when (atmo) {
                    AtmosphereEffect.CYBER_RAIN -> 0xFF8A2387L to 0xFF00E5FFL
                    AtmosphereEffect.GLOWING_EMBERS -> 0xFFFF512FL to 0xFFDD2476L
                    AtmosphereEffect.SPACE_STARS -> 0xFF000428L to 0xFF004E92L
                    AtmosphereEffect.FOG_SMOKE -> 0xFF0F2027L to 0xFF203A43L
                    AtmosphereEffect.LENS_FLARE -> 0xFFF12711L to 0xFFF5AF19L
                    else -> 0xFF141E30L to 0xFF243B55L
                }

                scenes.add(
                    MovieScene(
                        id = UUID.randomUUID().toString(),
                        sceneNumber = num,
                        actName = act,
                        visualPrompt = visualPrompt,
                        shotType = shot,
                        cameraMotion = motion,
                        atmosphere = atmo,
                        speaker = speaker,
                        dialogue = dialogue,
                        durationSec = dur,
                        primaryColorHex = col1,
                        secondaryColorHex = col2,
                        filter = VideoFilter.CINEMATIC,
                        sfxCue = sfx
                    )
                )
            }
        }

        return AiMovieProject(
            id = UUID.randomUUID().toString(),
            title = title,
            tagline = tagline,
            synopsis = synopsis,
            originalPrompt = originalPrompt,
            genre = genre,
            productionType = MovieProductionType.FULL_MOVIE,
            aspectRatio = AspectRatioType.RATIO_16_9,
            director = "Gemini AI Director",
            scenes = scenes
        )
    }

    private fun parseShotType(type: String): ShotTypeEnum {
        return when (type.uppercase()) {
            "CLOSE_UP" -> ShotTypeEnum.CLOSE_UP
            "MEDIUM_SHOT" -> ShotTypeEnum.MEDIUM_SHOT
            "WIDE_SHOT" -> ShotTypeEnum.WIDE_SHOT
            "OVERHEAD" -> ShotTypeEnum.OVERHEAD
            "LOW_ANGLE" -> ShotTypeEnum.LOW_ANGLE
            else -> ShotTypeEnum.WIDE_SHOT
        }
    }

    private fun parseCameraMove(move: String): CameraMoveEnum {
        return when (move.uppercase()) {
            "DOLLY_IN" -> CameraMoveEnum.DOLLY_IN
            "DOLLY_OUT" -> CameraMoveEnum.DOLLY_OUT
            "TRAVELLING" -> CameraMoveEnum.TRAVELLING
            "PAN" -> CameraMoveEnum.PAN
            "ORBIT" -> CameraMoveEnum.ORBIT
            "SLOW_MOTION" -> CameraMoveEnum.SLOW_MOTION
            else -> CameraMoveEnum.DOLLY_IN
        }
    }

    private fun parseLighting(light: String): LightingStyleEnum {
        return when (light.uppercase()) {
            "NEON_CYBER" -> LightingStyleEnum.NEON_CYBER
            "GOLDEN_HOUR" -> LightingStyleEnum.GOLDEN_HOUR
            "CHIAROSCURO" -> LightingStyleEnum.CHIAROSCURO
            "LUNAR_COLD" -> LightingStyleEnum.LUNAR_COLD
            else -> LightingStyleEnum.STUDIO_SOFT
        }
    }
}
