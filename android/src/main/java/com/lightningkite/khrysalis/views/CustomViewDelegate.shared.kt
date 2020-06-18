package com.lightningkite.khrysalis.views

import android.graphics.Canvas
import android.util.DisplayMetrics
import android.view.View
import com.lightningkite.khrysalis.rx.DisposeCondition
import com.lightningkite.khrysalis.weak
import com.lightningkite.khrysalis.views.CustomView
import io.reactivex.disposables.Disposable

abstract class CustomViewDelegate {
    var customView: CustomView? = null
    abstract fun generateAccessibilityView(): View?
    abstract fun draw(canvas: Canvas, width: Float, height: Float, displayMetrics: DisplayMetrics)
    open fun onTouchDown(id: Int, x: Float, y: Float, width: Float, height: Float): Boolean = false
    open fun onTouchMove(id: Int, x: Float, y: Float, width: Float, height: Float): Boolean = false
    open fun onTouchCancelled(id: Int, x: Float, y: Float, width: Float, height: Float): Boolean = false
    open fun onTouchUp(id: Int, x: Float, y: Float, width: Float, height: Float): Boolean = false
    open fun sizeThatFitsWidth(width: Float, height: Float): Float = width
    open fun sizeThatFitsHeight(width: Float, height: Float): Float = height

    fun invalidate() { customView?.invalidate() }
    fun postInvalidate() { customView?.postInvalidate() }

    val toDispose: ArrayList<Disposable> = ArrayList()
    val removed: DisposeCondition = DisposeCondition { it -> toDispose.add(it) }
    fun dispose() {
        for(item in toDispose){
            item.dispose()
        }
    }
}
