package com.lightningkite.khrysalis

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import com.lightningkite.khrysalis.net.HttpClient
import com.lightningkite.khrysalis.net.unsuccessfulAsError
import io.reactivex.Single
import kotlin.math.max


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
            is ImageResource -> {
                val drawable = HttpClient.appContext.resources.getDrawable(resource)
                if(drawable is BitmapDrawable){
                    Single.just(drawable.bitmap)
                } else {
                    val bitmap = Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                    Single.just(bitmap)
                }
            }
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
private fun ImageReference.load(maxDimension: Int = 2048): Single<Bitmap> {
    try {
        val finalOpts = BitmapFactory.Options()
        HttpClient.appContext.contentResolver.openInputStream(uri)?.use {
            val sizeOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(it, null, sizeOpts)?.apply {
                finalOpts.inSampleSize = max(
                    sizeOpts.outWidth.toDouble().div(maxDimension).let { Math.ceil(it) }.toInt(),
                    sizeOpts.outHeight.toDouble().div(maxDimension).let { Math.ceil(it) }.toInt()
                ).coerceAtLeast(1)
            }
            Unit
        }
            ?: return Single.error(IllegalStateException("Could not find file '$uri'."))
        HttpClient.appContext.contentResolver.openInputStream(uri)?.use {
            return Single.just(
                BitmapFactory.decodeStream(it, null, finalOpts) ?: return Single.error(
                    IllegalStateException("File '$uri' could not be parsed, got null.  Tried with options $finalOpts")
                )
            )
        }
            ?: return Single.error(IllegalStateException("Could not find file '$uri', but found it earlier. Huh?"))
    } catch (e: Exception) {
        return Single.error(e)
    }
}

/**
 *
 * Loads the image and return a Single<Bitmap> from an ImageRemoteUrl
 *
 */
private fun ImageRemoteUrl.load(): Single<Bitmap> {
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
