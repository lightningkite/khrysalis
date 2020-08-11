package com.lightningkite.khrysalis.net

import android.util.Log
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.IsCodable
import com.lightningkite.khrysalis.PlatformSpecific
import com.lightningkite.khrysalis.bytes.Data
import com.lightningkite.khrysalis.fromJsonString
import io.reactivex.Single
import io.reactivex.SingleEmitter
import okhttp3.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

typealias HttpResponse = Response

val HttpResponse.code: Int get() = this.code()
/* SHARED DECLARATIONS
val HttpResponse.isSuccessful: Boolean get() = true
*/
val HttpResponse.headers: Map<String, String> get() = this.headers().toMultimap().mapValues { it.value.joinToString(";") }

fun HttpResponse.discard(): Single<Unit> {
    return with(HttpClient){
        Single.create<Unit> { em ->
            body()!!.close()
            em.onSuccess(Unit)
        }.threadCorrectly()
    }
}
fun HttpResponse.readText(): Single<String> {
    return with(HttpClient){
        Single.create<String> { em -> em.onSuccess(body()!!.use { it.string() }) }.threadCorrectly()
    }
}
fun HttpResponse.readData(): Single<Data> {
    return with(HttpClient){
        Single.create<Data> { em -> em.onSuccess(body()!!.use { it.bytes() }) }.threadCorrectly()
    }
}

inline fun <reified T> HttpResponse.readJson(): Single<T> = readJson(jacksonTypeRef())

@PlatformSpecific fun <T> HttpResponse.readJson(typeToken: TypeReference<T>): Single<T> {
    return with(HttpClient){
        Single.create<T> { em: SingleEmitter<T> ->
            try {
                val result: T = HttpClient.mapper.readValue<T>(body()!!.use { it.byteStream() }, typeToken)
                em.onSuccess(result)
            } catch(e: Throwable){
                em.onError(e)
            }
        }.threadCorrectly<T>()
    }
}

inline fun <reified T> HttpResponse.readJsonDebug(): Single<T> = readJson(jacksonTypeRef())

@PlatformSpecific fun <T> HttpResponse.readJsonDebug(typeToken: TypeReference<T>): Single<T> {
    return with(HttpClient){
        Single.create<T> { em: SingleEmitter<T> ->
            try {
                val data = body()!!.use { it.string() }
                Log.d("HttpResponse", "Got '$data'")
                val result: T = HttpClient.mapper.readValue<T>(data, typeToken)
                em.onSuccess(result)
            } catch(e: Throwable){
                em.onError(e)
            }
        }.threadCorrectly<T>()
    }
}
