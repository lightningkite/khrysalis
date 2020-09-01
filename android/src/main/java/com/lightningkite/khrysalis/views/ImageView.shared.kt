package com.lightningkite.khrysalis.views

import android.media.MediaMetadataRetriever
import android.widget.ImageView
import com.lightningkite.khrysalis.Video
import com.lightningkite.khrysalis.VideoReference
import com.lightningkite.khrysalis.VideoRemoteUrl
import com.lightningkite.khrysalis.rx.forever
import com.lightningkite.khrysalis.thumbnail
import io.reactivex.rxkotlin.subscribeBy


/**
 *
 * Loads a thumbnail from the video into the imageview the function is called on.
 * Video can be from a local reference or a URL.
 *
 */
fun ImageView.loadVideoThumbnail(video: Video?) {
    if (video == null) return
    loadImage(null)
    video.thumbnail().subscribeBy(
        onError = {
            loadImage(null)
        },
        onSuccess = {
            loadImage(it)
        }
    ).forever()
}