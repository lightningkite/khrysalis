package com.lightningkite.khrysalis.net

class HttpResponseException(val response: HttpResponse, cause: Throwable? = null): Exception("Got code ${response.code}", cause)
