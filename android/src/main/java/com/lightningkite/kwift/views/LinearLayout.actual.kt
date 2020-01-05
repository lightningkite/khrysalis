package com.lightningkite.kwift.views

import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import com.lightningkite.kwift.views.geometry.Align
import com.lightningkite.kwift.views.geometry.AlignPair

fun LinearLayout.params(
    sizeX: Int = 0,
    sizeY: Int = 0,
    marginStart: Int = 0,
    marginEnd: Int = 0,
    marginTop: Int = 0,
    marginBottom: Int = 0,
    gravity: AlignPair = AlignPair.center,
    weight: Float = 0f
): LinearLayout.LayoutParams {
    val d = resources.displayMetrics.density
    val align = if(orientation == LinearLayout.HORIZONTAL) gravity.vertical else gravity.horizontal

    return if (orientation == LinearLayout.HORIZONTAL) {
        LinearLayout.LayoutParams(
            if (weight != 0f) 0 else if(sizeX == 0) WRAP_CONTENT else (sizeX * d).toInt(),
            if (align == Align.fill) MATCH_PARENT else if (sizeY == 0) WRAP_CONTENT else (sizeY * d).toInt(),
            weight
        )
    } else {
        LinearLayout.LayoutParams(
            if (align == Align.fill) MATCH_PARENT else if(sizeX == 0) WRAP_CONTENT else (sizeX * d).toInt(),
            if (weight != 0f) 0 else if (sizeY == 0) WRAP_CONTENT else (sizeY * d).toInt(),
            weight
        )
    }.apply {
        this.gravity = when (align) {
            Align.start -> Gravity.TOP or Gravity.START
            Align.center -> Gravity.CENTER
            Align.end -> Gravity.BOTTOM or Gravity.END
            Align.fill -> Gravity.CENTER
        }
        setMargins(
            (d * marginStart).toInt(),
            (d * marginTop).toInt(),
            (d * marginEnd).toInt(),
            (d * marginBottom).toInt()
        )
    }
}
