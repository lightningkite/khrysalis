package com.lightningkite.kwift.observables.actual

import android.provider.MediaStore
import android.widget.ImageView
import com.lightningkite.kwift.actual.ImageData
import com.lightningkite.kwift.actual.ImageReference
import com.lightningkite.kwift.observables.shared.ObservableProperty
import com.lightningkite.kwift.observables.shared.addAndRunWeak
import com.squareup.picasso.Picasso


fun ImageView.loadUrl(imageUrl: String?) {
    post {
        if (imageUrl != null && imageUrl.isNotBlank()) {
            Picasso.get()
                .load(imageUrl)
                .resize(width.coerceAtLeast(100), height.coerceAtLeast(100))
                .centerCrop()
                .into(this)
        } else {
            this.setImageDrawable(null)
        }
    }
}

fun ImageView.loadUrl(imageUrl: ObservableProperty<String?>) {
    post {
        imageUrl.addAndRunWeak(this) { self, it ->
            if (it != null && it.isNotBlank()) {
                Picasso.get()
                    .load(it)
                    .resize(self.width.coerceAtLeast(100), self.height.coerceAtLeast(100))
                    .centerCrop()
                    .into(self)
            } else {
                this.setImageDrawable(null)
            }
        }
    }
}

fun ImageView.loadImageData(imageData: ObservableProperty<ImageData?>) {
    post {
        imageData.addAndRunWeak(this) { self, it ->
            it?.let {
                self.setImageBitmap(it)
            }
            if (it == null) {
                setImageDrawable(null)
            }
        }
    }
}

fun ImageView.loadImageReference(imageReference: ObservableProperty<ImageReference?>) {
    post {
        imageReference.addAndRunWeak(this) { self, it ->
            it?.let {
                self.setImageBitmap(MediaStore.Images.Media.getBitmap(context.contentResolver, it))
            }
            if (it == null) {
                setImageDrawable(null)
            }
        }
    }
}

fun ImageView.loadUrlNotNull(imageUrl: ObservableProperty<String>) {
    post {
        imageUrl.addAndRunWeak(this) { self, it ->
            if (it.isNotBlank()) {
                Picasso.get()
                    .load(it)
                    .resize(self.width.coerceAtLeast(100), self.height.coerceAtLeast(100))
                    .centerCrop()
                    .into(self)
            }
        }
    }
}
