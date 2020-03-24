package com.lightningkite.khrysalis.net

import com.lightningkite.khrysalis.IsCodable
import com.lightningkite.khrysalis.fromJsonString
import okhttp3.Response

typealias HttpResponse = Response

val HttpResponse.code: Int get() = this.code()
/* SHARED DECLARATIONS
val HttpResponse.isSuccessful: Boolean get() = true
*/
val HttpResponse.headers: Map<String, String> get() = this.headers().toMultimap().mapValues { it.value.joinToString(";") }
fun HttpResponse.readText() = this.body()?.string()
inline fun <reified T: IsCodable> HttpResponse.readJson(): T {
    var raw: String? = null
    try {
        raw = readText()
        return HttpClient.mapper.readValue(raw, T::class.java)
    } catch(e:Exception){
        throw IllegalStateException("Failed to parse '$raw'", e)
    }
}
