package com.example.seekhoanime.domain.model


data class AnimeDetail(
    val id: Int,
    val title: String,
    val synopsis: String,
    val episodesText: String,
    val ratingText: String,
    val posterUrl: String?,
    val genresText: String,
    val trailerYoutubeId: String?,
    val trailerEmbedUrl: String?
)
