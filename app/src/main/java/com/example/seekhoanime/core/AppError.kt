package com.example.seekhoanime.core

sealed class AppError(open val debugMessage: String? = null) {
    data class Network(override val debugMessage: String? = null) : AppError(debugMessage)
    data class RateLimited(override val debugMessage: String? = null) : AppError(debugMessage)
    data class Server(override val debugMessage: String? = null) : AppError(debugMessage)
    data class NotFound(override val debugMessage: String? = null) : AppError(debugMessage)
    data class Database(override val debugMessage: String? = null) : AppError(debugMessage)
    data class Unknown(override val debugMessage: String? = null) : AppError(debugMessage)
}