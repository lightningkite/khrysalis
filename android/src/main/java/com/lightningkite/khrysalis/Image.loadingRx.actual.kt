package com.lightningkite.khrysalis

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.lightningkite.khrysalis.net.HttpClient
import com.lightningkite.khrysalis.net.unsuccessfulAsError
import io.reactivex.Single
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min


/**
 *
 * Loads the image and return a Single<Bitmap>. This works for all the types of Images available.
 *
 */
fun Image.load(): Single<Bitmap> {
    return try {
        when (this) {
            is ImageRaw -> Single.just(BitmapFactory.decodeByteArray(this.raw, 0, this.raw.size))
            is ImageReference -> load()
            is ImageBitmap -> Single.just(this.bitmap)
            is ImageRemoteUrl -> load()
        }
    } catch (e: Exception) {
        Single.error(e)
    }
}

/**
 *
 * Loads the image and return a Single<Bitmap> from an ImageReference
 *
 */
fun ImageReference.load(maxDimension: Int = 2048): Single<Bitmap> {
    try {
        val finalOpts = BitmapFactory.Options()
        HttpClient.appContext.contentResolver.openInputStream(uri)?.use {
            val sizeOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(it, null, sizeOpts).apply {
                finalOpts.inSampleSize = max(
                    sizeOpts.outWidth.toDouble().div(maxDimension).let { Math.ceil(it) }.toInt(),
                    sizeOpts.outHeight.toDouble().div(maxDimension).let { Math.ceil(it) }.toInt()
                ).coerceAtLeast(1)
            }
        }
            ?: return Single.error(IllegalStateException("Context from HttpClient is missing; please set up HttpClient before attempting this."))
        HttpClient.appContext.contentResolver.openInputStream(uri)?.use {
            return Single.just(BitmapFactory.decodeStream(it, null, finalOpts))
        }
            ?: return Single.error(IllegalStateException("Context from HttpClient is missing; please set up HttpClient before attempting this."))
    } catch (e: Exception) {
        return Single.error(e)
    }
}

/**
 *
 * Loads the image and return a Single<Bitmap> from an ImageRemoteUrl
 *
 */
fun ImageRemoteUrl.load(): Single<Bitmap> {
    return HttpClient.call(url, HttpClient.GET, mapOf())
        .unsuccessfulAsError()
        .map { response ->
            response.body()?.use {
                it.byteStream().use {
                    BitmapFactory.decodeStream(it)
                }
            }
        }
}
