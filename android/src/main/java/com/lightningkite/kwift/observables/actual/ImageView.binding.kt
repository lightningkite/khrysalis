package com.lightningkite.kwift.observables.actual

import android.widget.ImageView
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
