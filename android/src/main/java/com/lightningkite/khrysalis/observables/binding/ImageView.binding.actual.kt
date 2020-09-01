package com.lightningkite.khrysalis.observables.binding

import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.widget.ImageView
import com.lightningkite.khrysalis.net.HttpClient
import com.lightningkite.khrysalis.*
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.loadImage
import com.lightningkite.khrysalis.views.loadVideoThumbnail

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
            this.loadImage(it)
        }
    }.until(this.removed)
}

fun ImageView.bindVideoThumbnail(video: ObservableProperty<Video?>) {
    video.subscribeBy {
        post {
            this.loadVideoThumbnail(it)
        }
    }.until(removed)
}