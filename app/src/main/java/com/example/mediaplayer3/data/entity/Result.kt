package com.example.mediaplayer3.data.entity


sealed class Result<out T> {

    data class Success<out T>(val data: T): Result<T>()

    data class Error(val t: Throwable): Result<Nothing>()
}