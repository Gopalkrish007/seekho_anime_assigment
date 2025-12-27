package com.example.seekhoanime.data.repository


import com.example.seekhoanime.core.AppResult
import com.example.seekhoanime.domain.model.AnimeCharacter
import com.example.seekhoanime.domain.model.AnimeDetail
import com.example.seekhoanime.domain.model.AnimeSummary
import kotlinx.coroutines.flow.Flow


interface AnimeRepository {
    fun observeTopAnime(): Flow<List<AnimeSummary>>
    fun observeDetail(animeId: Int): Flow<AnimeDetail?>
    fun observeCast(animeId: Int): Flow<List<AnimeCharacter>>

    suspend fun refreshTopAnime(): AppResult<Unit>
    suspend fun refreshDetail(animeId: Int): AppResult<Unit>
    suspend fun refreshCast(animeId: Int): AppResult<Unit>
}