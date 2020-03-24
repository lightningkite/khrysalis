package com.lightningkite.khrysalis.net

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.lightningkite.khrysalis.Codable
import com.lightningkite.khrysalis.IsCodable
import com.lightningkite.khrysalis.PlatformSpecific
import com.lightningkite.khrysalis.swiftExactly
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
    val typeReference = jacksonTypeRef<T>()
    return this.map { it ->
        if(it.isSuccessful){
            return@map it.readJson<T>(typeReference)
        } else {
            throw HttpResponseException(it)
        }
    }
}
