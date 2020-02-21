package com.lightningkite.khrysalis.views


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.provider.MediaStore
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.lightningkite.khrysalis.Uri
import com.lightningkite.khrysalis.android.ActivityAccess
import com.lightningkite.khrysalis.location.GeoCoordinate
import com.lightningkite.khrysalis.views.android.startIntent
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File


typealias ViewDependency = ActivityAccess

fun ViewDependency.getString(resource: StringResource): String = context.getString(resource)
fun ViewDependency.getColor(resource: ColorResource): Int = context.resources.getColor(resource)
val ViewDependency.displayMetrics: DisplayMetrics get() = context.resources.displayMetrics

fun ViewDependency.share(subject: String, message: String){
    val i = Intent(Intent.ACTION_SEND)
    i.type = "text/plain"
    i.putExtra(Intent.EXTRA_SUBJECT, subject)
    i.putExtra(Intent.EXTRA_TEXT, message)
    context.startActivity(Intent.createChooser(i, subject))
}

fun ViewDependency.openUrl(url: String) {
    startIntent(
        intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
    )
}

fun ViewDependency.openMap(coordinate: GeoCoordinate, label: String? = null, zoom: Float? = null) {
    startIntent(
        intent = Intent(Intent.ACTION_VIEW).apply {
            if(label == null){
                if(zoom == null){
                    data = Uri.parse("geo:${coordinate.latitude},${coordinate.longitude}")
                } else {
                    data = Uri.parse("geo:${coordinate.latitude},${coordinate.longitude}?z=$zoom")
                }
            } else {
                if(zoom == null){
                    data = Uri.parse("geo:${coordinate.latitude},${coordinate.longitude}?q=${Uri.encode(label)}")
                } else {
                    data = Uri.parse("geo:${coordinate.latitude},${coordinate.longitude}?q=${Uri.encode(label)}&z=$zoom")
                }
            }
        }
    )
}

fun ViewDependency.requestImageGallery(
    callback: (Uri) -> Unit
) {
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                val uri = result?.data
                if (code == Activity.RESULT_OK && uri != null) {
                    callback(uri)
                }
            }
        }
    }
}

fun ViewDependency.requestImageCamera(
    callback: (Uri) -> Unit
) {
    val fileProviderAuthority = context.packageName + ".fileprovider"
    val file = File(context.cacheDir, "images").also { it.mkdirs() }
        .let { File.createTempFile("image", ".jpg", it) }
        .let { FileProvider.getUriForFile(context, fileProviderAuthority, it) }
    requestPermission(Manifest.permission.CAMERA) {
        if (it) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
            startIntent(intent) { code, result ->
                if (code == Activity.RESULT_OK) callback(result?.data ?: file)
            }
        }
    }
}


fun ViewDependency.downloadDrawable(
    url: String,
    width: Int? = null,
    height: Int? = null,
    onResult: (Drawable?) -> Unit
) {
    Picasso.get()
        .load(url)
        .let {
            if (width == null || height == null) it
            else it.resize(width.coerceAtLeast(100), height.coerceAtLeast(100)).centerCrop()
        }
        .into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                onResult(null)
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                onResult(BitmapDrawable(bitmap))
            }
        })
}

fun ViewDependency.checkedDrawable(
    checked: Drawable,
    normal: Drawable
) = StateListDrawable().apply {
    addState(intArrayOf(android.R.attr.state_checked), checked)
    addState(intArrayOf(), normal)
}

fun ViewDependency.setSizeDrawable(drawable: Drawable, width: Int, height: Int): Drawable {
    val scale = context.resources.displayMetrics.density
    return object : LayerDrawable(arrayOf(drawable)) {
        override fun getIntrinsicWidth(): Int = (width * scale).toInt()
        override fun getIntrinsicHeight(): Int = (height * scale).toInt()
    }
}
