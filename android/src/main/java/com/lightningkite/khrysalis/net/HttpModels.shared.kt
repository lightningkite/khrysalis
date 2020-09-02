package com.lightningkite.khrysalis.net

import okhttp3.CacheControl
import java.util.concurrent.TimeUnit

enum class HttpPhase {
    Connect,
    Write,
    Waiting,
    Read,
    Done
}
data class HttpProgress(
    val phase: HttpPhase,
    val ratio: Float
) {
    val approximate: Float get() = when(phase){
        HttpPhase.Connect -> 0f
        HttpPhase.Write -> 0.15f + 0.5f * ratio
        HttpPhase.Waiting -> 0.65f
        HttpPhase.Read -> 0.7f + 0.3f * ratio
        HttpPhase.Done -> 1f
    }
    companion object {
        val connecting: HttpProgress = HttpProgress(HttpPhase.Connect, 0f)
        val waiting: HttpProgress = HttpProgress(HttpPhase.Waiting, 0f)
        val done: HttpProgress = HttpProgress(HttpPhase.Done, 0f)
    }
}

data class HttpOptions(
    val callTimeout: Long? = null,
    val writeTimeout: Long? = 10_000L,
    val readTimeout: Long? = 10_000L,
    val connectTimeout: Long? = 10_000L,
    val cacheMode: HttpCacheMode = HttpCacheMode.Default
)

enum class HttpCacheMode {
    Default,
    NoStore,
    Reload,
    NoCache,
    ForceCache,
    OnlyIfCached
}