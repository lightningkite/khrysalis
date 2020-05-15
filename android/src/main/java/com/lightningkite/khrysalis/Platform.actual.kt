package com.lightningkite.khrysalis

/**
 *
 * Enum to call and determine what platform the code is currently on.
 *
 */

enum class Platform {
    iOS, Android, Web;

    companion object {
        val current = Android
    }
}
