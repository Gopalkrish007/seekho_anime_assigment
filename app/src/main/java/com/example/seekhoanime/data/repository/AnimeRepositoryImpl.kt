package com.example.seekhoanime.data.repository


import com.example.seekhoanime.core.AppResult
import com.example.seekhoanime.core.ErrorMapper
import com.example.seekhoanime.data.db.dao.AnimeDao
import com.example.seekhoanime.data.db.entity.AnimeCharacterEntity
import com.example.seekhoanime.data.db.entity.AnimeDetailEntity
import com.example.seekhoanime.data.db.entity.AnimeSummaryEntity
import com.example.seekhoanime.data.mapper.toDomain
import com.example.seekhoanime.data.mapper.toMainCastDomain
import com.example.seekhoanime.data.network.ApiClient
import com.example.seekhoanime.data.network.JikanApi
import com.example.seekhoanime.domain.model.AnimeCharacter
import com.example.seekhoanime.domain.model.AnimeDetail
import com.example.seekhoanime.domain.model.AnimeSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class AnimeRepositoryImpl(
    private val dao: AnimeDao,
    private val api: JikanApi

) : AnimeRepository {

//    private val api = ApiClient.api

    override fun observeTopAnime(): Flow<List<AnimeSummary>> =
        dao.observeTopAnime().map { list ->
            list.map { AnimeSummary(it.id, it.title, it.episodesText, it.ratingText, it.imageUrl) }
        }

    override fun observeDetail(animeId: Int): Flow<AnimeDetail?> =
        dao.observeAnimeDetail(animeId).map { e ->
            e?.let {
                AnimeDetail(
                    id = it.id,
                    title = it.title,
                    synopsis = it.synopsis,
                    episodesText = it.episodesText,
                    ratingText = it.ratingText,
                    posterUrl = it.posterUrl,
                    genresText = it.genresText,
                    trailerYoutubeId = it.trailerYoutubeId,
                    trailerEmbedUrl = it.trailerEmbedUrl

                )
            }
        }

    override fun observeCast(animeId: Int): Flow<List<AnimeCharacter>> =
        dao.observeCast(animeId).map { list ->
            list.map { AnimeCharacter(it.name, it.imageUrl) }
        }

    override suspend fun refreshTopAnime(): AppResult<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val top = api.getTopAnime().data.map { dto ->
                val domain = dto.toDomain()
                AnimeSummaryEntity(
                    id = domain.id,
                    title = domain.title,
                    episodesText = domain.episodesText,
                    ratingText = domain.ratingText,
                    imageUrl = domain.imageUrl,
                    updatedAt = now
                )
            }

            dao.clearTopAnime()
            dao.upsertTopAnime(top)

            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Error(ErrorMapper.map(t))
        }
    }

    override suspend fun refreshDetail(animeId: Int): AppResult<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val dto = api.getAnimeDetail(animeId).data
                ?: return AppResult.Error(ErrorMapper.map(NullPointerException("Detail data is null")))

            val domain = dto.toDomain()

            dao.upsertAnimeDetail(
                AnimeDetailEntity(
                    id = domain.id,
                    title = domain.title,
                    synopsis = domain.synopsis,
                    episodesText = domain.episodesText,
                    ratingText = domain.ratingText,
                    posterUrl = domain.posterUrl,
                    genresText = domain.genresText,
                    trailerYoutubeId = domain.trailerYoutubeId,
                    trailerEmbedUrl = domain.trailerEmbedUrl,
                    updatedAt = now
                )
            )

            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Error(ErrorMapper.map(t))
        }
    }

    override suspend fun refreshCast(animeId: Int): AppResult<Unit> {
        return try {
            val cast = api.getAnimeCharacters(animeId).data
                .toMainCastDomain()
                .map {
                    AnimeCharacterEntity(
                        animeId = animeId,
                        name = it.name,
                        imageUrl = it.imageUrl
                    )
                }

            dao.replaceCast(animeId, cast)

            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Error(ErrorMapper.map(t))
        }
    }
}
