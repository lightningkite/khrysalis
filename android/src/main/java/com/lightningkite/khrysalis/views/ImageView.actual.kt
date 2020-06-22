package com.lightningkite.khrysalis.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.util.Size
import android.widget.ImageView
import com.lightningkite.khrysalis.*
import com.lightningkite.khrysalis.net.HttpClient
import com.squareup.picasso.Picasso
import java.io.IOException


/**
 *
 * Loads the image into the imageview the function is called on.
 * An image can be from multiple sources, such as the web, an android image reference,
 * and a direct bitmap. It will handle all cases and load the image.
 *
 */
fun ImageView.loadImage(image: Image?) {
    post {
        image?.let { image ->
            when (image) {
                is ImageRaw -> this.setImageBitmap(BitmapFactory.decodeByteArray(image.raw, 0, image.raw.size))
                is ImageReference -> this.setImageBitmap(
                    MediaStore.Images.Media.getBitmap(
                        HttpClient.appContext.contentResolver,
                        image.uri
                    )
                )
                is ImageBitmap -> this.setImageBitmap(image.bitmap)
                is ImageRemoteUrl -> {
                    if (image.url.isNotBlank() && this.width > 0 && this.height > 0) {
                        Picasso.get().load(image.url).resize(this.width * 2, this.height * 2).centerInside().into(this)
                    }
                }
            }
        }
        if (image == null) {
            this.setImageDrawable(null)
        }
    }
}

inline fun ImageView.loadImageAlt(image: Image?) = loadImage(image)


/**
 *
 * Loads a thumbnail from the video into the imageview the function is called on.
 * Video can be from a local reference or a URL.
 *
 */
fun ImageView.loadVideoThumbnail(video: Video?) {
    when (video) {
        is VideoReference -> {
            try {

                val mMMR = MediaMetadataRetriever()
                mMMR.setDataSource(context, video.uri)
                if(this.width > 0 && this.height > 0) {
                    this.setImageBitmap(
                        mMMR.getScaledFrameAtTime(
                            2000000,
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                            this.width,
                            this.height
                        )
                    )
                }else{
                    this.setImageBitmap(mMMR.frameAtTime)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showDialog(ViewStringRaw(e.message ?: "An Unknown Error Occurred"))
            }
        }
        is VideoRemoteUrl -> {
            this.setImageBitmap(retrieveVideoFrameFromVideo(video.url))
        }
        else -> {
            this.setImageDrawable(null)
        }
    }
}

fun retrieveVideoFrameFromVideo(videoPath: String?): Bitmap? {
    var mediaMetadataRetriever: MediaMetadataRetriever? = null
    try {
        mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(videoPath, HashMap<String, String>())
        return mediaMetadataRetriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        mediaMetadataRetriever?.release()
    }
    return null
}

//class VideoRequestHandler : RequestHandler() {
//    var SCHEME_VIDEO = "video"
//    override fun canHandleRequest(data: Request): Boolean {
//        val scheme: String? = data.uri.scheme
//        return SCHEME_VIDEO == scheme
//    }
//
//    @Throws(IOException::class)
//    override fun load(data: Request, arg1: Int): Result {
//        val bm: Bitmap = ThumbnailUtils.createVideoThumbnail(data.uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND)
//        return Result(bm, LoadedFrom.DISK)
//    }
//}