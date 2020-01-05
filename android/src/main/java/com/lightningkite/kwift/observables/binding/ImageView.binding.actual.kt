package com.lightningkite.kwift.observables.binding

import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.widget.ImageView
import com.lightningkite.kwift.net.HttpClient
import com.lightningkite.kwift.*
import com.squareup.picasso.Picasso
import com.lightningkite.kwift.observables.*


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
                    if (image.url.isNotBlank()) {
                        Picasso.get().load(image.url).into(this)
                    }
                }
            }
        }
        if (image == null) {
            this.setImageDrawable(null)
        }
    }
}

fun ImageView.bindImage(image: ObservableProperty<Image?>) {
    post {
        image.addAndRunWeak(this) { self, it ->
            self.loadImage(it)
        }
    }
}
