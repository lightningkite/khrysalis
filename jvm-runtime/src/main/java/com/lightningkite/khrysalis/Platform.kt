package com.lightningkite.khrysalis

enum class Platform {
    Android, Ios, Web;
    companion object {
        val current get() = Platform.Android
    }
}