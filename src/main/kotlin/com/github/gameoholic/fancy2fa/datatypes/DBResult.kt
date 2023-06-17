package com.github.gameoholic.fancy2fa.datatypes

sealed class InternalDBResult<out T> {
        data class Success<out T>(val result: T) : InternalDBResult<T>()
        data class Error(val errorMessage: String) : InternalDBResult<Nothing>()
}

data class DBResult<T>(val result: T)
