package com.lightningkite.kwift.views.android

//Original: https://github.com/hyogeunpark/ColorRatingBar

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.lightningkite.kwift.R

/**
 * Created by hyogeun.park on 2017. 6. 1..
 */

class ColorRatingBar : AppCompatRatingBar {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ColorRatingBar, defStyleAttr, 0)
        val progressColor =
            a.getColor(R.styleable.ColorRatingBar_progress_color, Color.YELLOW)
        val emptyColor =
            a.getColor(R.styleable.ColorRatingBar_empty_color, Color.LTGRAY)
        val changeable = a.getBoolean(R.styleable.ColorRatingBar_changeable, true)

        val stars = progressDrawable as LayerDrawable
        // Filled stars
        setRatingStarColor(DrawableCompat.wrap(stars.getDrawable(2)), progressColor)
        // Half filled stars
        setRatingStarColor(DrawableCompat.wrap(stars.getDrawable(1)), Color.TRANSPARENT)
        // Empty stars
        setRatingStarColor(DrawableCompat.wrap(stars.getDrawable(0)), emptyColor)

        setIsIndicator(!changeable)
    }

    private fun setRatingStarColor(drawable: Drawable, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(drawable, color)
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }
}
