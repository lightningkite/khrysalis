package com.lightningkite.khrysalis.views.draw

import android.graphics.Paint
import android.graphics.Shader
import com.lightningkite.khrysalis.views.geometry.GFloat

/* SHARED DECLARATIONS
class Paint(){
    var flags: Int
    var color: ColorValue
    var strokeWidth: GFloat
    var alpha: GFloat
    var style: Style
    var textSize: GFloat
    var shader: ShaderValue
    var isAntiAlias: Boolean
    var isFakeBoldText: Boolean

    enum class Style {
        FILL, STROKE, FILL_AND_STROKE
    }

    fun measureText(text: String): GFloat
}
 */

val Paint.textHeight: GFloat get() = fontMetrics.let { it.descent - it.ascent }
