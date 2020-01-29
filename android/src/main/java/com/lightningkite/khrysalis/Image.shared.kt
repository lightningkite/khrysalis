package com.lightningkite.khrysalis

import android.graphics.Bitmap
import android.net.Uri
import com.lightningkite.khrysalis.bytes.Data

sealed class Image
data class ImageReference(val uri: Uri): Image()
data class ImageBitmap(val bitmap: Bitmap): Image()
data class ImageRaw(val raw: Data): Image()
data class ImageRemoteUrl(val url: String): Image()

fun String.asImage(): Image = ImageRemoteUrl(this)
fun Uri.asImage(): Image = ImageReference(this)
fun Bitmap.asImage(): Image = ImageBitmap(this)
