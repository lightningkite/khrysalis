package com.lightningkite.khrysalis

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.lightningkite.khrysalis.net.HttpClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

/* SHARED DECLARATIONS
typealias Bitmap = Bitmap
 */

@Deprecated("Use Rx Style instead")
fun loadImage(image: Image, onResult: (Bitmap?) -> Unit) {
    try {
        when (image) {
            is ImageRaw -> onResult(BitmapFactory.decodeByteArray(image.raw, 0, image.raw.size))
            is ImageReference -> loadImage(image.uri, onResult = onResult)
            is ImageBitmap -> onResult(image.bitmap)
            is ImageRemoteUrl -> loadImage(image.url, onResult = onResult)
        }
    } catch (e: Exception) {
        onResult(null)
    }
}

@Deprecated("Use Rx Style instead")
fun Image.load(onResult: (Bitmap?) -> Unit) = loadImage(this, onResult)

@Deprecated("Use Rx Style instead")
fun loadImage(uri: Uri, maxDimension: Int = 2048, onResult: (Bitmap?) -> Unit) {
    var result: Bitmap? = null
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
        HttpClient.appContext.contentResolver.openInputStream(uri)?.use {
            result = BitmapFactory.decodeStream(it, null, finalOpts)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    onResult(result)
}

@Deprecated("Use Rx Style instead")
fun loadImage(url: String, onResult: (Bitmap?) -> Unit) {
    if (url.isBlank()) {
        onResult(null)
        return
    }
    val call = Request.Builder()
        .url(url)
        .get()
        .build()
    with(HttpClient) {
        client.newCall(call).go(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    response.body()?.use {
                        it.byteStream().use {
                            onResult(BitmapFactory.decodeStream(it))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    onResult(null)
                }
            }
        })
    }
}
