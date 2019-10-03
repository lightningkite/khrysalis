package com.lightningkite.kwift.actuals

interface Codable

fun Any?.toJsonString(): String {
    return HttpClient.mapper.writeValueAsString(this)
}

inline fun <reified T> String.fromJsonString(): T? {
    return try {
        HttpClient.mapper.readValue(this, T::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.fromJsonStringUntyped(): Any? {
    return try {
        HttpClient.mapper.readValue(this, Any::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
