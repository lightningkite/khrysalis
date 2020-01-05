package com.lightningkite.kwift.views.draw

import android.graphics.Paint
import android.graphics.Shader

/* SHARED DECLARATIONS
class Paint(){
    var flags: Int
    var color: ColorValue
    var strokeWidth: Float
    var alpha: Float
    var style: Style
    var textSize: Float
    var shader: ShaderValue
    var isAntiAlias: Boolean
    var isFakeBoldText: Boolean

    enum class Style {
        FILL, STROKE, FILL_AND_STROKE
    }

    fun measureText(text: String): Float
}
 */

val Paint.textHeight: Float get() = fontMetrics.let { it.descent - it.ascent }
