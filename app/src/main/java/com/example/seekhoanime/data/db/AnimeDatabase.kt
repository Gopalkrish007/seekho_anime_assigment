package com.example.seekhoanime.data.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.seekhoanime.data.db.dao.AnimeDao
import com.example.seekhoanime.data.db.entity.AnimeCharacterEntity
import com.example.seekhoanime.data.db.entity.AnimeDetailEntity
import com.example.seekhoanime.data.db.entity.AnimeSummaryEntity

@Database(
    entities = [AnimeSummaryEntity::class, AnimeDetailEntity::class, AnimeCharacterEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AnimeDatabase : RoomDatabase() {
    abstract fun animeDao(): AnimeDao

    companion object {
        fun create(context: Context): AnimeDatabase =
            Room.databaseBuilder(context, AnimeDatabase::class.java, "anime_db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
