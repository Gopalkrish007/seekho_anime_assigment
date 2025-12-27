package com.example.seekhoanime


import android.content.Context
import com.example.seekhoanime.data.db.AnimeDatabase
import com.example.seekhoanime.data.network.ApiClient
import com.example.seekhoanime.data.network.NetworkMonitor
import com.example.seekhoanime.data.repository.AnimeRepository
import com.example.seekhoanime.data.repository.AnimeRepositoryImpl

object AppGraph {
    lateinit var repo: AnimeRepository
    lateinit var networkMonitor: NetworkMonitor
        private set

    fun init(context: Context) {
        val db = AnimeDatabase.create(context)
        repo = AnimeRepositoryImpl(db.animeDao(), ApiClient.api)
        networkMonitor = NetworkMonitor(context)
    }
}
