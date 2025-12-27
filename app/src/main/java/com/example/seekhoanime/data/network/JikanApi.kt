package com.example.seekhoanime.data.network

import com.example.seekhoanime.data.dto.AnimeCharactersResponseDto
import com.example.seekhoanime.data.dto.AnimeDetailResponseDto
import com.example.seekhoanime.data.dto.TopAnimeResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JikanApi {

    // Top Anime list
    @GET("v4/top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int = 1
    ): TopAnimeResponseDto

    //Anime detail
    @GET("v4/anime/{id}")
    suspend fun getAnimeDetail(
        @Path("id") animeId: Int
    ): AnimeDetailResponseDto

    // Needed to show "Main cast" (Jikan provides characters separately)
    @GET("v4/anime/{id}/characters")
    suspend fun getAnimeCharacters(
        @Path("id") animeId: Int
    ): AnimeCharactersResponseDto
}