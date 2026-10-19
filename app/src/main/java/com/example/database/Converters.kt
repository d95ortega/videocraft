package com.example.database

import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

object ProjectJsonParser {

    fun serializeClips(clips: List<VideoClip>): String {
        val array = JSONArray()
        for (c in clips) {
            val obj = JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("uri", c.uri ?: "")
                put("durationSec", c.durationSec.toDouble())
                put("startTrimSec", c.startTrimSec.toDouble())
                put("endTrimSec", c.endTrimSec.toDouble())
                put("filter", c.filter.name)
                put("speed", c.speed.toDouble())
                put("volume", c.volume.toDouble())
                put("stockScene", c.stockScene.name)
                put("transitionIn", c.transitionIn.name)
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun deserializeClips(json: String?): List<VideoClip> {
        if (json.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<VideoClip>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val filterStr = obj.optString("filter", VideoFilter.ORIGINAL.name)
                val stockStr = obj.optString("stockScene", StockSceneType.SUNSET_GRADIENT.name)
                val transStr = obj.optString("transitionIn", TransitionType.NONE.name)
                list.add(
                    VideoClip(
                        id = obj.optString("id"),
                        name = obj.optString("name", "Clip"),
                        uri = obj.optString("uri").takeIf { it.isNotEmpty() },
                        durationSec = obj.optDouble("durationSec", 3.0).toFloat(),
                        startTrimSec = obj.optDouble("startTrimSec", 0.0).toFloat(),
                        endTrimSec = obj.optDouble("endTrimSec", 3.0).toFloat(),
                        filter = runCatching { VideoFilter.valueOf(filterStr) }.getOrDefault(VideoFilter.ORIGINAL),
                        speed = obj.optDouble("speed", 1.0).toFloat(),
                        volume = obj.optDouble("volume", 1.0).toFloat(),
                        stockScene = runCatching { StockSceneType.valueOf(stockStr) }.getOrDefault(StockSceneType.SUNSET_GRADIENT),
                        transitionIn = runCatching { TransitionType.valueOf(transStr) }.getOrDefault(TransitionType.NONE)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun serializeTexts(texts: List<TextOverlay>): String {
        val array = JSONArray()
        for (t in texts) {
            val obj = JSONObject().apply {
                put("id", t.id)
                put("text", t.text)
                put("startTimeSec", t.startTimeSec.toDouble())
                put("endTimeSec", t.endTimeSec.toDouble())
                put("textColorHex", t.textColorHex)
                put("backgroundColorHex", t.backgroundColorHex ?: -1L)
                put("fontSizeSp", t.fontSizeSp)
                put("animation", t.animation.name)
                put("positionYPercent", t.positionYPercent.toDouble())
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun deserializeTexts(json: String?): List<TextOverlay> {
        if (json.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<TextOverlay>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val animStr = obj.optString("animation", TextAnimation.POP.name)
                val bg = obj.optLong("backgroundColorHex", -1L)
                list.add(
                    TextOverlay(
                        id = obj.optString("id"),
                        text = obj.optString("text", ""),
                        startTimeSec = obj.optDouble("startTimeSec", 0.0).toFloat(),
                        endTimeSec = obj.optDouble("endTimeSec", 3.0).toFloat(),
                        textColorHex = obj.optLong("textColorHex", 0xFFFFFFFFL),
                        backgroundColorHex = if (bg == -1L) null else bg,
                        fontSizeSp = obj.optInt("fontSizeSp", 22),
                        animation = runCatching { TextAnimation.valueOf(animStr) }.getOrDefault(TextAnimation.POP),
                        positionYPercent = obj.optDouble("positionYPercent", 0.5).toFloat()
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun serializeStickers(stickers: List<StickerOverlay>): String {
        val array = JSONArray()
        for (s in stickers) {
            val obj = JSONObject().apply {
                put("id", s.id)
                put("emoji", s.emoji)
                put("startTimeSec", s.startTimeSec.toDouble())
                put("endTimeSec", s.endTimeSec.toDouble())
                put("posXPercent", s.posXPercent.toDouble())
                put("posYPercent", s.posYPercent.toDouble())
                put("scale", s.scale.toDouble())
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun deserializeStickers(json: String?): List<StickerOverlay> {
        if (json.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<StickerOverlay>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    StickerOverlay(
                        id = obj.optString("id"),
                        emoji = obj.optString("emoji", "✨"),
                        startTimeSec = obj.optDouble("startTimeSec", 0.0).toFloat(),
                        endTimeSec = obj.optDouble("endTimeSec", 3.0).toFloat(),
                        posXPercent = obj.optDouble("posXPercent", 0.5).toFloat(),
                        posYPercent = obj.optDouble("posYPercent", 0.5).toFloat(),
                        scale = obj.optDouble("scale", 1.0).toFloat()
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun serializeAudio(audio: AudioTrack?): String? {
        if (audio == null) return null
        return JSONObject().apply {
            put("id", audio.id)
            put("title", audio.title)
            put("artist", audio.artist)
            put("genre", audio.genre)
            put("durationSec", audio.durationSec.toDouble())
            put("bpm", audio.bpm)
            put("volume", audio.volume.toDouble())
            put("isMuted", audio.isMuted)
            val freqArr = JSONArray()
            audio.noteFrequencies.forEach { freqArr.put(it) }
            put("noteFrequencies", freqArr)
        }.toString()
    }

    fun deserializeAudio(json: String?): AudioTrack? {
        if (json.isNullOrEmpty()) return null
        return try {
            val obj = JSONObject(json)
            val freqArr = obj.optJSONArray("noteFrequencies")
            val freqs = mutableListOf<Int>()
            if (freqArr != null) {
                for (i in 0 until freqArr.length()) {
                    freqs.add(freqArr.getInt(i))
                }
            }
            AudioTrack(
                id = obj.optString("id"),
                title = obj.optString("title", "Audio"),
                artist = obj.optString("artist", "Artist"),
                genre = obj.optString("genre", "Genre"),
                durationSec = obj.optDouble("durationSec", 30.0).toFloat(),
                bpm = obj.optInt("bpm", 120),
                volume = obj.optDouble("volume", 0.8).toFloat(),
                isMuted = obj.optBoolean("isMuted", false),
                noteFrequencies = freqs.ifEmpty { listOf(261, 329, 392, 523) }
            )
        } catch (_: Exception) {
            null
        }
    }

    fun toEntity(project: VideoProject): ProjectEntity {
        return ProjectEntity(
            id = project.id,
            title = project.title,
            aspectRatioTag = project.aspectRatio.tag,
            clipsJson = serializeClips(project.clips),
            textOverlaysJson = serializeTexts(project.textOverlays),
            stickersJson = serializeStickers(project.stickers),
            audioTrackJson = serializeAudio(project.audioTrack),
            createdAt = project.createdAt,
            updatedAt = project.updatedAt,
            isExported = project.isExported,
            exportedUri = project.exportedUri
        )
    }

    fun toModel(entity: ProjectEntity): VideoProject {
        val ratio = AspectRatioType.entries.find { it.tag == entity.aspectRatioTag } ?: AspectRatioType.RATIO_9_16
        return VideoProject(
            id = entity.id,
            title = entity.title,
            aspectRatio = ratio,
            clips = deserializeClips(entity.clipsJson),
            textOverlays = deserializeTexts(entity.textOverlaysJson),
            stickers = deserializeStickers(entity.stickersJson),
            audioTrack = deserializeAudio(entity.audioTrackJson),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            isExported = entity.isExported,
            exportedUri = entity.exportedUri
        )
    }
}
