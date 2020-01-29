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

/* SHARED DECLARATIONS
typealias Bitmap = Bitmap
 */

fun loadImage(image: Image, onResult: (Bitmap?) -> Unit) {
    try {
        when(image){
            is ImageRaw -> onResult(BitmapFactory.decodeByteArray(image.raw, 0, image.raw.size))
            is ImageReference -> loadImage(image.uri, onResult)
            is ImageBitmap -> onResult(image.bitmap)
            is ImageRemoteUrl -> loadImage(image.url, onResult)
        }
    } catch (e: Exception) {
        onResult(null)
    }
}

fun Image.load(onResult: (Bitmap?) -> Unit) = loadImage(this, onResult)

fun loadImage(uri: Uri, onResult: (Bitmap?) -> Unit) {
    onResult(MediaStore.Images.Media.getBitmap(HttpClient.appContext.contentResolver, uri))
}

fun loadImage(url: String, onResult: (Bitmap?) -> Unit) {
    if(url.isBlank()){
        onResult(null)
        return
    }
    val call = Request.Builder()
        .url(url)
        .get()
        .build()
    with(HttpClient){
        client.newCall(call).go(object: Callback {
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
                } catch(e:Exception){
                    e.printStackTrace()
                    onResult(null)
                }
            }
        })
    }
}
