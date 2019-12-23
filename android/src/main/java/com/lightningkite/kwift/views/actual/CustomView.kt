package com.lightningkite.kwift.views.actual

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeProvider
import android.widget.FrameLayout
import com.alamkanak.weekview.MonthLoader
import com.lightningkite.kwift.views.shared.CustomViewDelegate
import java.util.ArrayList

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var delegate: CustomViewDelegate? = null

    var accessibilityView: View? = null

    fun setup() {
        if((context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager).isEnabled) {
            accessibilityView = delegate?.generateAccessibilityView()
            accessibilityView?.let {
                addView(it, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
            }
        }
    }

    data class Touch(
        var x: Float,
        var y: Float,
        var id: Int
    )

    val touches = HashMap<Int, Touch>()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(accessibilityView != null) return super.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val pointerId = event.getPointerId(event.actionIndex)
                val touch = Touch(
                    x = event.getX(event.actionIndex),
                    y = event.getY(event.actionIndex),
                    id = pointerId
                )
                touches[pointerId] = touch
                delegate?.onTouchDown(touch.id, touch.x, touch.y, width.toFloat(), height.toFloat())
            }
            MotionEvent.ACTION_MOVE -> {
                for (pointerIndex in 0 until event.pointerCount) {
                    val pointerId = event.getPointerId(pointerIndex)
                    val touch = touches[pointerId]
                    if (touch != null) {
                        touch.x = event.getX(pointerIndex)
                        touch.y = event.getY(pointerIndex)
                        delegate?.onTouchMove(touch.id, touch.x, touch.y, width.toFloat(), height.toFloat())
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                val pointerId = event.getPointerId(event.actionIndex)
                val touch = touches.remove(pointerId)
                if (touch != null) {
                    delegate?.onTouchUp(touch.id, touch.x, touch.y, width.toFloat(), height.toFloat())
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(accessibilityView == null){
            delegate?.draw(canvas, width.toFloat(), height.toFloat())
        }
    }
}
