package com.lightningkite.khrysalis.net

import com.lightningkite.khrysalis.Codable
import com.lightningkite.khrysalis.PlatformSpecific
import com.lightningkite.khrysalis.swiftExactly
import io.reactivex.Single

fun Single<@swiftExactly("Element") HttpResponse>.unsuccessfulAsError(): Single<HttpResponse> {
    return this.map { it ->
        if(it.isSuccessful){
            return@map it
        } else {
            throw HttpResponseException(it)
        }
    }
}


inline fun <reified T: Codable> Single<@swiftExactly("Element") HttpResponse>.readJson(): Single<T> {
    return this.map { it ->
        if(it.isSuccessful){
            return@map it.readJson<T>()
        } else {
            throw HttpResponseException(it)
        }
    }
}
