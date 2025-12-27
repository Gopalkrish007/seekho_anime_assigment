package com.example.seekhoanime.core

import retrofit2.HttpException
import java.io.IOException

object ErrorMapper {
    fun map(t: Throwable): AppError {
        return when (t) {
            is IOException -> AppError.Network(t.message)
            is HttpException -> {
                when (t.code()) {
                    429 -> AppError.RateLimited(t.message())
                    404 -> AppError.NotFound(t.message())
                    in 500..599 -> AppError.Server(t.message())
                    else -> AppError.Unknown(t.message())
                }
            }
            is android.database.sqlite.SQLiteException -> AppError.Database(t.message)
            else -> AppError.Unknown(t.message)
        }
    }

    fun userMessage(e: AppError): String = when (e) {
        is AppError.Network -> "No internet. Showing cached data."
        is AppError.RateLimited -> "Too many requests. Try again in a few seconds."
        is AppError.Server -> "Server issue. Try again."
        is AppError.NotFound -> "Data not found."
        is AppError.Database -> "Storage error. Please reopen the app."
        is AppError.Unknown -> "Something went wrong."
    }
}
