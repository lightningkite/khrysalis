package com.lightningkite.kwift.shared

import android.graphics.Bitmap
import android.net.Uri

sealed class Image
data class ImageReference(val uri: Uri): Image()
data class ImageBitmap(val bitmap: Bitmap): Image()
data class ImageRaw(val raw: ByteArray): Image()
data class ImageRemoteUrl(val url: String): Image()

fun String.asImage(): Image = ImageRemoteUrl(this)
fun Uri.asImage(): Image = ImageReference(this)
fun Bitmap.asImage(): Image = ImageBitmap(this)
