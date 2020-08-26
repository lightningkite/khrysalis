package com.lightningkite.khrysalis.observables.binding

import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.widget.ImageView
import com.lightningkite.khrysalis.net.HttpClient
import com.lightningkite.khrysalis.*
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.loadImageAlt
import com.lightningkite.khrysalis.views.loadVideoThumbnail


/**
 *
 * Loads the image into the imageview the function is called on.
 * An image can be from multiple sources, such as the web, an android image reference,
 * and a direct bitmap. It will handle all cases and load the image.
 *
 */
@Deprecated(
    "Use the one in the 'views' package instead.",
    ReplaceWith("this.loadImage(image)", "com.lightningkite.khrysalis.views.loadImage")
)
fun ImageView.loadImage(image: Image?) = loadImageAlt(image)


/**
 *
 * Binds the imageview internal image to the image provided by the observable.
 * Any changes to the observable will cause a reload of the image to match the change.
 * An image can be from multiple sources, such as the web, an android image reference,
 * and a direct bitmap. It will handle all cases and load the image.
 *
 */
fun ImageView.bindImage(image: ObservableProperty<Image?>) {
    image.subscribeBy { it ->
        post {
            this.loadImageAlt(it)
        }
    }.until(this.removed)
}

fun ImageView.bindVideoThumbnail(video: ObservableProperty<Video?>) {
    video.subscribeBy {
        this.loadVideoThumbnail(it)
    }.until(removed)
}