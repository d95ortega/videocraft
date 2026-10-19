package com.example.data

import com.example.model.*

object VideoTemplatesData {
    val templates = listOf(
        VideoTemplate(
            id = "template_viral_hook",
            title = "TikTok & Reels Viral Hook",
            description = "Estructura diseñada para retener a la audiencia en los primeros 3 segundos con transiciones rápidas.",
            category = "Redes Sociales",
            aspectRatio = AspectRatioType.RATIO_9_16,
            defaultMusic = "Electro Hook 128 BPM",
            initialClips = listOf(
                VideoClip(
                    name = "Gancho Impactante",
                    durationSec = 2.0f,
                    endTrimSec = 2.0f,
                    stockScene = StockSceneType.NEON_PULSE,
                    filter = VideoFilter.ULTRA_VIVID,
                    speed = 1.0f
                ),
                VideoClip(
                    name = "Demostración",
                    durationSec = 3.5f,
                    endTrimSec = 3.5f,
                    stockScene = StockSceneType.CYBER_MATRIX,
                    filter = VideoFilter.CYBER_NEON,
                    transitionIn = TransitionType.ZOOM_IN
                ),
                VideoClip(
                    name = "Llamado a la Acción",
                    durationSec = 2.5f,
                    endTrimSec = 2.5f,
                    stockScene = StockSceneType.SUNSET_GRADIENT,
                    filter = VideoFilter.WARM_SUNSET,
                    transitionIn = TransitionType.FLASH_WHITE
                )
            ),
            initialTexts = listOf(
                TextOverlay(
                    text = "😱 ¡NO HAGAS ESTO!",
                    startTimeSec = 0f,
                    endTimeSec = 2.0f,
                    textColorHex = 0xFFFFD700L,
                    fontSizeSp = 24,
                    animation = TextAnimation.BOUNCE,
                    positionYPercent = 0.25f
                ),
                TextOverlay(
                    text = "Prueba este truco en su lugar 👇",
                    startTimeSec = 2.1f,
                    endTimeSec = 5.5f,
                    textColorHex = 0xFFFFFFFFL,
                    fontSizeSp = 20,
                    animation = TextAnimation.TYPEWRITER,
                    positionYPercent = 0.75f
                ),
                TextOverlay(
                    text = "🔥 ¡Guarda este video!",
                    startTimeSec = 5.6f,
                    endTimeSec = 8.0f,
                    textColorHex = 0xFFFF3B5CL,
                    fontSizeSp = 22,
                    animation = TextAnimation.POP,
                    positionYPercent = 0.5f
                )
            ),
            initialStickers = listOf(
                StickerOverlay(emoji = "⚡", startTimeSec = 0f, endTimeSec = 2f, posXPercent = 0.85f, posYPercent = 0.2f),
                StickerOverlay(emoji = "🚀", startTimeSec = 5.5f, endTimeSec = 8f, posXPercent = 0.2f, posYPercent = 0.7f)
            )
        ),
        VideoTemplate(
            id = "template_cinematic_vlog",
            title = "Vlog Cinematográfico",
            description = "Elegante formato 16:9 con gradación de color Teal & Orange y ritmo relajado.",
            category = "Cinematográfico",
            aspectRatio = AspectRatioType.RATIO_16_9,
            defaultMusic = "Lo-Fi Midnight Breeze",
            initialClips = listOf(
                VideoClip(
                    name = "Toma Aérea Amanecer",
                    durationSec = 4.0f,
                    endTrimSec = 4.0f,
                    stockScene = StockSceneType.GOLDEN_HOUR,
                    filter = VideoFilter.CINEMATIC
                ),
                VideoClip(
                    name = "Caminata Urbana",
                    durationSec = 4.5f,
                    endTrimSec = 4.5f,
                    stockScene = StockSceneType.DARK_CINEMA,
                    filter = VideoFilter.CINEMATIC,
                    transitionIn = TransitionType.DISSOLVE
                ),
                VideoClip(
                    name = "Horizonte Nocturno",
                    durationSec = 3.5f,
                    endTrimSec = 3.5f,
                    stockScene = StockSceneType.AURORA_BOREALIS,
                    filter = VideoFilter.CYBER_NEON,
                    transitionIn = TransitionType.FADE_BLACK
                )
            ),
            initialTexts = listOf(
                TextOverlay(
                    text = "DIARIOS DE VIAJE • EP. 01",
                    startTimeSec = 0.5f,
                    endTimeSec = 3.5f,
                    textColorHex = 0xFFFFFFFFL,
                    backgroundColorHex = null,
                    fontSizeSp = 18,
                    animation = TextAnimation.FADE,
                    positionYPercent = 0.85f
                )
            ),
            initialStickers = emptyList()
        ),
        VideoTemplate(
            id = "template_celebration",
            title = "Fiesta y Cumpleaños",
            description = "Brillante, alegre y lleno de confeti para felicitar y recordar momentos especiales.",
            category = "Celebración",
            aspectRatio = AspectRatioType.RATIO_9_16,
            defaultMusic = "Happy Celebration Pop",
            initialClips = listOf(
                VideoClip(
                    name = "Celebración Inicio",
                    durationSec = 3.0f,
                    endTrimSec = 3.0f,
                    stockScene = StockSceneType.SUNSET_GRADIENT,
                    filter = VideoFilter.ULTRA_VIVID
                ),
                VideoClip(
                    name = "Momento Pastel / Brindis",
                    durationSec = 3.5f,
                    endTrimSec = 3.5f,
                    stockScene = StockSceneType.NEON_PULSE,
                    filter = VideoFilter.WARM_SUNSET,
                    transitionIn = TransitionType.ZOOM_IN
                )
            ),
            initialTexts = listOf(
                TextOverlay(
                    text = "🎉 ¡FELIZ DÍA! 🎂",
                    startTimeSec = 0f,
                    endTimeSec = 3.0f,
                    textColorHex = 0xFFFFD700L,
                    fontSizeSp = 26,
                    animation = TextAnimation.BOUNCE,
                    positionYPercent = 0.3f
                ),
                TextOverlay(
                    text = "Que todos tus deseos se cumplan ✨",
                    startTimeSec = 3.0f,
                    endTimeSec = 6.5f,
                    textColorHex = 0xFFFFFFFFL,
                    fontSizeSp = 20,
                    animation = TextAnimation.POP,
                    positionYPercent = 0.7f
                )
            ),
            initialStickers = listOf(
                StickerOverlay(emoji = "🎈", startTimeSec = 0f, endTimeSec = 6.5f, posXPercent = 0.15f, posYPercent = 0.2f),
                StickerOverlay(emoji = "🥳", startTimeSec = 1f, endTimeSec = 6.5f, posXPercent = 0.85f, posYPercent = 0.2f),
                StickerOverlay(emoji = "✨", startTimeSec = 2f, endTimeSec = 6.5f, posXPercent = 0.5f, posYPercent = 0.85f)
            )
        ),
        VideoTemplate(
            id = "template_product_promo",
            title = "Promoción de Producto",
            description = "Ideal para tiendas, emprendimientos y mostrar características clave en formato cuadrado.",
            category = "Negocios",
            aspectRatio = AspectRatioType.RATIO_1_1,
            defaultMusic = "Minimalist Acoustic Chill",
            initialClips = listOf(
                VideoClip(
                    name = "Presentación de Marca",
                    durationSec = 3.0f,
                    endTrimSec = 3.0f,
                    stockScene = StockSceneType.RETRO_GRID,
                    filter = VideoFilter.ULTRA_VIVID
                ),
                VideoClip(
                    name = "Detalles y Beneficios",
                    durationSec = 3.5f,
                    endTrimSec = 3.5f,
                    stockScene = StockSceneType.OCEAN_DEEP,
                    filter = VideoFilter.ORIGINAL,
                    transitionIn = TransitionType.SLIDE_LEFT
                )
            ),
            initialTexts = listOf(
                TextOverlay(
                    text = "NUEVA COLECCIÓN 2026",
                    startTimeSec = 0f,
                    endTimeSec = 3.0f,
                    textColorHex = 0xFFFFFFFFL,
                    fontSizeSp = 20,
                    animation = TextAnimation.POP,
                    positionYPercent = 0.2f
                ),
                TextOverlay(
                    text = "🔥 20% DE DESCUENTO HOY",
                    startTimeSec = 3.0f,
                    endTimeSec = 6.5f,
                    textColorHex = 0xFFFF3B5CL,
                    fontSizeSp = 22,
                    animation = TextAnimation.BOUNCE,
                    positionYPercent = 0.8f
                )
            ),
            initialStickers = listOf(
                StickerOverlay(emoji = "🛍️", startTimeSec = 0f, endTimeSec = 6.5f, posXPercent = 0.85f, posYPercent = 0.15f)
            )
        )
    )
}
