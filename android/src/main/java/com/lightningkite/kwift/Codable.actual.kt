package com.lightningkite.kwift

import com.lightningkite.kwift.net.HttpClient

interface Codable
typealias IsCodable = Any
typealias JsonList = List<*>
typealias JsonMap = Map<*, *>

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

