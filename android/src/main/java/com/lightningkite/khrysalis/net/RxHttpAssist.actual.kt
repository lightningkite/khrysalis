package com.lightningkite.khrysalis.net

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.*
import com.lightningkite.khrysalis.bytes.Data
import io.reactivex.Observable
import io.reactivex.Single
import java.lang.reflect.ParameterizedType

fun Single<@SwiftExactly("Element") HttpResponse>.unsuccessfulAsError(): Single<HttpResponse> {
    return this.map { it ->
        if(it.isSuccessful){
            return@map it
        } else {
            throw HttpResponseException(it)
        }
    }
}


fun Single<@SwiftExactly("Element") HttpResponse>.discard(): Single<Unit> {
    return this.flatMap { it.discard() }
}
inline fun <reified T> Single<@SwiftExactly("Element") HttpResponse>.readJson(): Single<T> {
    val type = jacksonTypeRef<T>()
    return this.flatMap { it ->
        if(it.isSuccessful){
            it.readJson<T>(type)
        } else {
            Single.error<T>(HttpResponseException(it)) as Single<T>
        }
    }
}
inline fun <reified T> Single<@SwiftExactly("Element") HttpResponse>.readJsonDebug(): Single<T> {
    val type = jacksonTypeRef<T>()
    return this.flatMap { it ->
        if(it.isSuccessful){
            it.readJsonDebug<T>(type)
        } else {
            Single.error<T>(HttpResponseException(it)) as Single<T>
        }
    }
}
fun Single<@SwiftExactly("Element") HttpResponse>.readText(): Single<String> {
    return this.flatMap { it ->
        if(it.isSuccessful){
            it.readText()
        } else {
            Single.error<String>(HttpResponseException(it))
        }
    }
}
fun Single<@SwiftExactly("Element") HttpResponse>.readData(): Single<Data> {
    return this.flatMap { it ->
        if(it.isSuccessful){
            it.readData()
        } else {
            Single.error<Data>(HttpResponseException(it))
        }
    }
}