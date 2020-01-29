package com.lightningkite.khrysalis

class Failable<T>(
    val result: T? = null,
    val issue: String? = null
) {
    companion object {
        fun <T> failure(message: String): Failable<T> = Failable<T>(issue = message)
        fun <T> success(value: T): Failable<T> = Failable<T>(result = value)
    }
}
