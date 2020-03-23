package com.lightningkite.khrysalis.net

import android.util.Log
import com.lightningkite.khrysalis.Codable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

typealias HttpBody = RequestBody
typealias HttpMimeType = MediaType

fun Codable.toJsonHttpBody() {
    val sending = HttpClient.mapper.writeValueAsString(this)
    Log.i("HttpClient", "with body $sending")
    RequestBody.create(MediaType.parse("application/json"), sending)
}

/* SHARED DECLARATIONS
class MultipartBody {
    companion object {
        val MIXED: HttpMimeType
        val ALTERNATIVE: HttpMimeType
        val DIGEST: HttpMimeType
        val PARALLEL: HttpMimeType
        val FORM: HttpMimeType
    }
    class Builder {
        fun setType(type: HttpMimeType)
        fun addFormDataPart(name: String, value: String): Builder
        fun addFormDataPart(name: String, filename: String, body: HttpBody): Builder
        fun build(): HttpBody
    }
}
 */
