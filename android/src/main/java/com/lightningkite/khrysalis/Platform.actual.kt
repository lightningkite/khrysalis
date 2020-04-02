package com.lightningkite.khrysalis

/**
 *
 * Enum to call and determine what platform the code is currently on.
 *
 */

enum class Platform {
    iOS, Android;

    companion object {
        val current = Android
    }
}
