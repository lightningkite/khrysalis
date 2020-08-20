package com.lightningkite.khrysalis

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.lightningkite.khrysalis.net.HttpClient
import com.lightningkite.khrysalis.rx.forever
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
    image.load().subscribe { result, fail ->
        onResult(result)
    }.forever()
}

@Deprecated("Use Rx Style instead")
fun Image.load(onResult: (Bitmap?) -> Unit) {
    load().subscribe { result, fail ->
        onResult(result)
    }.forever()
}