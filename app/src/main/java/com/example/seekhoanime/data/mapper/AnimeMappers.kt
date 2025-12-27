package com.example.seekhoanime.data.mapper


import com.example.seekhoanime.data.dto.AnimeDetailDto
import com.example.seekhoanime.data.dto.AnimeDto
import com.example.seekhoanime.data.dto.CharacterEntryDto
import com.example.seekhoanime.domain.model.AnimeCharacter
import com.example.seekhoanime.domain.model.AnimeDetail
import com.example.seekhoanime.domain.model.AnimeSummary

fun AnimeDto.toDomain(): AnimeSummary {
    val t = title?.trim().orEmpty().ifBlank { "Untitled" }
    val ep = episodes?.let { "$it eps" } ?: "Episodes: N/A"
    val rating = score?.let { "★ $it" } ?: "★ N/A"
    val img = images?.jpg?.imageUrl
    return AnimeSummary(
        id = malId,
        title = t,
        episodesText = ep,
        ratingText = rating,
        imageUrl = img
    )
}

fun AnimeDetailDto.toDomain(): AnimeDetail {
    val t = title?.trim().orEmpty().ifBlank { "Untitled" }
    val syn = synopsis?.trim().orEmpty().ifBlank { "No synopsis available." }
    val ep = episodes?.let { "$it" } ?: "N/A"
    val rating = score?.let { "$it" } ?: "N/A"
    val poster = images?.jpg?.imageUrl

    val genresText = genres
        ?.mapNotNull { it.name?.trim() }
        ?.filter { it.isNotBlank() }
        ?.joinToString(", ")
        ?: "N/A"

    val youtubeId = trailer?.youtubeId
    val embedUrl = trailer?.embedUrl



    return AnimeDetail(
        id = malId,
        title = t,
        synopsis = syn,
        episodesText = ep,
        ratingText = rating,
        posterUrl = poster,
        genresText = genresText,
        trailerYoutubeId = youtubeId,
        trailerEmbedUrl = embedUrl
    )
}

fun List<CharacterEntryDto>.toMainCastDomain(max: Int = 10): List<AnimeCharacter> {
    return this.asSequence()
        .filter { it.role?.equals("Main", ignoreCase = true) == true }
        .mapNotNull { it.character }
        .map {
            AnimeCharacter(
                name = it.name?.trim().orEmpty().ifBlank { "Unknown" },
                imageUrl = it.images?.jpg?.imageUrl
            )
        }
        .take(max)
        .toList()
}
