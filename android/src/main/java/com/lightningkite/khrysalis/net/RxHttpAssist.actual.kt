package com.lightningkite.khrysalis.net

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.Codable
import com.lightningkite.khrysalis.IsCodable
import com.lightningkite.khrysalis.PlatformSpecific
import com.lightningkite.khrysalis.bytes.Data
import com.lightningkite.khrysalis.swiftExactly
import io.reactivex.Observable
import io.reactivex.Single
import java.lang.reflect.ParameterizedType

fun Single<@swiftExactly("Element") HttpResponse>.unsuccessfulAsError(): Single<HttpResponse> {
    return this.map { it ->
        if(it.isSuccessful){
            return@map it
        } else {
            throw HttpResponseException(it)
        }
    }
}


inline fun <reified T> Single<@swiftExactly("Element") HttpResponse>.readJson(): Single<T> {
    return this.flatMap { it ->
        if(it.isSuccessful){
            return@flatMap with(HttpClient){
                Single.create<T> { em -> em.onSuccess(HttpClient.mapper.readValue<T>(it.body()!!.byteStream())) }
                    .threadCorrectly()
            }
        } else {
            Single.error<T>(HttpResponseException(it))
        }
    }
}
fun Single<@swiftExactly("Element") HttpResponse>.readText(): Single<String> {
    return this.flatMap { it ->
        if(it.isSuccessful){
            return@flatMap with(HttpClient){
                Single.create<String> { em -> em.onSuccess(it.body()!!.string()) }
                    .threadCorrectly()
            }
        } else {
            Single.error<String>(HttpResponseException(it))
        }
    }
}
fun Single<@swiftExactly("Element") HttpResponse>.readData(): Single<Data> {
    return this.flatMap { it ->
        if(it.isSuccessful){
            return@flatMap with(HttpClient){
                Single.create<Data> { em -> em.onSuccess(it.body()!!.bytes()) }
                    .threadCorrectly()
            }
        } else {
            Single.error<Data>(HttpResponseException(it))
        }
    }
}
fun <Element> Single<Element>.readHttpException(): Single<Element> {
    return this.onErrorResumeNext {
        if(it is HttpResponseException){
            return@onErrorResumeNext with(HttpClient){
                Single.create<Element> { em -> throw HttpReadResponseException(it.response, it.response.body()!!.string(), it.cause) }
                    .threadCorrectly()
            }
        } else {
            Single.error<Element>(it)
        }
    }
}