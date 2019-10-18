package com.lightningkite.kwift.views.actual

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

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
