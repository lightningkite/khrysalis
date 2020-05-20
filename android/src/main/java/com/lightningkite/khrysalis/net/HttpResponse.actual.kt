package com.lightningkite.khrysalis.net

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.IsCodable
import com.lightningkite.khrysalis.PlatformSpecific
import com.lightningkite.khrysalis.fromJsonString
import okhttp3.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

typealias HttpResponse = Response

val HttpResponse.code: Int get() = this.code()
/* SHARED DECLARATIONS
val HttpResponse.isSuccessful: Boolean get() = true
*/
val HttpResponse.headers: Map<String, String> get() = this.headers().toMultimap().mapValues { it.value.joinToString(";") }

@Deprecated("Instead, you should use the single Rx transformation to read the data due to flow.")
fun HttpResponse.readText() = this.body()!!.string()
@Deprecated("Instead, you should use the single Rx transformation to read the data due to flow.")
inline fun <reified T> HttpResponse.readJson(): T {
    var raw: String? = null
    try {
        raw = readText()
        return HttpClient.mapper.readValue<T>(raw)
    } catch(e:Exception){
        throw IllegalStateException("Failed to parse '$raw'", e)
    }
}

@Deprecated("Instead, you should use the single Rx transformation to read the data due to flow.")
@PlatformSpecific inline fun <reified T> HttpResponse.readJson(typeToken: TypeReference<T>): T {
    var raw: String? = null
    try {
        raw = readText()
        return HttpClient.mapper.readValue<T>(raw, typeToken)
    } catch(e:Exception){
        throw IllegalStateException("Failed to parse '$raw'", e)
    }
}
