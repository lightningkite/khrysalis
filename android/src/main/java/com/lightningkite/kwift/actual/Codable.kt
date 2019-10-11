package com.lightningkite.kwift.actual

interface Codable
typealias IsCodable = Any

fun IsCodable?.toJsonString(): String {
    return HttpClient.mapper.writeValueAsString(this)
}

inline fun <reified T: IsCodable> String.fromJsonString(): T? {
    return try {
        HttpClient.mapper.readValue(this, T::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.fromJsonStringUntyped(): IsCodable? {
    return try {
        HttpClient.mapper.readValue(this, Any::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
