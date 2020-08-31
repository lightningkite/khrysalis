package com.lightningkite.khrysalis

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
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
                if (drawable is BitmapDrawable) {
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
    return Single.create { emitter ->
        Glide.with(HttpClient.appContext)
            .asBitmap()
            .load(this.uri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    emitter.onError(Exception("Failed to load drawable"))
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    emitter.onSuccess(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    emitter.onError(Exception())
                }
            })
    }
}

/**
 *
 * Loads the image and return a Single<Bitmap> from an ImageRemoteUrl
 *
 */
private fun ImageRemoteUrl.load(): Single<Bitmap> {
    return Single.create { emitter ->

        Glide.with(HttpClient.appContext)
            .asBitmap()
            .load(this.url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    emitter.onError(Exception("Failed to load drawable"))
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    emitter.onSuccess(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    emitter.onError(Exception())
                }
            })
    }
}
