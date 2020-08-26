package com.lightningkite.khrysalis.views


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.provider.CalendarContract
import android.provider.MediaStore
import android.util.DisplayMetrics
import androidx.core.content.FileProvider
import com.lightningkite.khrysalis.*
import com.lightningkite.khrysalis.android.ActivityAccess
import com.lightningkite.khrysalis.location.GeoCoordinate
import com.lightningkite.khrysalis.views.android.startIntent
import java.io.File
import java.util.*


typealias ViewDependency = ActivityAccess

fun ViewDependency.getString(resource: StringResource): String = context.getString(resource)
fun ViewDependency.getColor(resource: ColorResource): Int = context.resources.getColor(resource)
val ViewDependency.displayMetrics: DisplayMetrics get() = context.resources.displayMetrics

fun ViewDependency.share(shareTitle: String, message: String? = null, url: String? = null, image: Image? = null) {
    val i = Intent(Intent.ACTION_SEND)
    i.type = "text/plain"
    listOfNotNull(message, url).joinToString("\n").takeUnless { it == null }?.let { i.putExtra(Intent.EXTRA_TEXT, it) }
    if (image != null) {
        when (image) {
            is ImageReference -> {
                i.setType("image/jpeg")
                i.putExtra(Intent.EXTRA_STREAM, image.uri)
            }
            is ImageRemoteUrl -> {
                i.setType("image/jpeg")
                i.putExtra(Intent.EXTRA_STREAM, Uri.parse(image.url))
            }

        }
    }
    context.startActivity(Intent.createChooser(i, shareTitle))
}

fun ViewDependency.openUrl(url: String): Boolean {
    val mgr = context.packageManager
    val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
    val list = mgr.queryIntentActivities(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    )
    return if (list.size > 0) {
        startIntent(intent = intent)
        true
    } else {
        false
    }
}

fun ViewDependency.openAndroidAppOrStore(packageName: String) {
    val mgr = context.packageManager
    val intent = mgr.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        startIntent(intent = intent)
    } else {
        openUrl("market://details?id=$packageName")
    }
}

fun ViewDependency.openIosStore(numberId: String) {
    openUrl("https://apps.apple.com/us/app/taxbot/id$numberId")
}

fun ViewDependency.openMap(coordinate: GeoCoordinate, label: String? = null, zoom: Float? = null) {
    startIntent(
        intent = Intent(Intent.ACTION_VIEW).apply {
            if (label == null) {
                if (zoom == null) {
                    data = Uri.parse("geo:${coordinate.latitude},${coordinate.longitude}")
                } else {
                    data = Uri.parse("geo:${coordinate.latitude},${coordinate.longitude}?z=$zoom")
                }
            } else {
                if (zoom == null) {
                    data = Uri.parse("geo:${coordinate.latitude},${coordinate.longitude}?q=${Uri.encode(label)}")
                } else {
                    data =
                        Uri.parse("geo:${coordinate.latitude},${coordinate.longitude}?q=${Uri.encode(label)}&z=$zoom")
                }
            }
        }
    )
}

fun ViewDependency.openEvent(title: String, description: String, location: String, start: Date, end: Date) {
    startIntent(
        intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.DESCRIPTION, description)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.time)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.time)
            putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        }
    )
}

fun ViewDependency.requestImagesGallery(
    callback: (List<Uri>) -> Unit
) {
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) { hasPermission ->
        if (hasPermission) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"
            getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                if (code == Activity.RESULT_OK) {
                    result?.clipData?.let { clipData ->
                        callback((0 until clipData.itemCount).map { index -> clipData.getItemAt(index).uri })
                    } ?: result?.data?.let { callback(listOf(it)) }
                }
            }
        }
    }
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
    front: Boolean = false,
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
            //TODO:Test this on an older device. This works on newest, but we need to make sure it works/doesn't crash a newer one.
            if (front) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
//                }else{
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1)

//                }
                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            }
            startIntent(intent) { code, result ->
                if (code == Activity.RESULT_OK) callback(result?.data ?: file)
            }
        }
    }
}


fun ViewDependency.requestVideoGallery(
    callback: (Uri) -> Unit
) {
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "video/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "video/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Video")
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

fun ViewDependency.requestVideosGallery(
    callback: (List<Uri>) -> Unit
) {
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "video/*"
            getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "video/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Video")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                if (code == Activity.RESULT_OK) {
                    result?.clipData?.let { clipData ->
                        callback((0 until clipData.itemCount).map { index -> clipData.getItemAt(index).uri })
                    } ?: result?.data?.let { callback(listOf(it)) }
                }
            }
        }
    }
}


fun ViewDependency.requestVideoCamera(
    front: Boolean = false,
    callback: (Uri) -> Unit
) {
    val fileProviderAuthority = context.packageName + ".fileprovider"
    val file = File(context.cacheDir, "videos").also { it.mkdirs() }
        .let { File.createTempFile("video", ".mp4", it) }
        .let { FileProvider.getUriForFile(context, fileProviderAuthority, it) }
    requestPermission(Manifest.permission.CAMERA) {
        if (it) {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
            //TODO:Test this on an older device. This works on newest, but we need to make sure it works/doesn't crash a newer one.
            if (front) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
//                }else{
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1)

//                }
                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            }
            startIntent(intent) { code, result ->
                if (code == Activity.RESULT_OK) callback(result?.data ?: file)
            }
        }
    }
}


fun ViewDependency.requestMediasGallery(
    callback: (List<Uri>) -> Unit
) {
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "video/* image/*"
            getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "video/* image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Media")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                if (code == Activity.RESULT_OK) {
                    result?.clipData?.let { clipData ->
                        callback((0 until clipData.itemCount).map { index -> clipData.getItemAt(index).uri })
                    } ?: result?.data?.let { callback(listOf(it)) }
                }
            }
        }
    }
}
fun ViewDependency.requestMediaGallery(
    callback: (Uri) -> Unit
) {
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "video/* image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "video/* image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Media")
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


@Deprecated("")
fun ViewDependency.downloadDrawable(
    url: String,
    width: Int? = null,
    height: Int? = null,
    onResult: (Drawable?) -> Unit
) {
    fatalError("deprecated")
}

@Deprecated("")
fun ViewDependency.checkedDrawable(
    checked: Drawable,
    normal: Drawable
) = StateListDrawable().apply {
    addState(intArrayOf(android.R.attr.state_checked), checked)
    addState(intArrayOf(), normal)
}

@Deprecated("")
fun ViewDependency.setSizeDrawable(drawable: Drawable, width: Int, height: Int): Drawable {
    val scale = context.resources.displayMetrics.density
    return object : LayerDrawable(arrayOf(drawable)) {
        override fun getIntrinsicWidth(): Int = (width * scale).toInt()
        override fun getIntrinsicHeight(): Int = (height * scale).toInt()
    }
}
