package com.lightningkite.khrysalis

import android.graphics.Bitmap
import android.net.Uri
import com.lightningkite.khrysalis.bytes.Data

/**
 *
 * Image is a way to consolidate all the ways an image is described, handled, or created in the app.
 * These are Uri references, a remote URL, a bitmap and a Raw Byte Array.
 *
 */

sealed class Video
data class VideoReference(val uri: Uri): Video()
@JsName("ImageImageBitmap")
//data class VideoRaw(val raw: Data): Video()
data class VideoRemoteUrl(val url: String): Video()

fun String.asVideo(): Video = VideoRemoteUrl(this)
fun Uri.asVideo(): Video = VideoReference(this)
