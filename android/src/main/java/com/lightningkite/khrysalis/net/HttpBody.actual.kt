package com.lightningkite.khrysalis.net

import android.graphics.Bitmap
import android.util.Log
import com.lightningkite.khrysalis.Codable
import com.lightningkite.khrysalis.IsCodable
import com.lightningkite.khrysalis.Image
import com.lightningkite.khrysalis.Uri
import com.lightningkite.khrysalis.bytes.Data
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Okio
import java.io.ByteArrayOutputStream


typealias HttpBody = RequestBody
typealias HttpBodyPart = MultipartBody.Part

fun IsCodable.toJsonHttpBody(): HttpBody {
    val sending = HttpClient.mapper.writeValueAsString(this)
    Log.i("HttpClient", "with body $sending")
    return RequestBody.create(HttpMediaTypes.JSON, sending)
}

fun Data.toHttpBody(mediaType: HttpMediaType): HttpBody {
    return RequestBody.create(mediaType, this)
}

fun String.toHttpBody(mediaType: HttpMediaType = HttpMediaTypes.TEXT): HttpBody {
    return RequestBody.create(mediaType, this)
}

fun Bitmap.toHttpBody(maxBytes: Long = 10_000_000): HttpBody {
    var qualityToTry = 100
    var data = ByteArrayOutputStream().use {
        this.compress(Bitmap.CompressFormat.JPEG, qualityToTry, it)
        it.toByteArray()
    }
    while (data.size > maxBytes) {
        qualityToTry -= 5
        data = ByteArrayOutputStream().use {
            this.compress(Bitmap.CompressFormat.JPEG, qualityToTry, it)
            it.toByteArray()
        }
    }
    return RequestBody.create(HttpMediaTypes.JPEG, data)
}

fun Uri.toHttpBody(type: HttpMediaType): HttpBody {
    return object : RequestBody() {
        override fun contentType(): MediaType = type

        override fun writeTo(sink: BufferedSink) {
            HttpClient.appContext.contentResolver.openInputStream(this@toHttpBody)?.use {
                Okio.source(it).use { source ->
                    sink.writeAll(source)
                }
            } ?: throw IllegalStateException("URI (${this@toHttpBody}) could not be opened")
        }

    }
}

fun multipartFormBody(vararg parts: HttpBodyPart): HttpBody {
    return MultipartBody.Builder().setType(MultipartBody.FORM).also {
        for (part in parts) {
            it.addPart(part)
        }
    }.build()
}

fun multipartFormFilePart(name: String, value: String): HttpBodyPart = HttpBodyPart.createFormData(name, value)
fun multipartFormFilePart(name: String, filename: String? = null, body: HttpBody): HttpBodyPart =
    HttpBodyPart.createFormData(name, filename, body)
