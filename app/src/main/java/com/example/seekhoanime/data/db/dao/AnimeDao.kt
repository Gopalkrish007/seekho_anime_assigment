package com.example.seekhoanime.data.db.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.seekhoanime.data.db.entity.AnimeCharacterEntity
import com.example.seekhoanime.data.db.entity.AnimeDetailEntity
import com.example.seekhoanime.data.db.entity.AnimeSummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    // Top list
    @Query("SELECT * FROM anime_summary ORDER BY ratingText DESC")
    fun observeTopAnime(): Flow<List<AnimeSummaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTopAnime(list: List<AnimeSummaryEntity>)

    @Query("DELETE FROM anime_summary")
    suspend fun clearTopAnime()

    // Detail
    @Query("SELECT * FROM anime_detail WHERE id = :animeId LIMIT 1")
    fun observeAnimeDetail(animeId: Int): Flow<AnimeDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAnimeDetail(entity: AnimeDetailEntity)

    // Cast
    @Query("SELECT * FROM anime_character WHERE animeId = :animeId")
    fun observeCast(animeId: Int): Flow<List<AnimeCharacterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCast(list: List<AnimeCharacterEntity>)

    @Query("DELETE FROM anime_character WHERE animeId = :animeId")
    suspend fun deleteCastForAnime(animeId: Int)

    @Transaction
    suspend fun replaceCast(animeId: Int, list: List<AnimeCharacterEntity>) {
        deleteCastForAnime(animeId)
        upsertCast(list)
    }
}
