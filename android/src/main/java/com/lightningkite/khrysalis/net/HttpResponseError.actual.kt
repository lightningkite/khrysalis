package com.lightningkite.khrysalis.net

open class HttpResponseException(val response: HttpResponse, cause: Throwable? = null): Exception("Got code ${response.code}", cause)
class HttpReadResponseException(response: HttpResponse, val text: String, cause: Throwable? = null): HttpResponseException(response, cause)
