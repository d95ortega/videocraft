package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val aspectRatioTag: String,
    val clipsJson: String,
    val textOverlaysJson: String,
    val stickersJson: String,
    val audioTrackJson: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isExported: Boolean,
    val exportedUri: String?
)
