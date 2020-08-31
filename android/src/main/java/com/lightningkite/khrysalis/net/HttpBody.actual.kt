package com.lightningkite.khrysalis.net

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.lightningkite.khrysalis.*
import com.lightningkite.khrysalis.bytes.Data
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Okio
import java.io.ByteArrayOutputStream
import java.io.File


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

fun Image.toHttpBody(maxDimension: Int = 2048): Single<HttpBody> = Single.create { em ->
    val glide = Glide.with(HttpClient.appContext).asBitmap()
    val task = when(this){
        is ImageReference -> glide.load(this.uri)
        is ImageBitmap -> glide.load(this.bitmap)
        is ImageRaw -> glide.load(this.raw)
        is ImageRemoteUrl -> glide.load(this.url)
        is ImageResource -> glide.load(this.resource)
    }
    task
        .addListener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                e?.printStackTrace() ?: Exception().printStackTrace()
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

        })
        .into(object: CustomTarget<Bitmap>(){
        override fun onLoadCleared(placeholder: Drawable?) {}
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            em.onSuccess(resource.toHttpBody())
        }
        override fun onLoadFailed(errorDrawable: Drawable?) {
            em.onError(Exception())
        }
    })
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

fun Uri.toHttpBody(): HttpBody {
    val type = HttpMediaTypes.fromString(HttpClient.appContext.contentResolver.getType(this) ?: "*/*")
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
