package com.example.seekhoanime.domain.model

data class AnimeSummary(
    val id: Int,
    val title: String,
    val episodesText: String,
    val ratingText: String,
    val imageUrl: String?
)
