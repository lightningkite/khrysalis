package com.lightningkite.kwift

data class Failable<T>(
    val result: T? = null,
    val issue: String? = null
) {
    companion object {
        fun <T> failure(message: String): Failable<T> = Failable(issue = message)
        fun <T> success(value: T): Failable<T> = Failable(result = value)
    }
}
