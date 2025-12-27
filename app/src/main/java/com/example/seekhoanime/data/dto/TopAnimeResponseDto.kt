package com.example.seekhoanime.data.dto


import com.google.gson.annotations.SerializedName

data class TopAnimeResponseDto(
    @SerializedName("data") val data: List<AnimeDto> = emptyList()
)

data class AnimeDto(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("episodes") val episodes: Int?,
    @SerializedName("score") val score: Double?,
    @SerializedName("images") val images: ImagesDto?
)

data class ImagesDto(
    @SerializedName("jpg") val jpg: JpgDto?
)

data class JpgDto(
    @SerializedName("image_url") val imageUrl: String?
)
