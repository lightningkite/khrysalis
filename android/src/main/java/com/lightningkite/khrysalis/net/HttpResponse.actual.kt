package com.lightningkite.khrysalis.net

import okhttp3.Response

typealias HttpResponse = Response

val HttpResponse.code: Int get() = this.code()
/* SHARED DECLARATIONS
val HttpResponse.isSuccessful: Boolean get() = true
*/
val HttpResponse.headers: Map<String, String> get() = this.headers().toMultimap().mapValues { it.value.joinToString(";") }
fun HttpResponse.bodyText() = this.body()?.string()

private fun test(response: HttpResponse){
    response.message()
}
