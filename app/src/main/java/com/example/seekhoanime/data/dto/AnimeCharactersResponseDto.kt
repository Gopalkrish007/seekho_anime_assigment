package com.example.seekhoanime.data.dto


import com.google.gson.annotations.SerializedName

data class AnimeCharactersResponseDto(
    @SerializedName("data") val data: List<CharacterEntryDto> = emptyList()
)

data class CharacterEntryDto(
    @SerializedName("role") val role: String?,
    @SerializedName("character") val character: CharacterDto?
)

data class CharacterDto(
    @SerializedName("name") val name: String?,
    @SerializedName("images") val images: ImagesDto?
)
