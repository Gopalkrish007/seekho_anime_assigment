package com.example.seekhoanime.data.db.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "anime_character",
    primaryKeys = ["animeId", "name"],
    indices = [Index("animeId")]
)
data class AnimeCharacterEntity(
    val animeId: Int,
    val name: String,
    val imageUrl: String?
)
