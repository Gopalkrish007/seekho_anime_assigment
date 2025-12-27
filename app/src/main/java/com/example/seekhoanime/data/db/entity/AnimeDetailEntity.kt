package com.example.seekhoanime.data.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_detail")
data class AnimeDetailEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val synopsis: String,
    val episodesText: String,
    val ratingText: String,
    val posterUrl: String?,
    val genresText: String,
    val trailerYoutubeId: String?,
    val trailerEmbedUrl : String?,
    val updatedAt: Long,
)
