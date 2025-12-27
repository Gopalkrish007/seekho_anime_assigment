package com.example.seekhoanime.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_summary")
data class AnimeSummaryEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val episodesText: String,
    val ratingText: String,
    val imageUrl: String?,
    val updatedAt: Long
)
