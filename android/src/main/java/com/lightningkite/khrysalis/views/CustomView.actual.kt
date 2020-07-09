package com.lightningkite.khrysalis.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.accessibility.AccessibilityManager
import android.widget.FrameLayout
import com.lightningkite.khrysalis.PlatformSpecific
import com.lightningkite.khrysalis.R
import com.lightningkite.khrysalis.rx.DisposableLambda
import com.lightningkite.khrysalis.rx.removed
import kotlin.math.min

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init{
        setWillNotDraw(false)
        this.removed.call(DisposableLambda {
            this.delegate = null
        })
    }

    var delegate: CustomViewDelegate? = null
        set(value) {
            field?.let {
                it.customView = null
                it.dispose()
            }
            if(value != null) {
                value.customView = this
            }
            field = value

            if ((context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager).isEnabled) {
                accessibilityView = delegate?.generateAccessibilityView()
                accessibilityView?.let {
                    addView(it, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
                }
            }
        }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomView, defStyleAttr, 0)
        a.getString(R.styleable.CustomView_delegateClass)?.let { delegateClassName ->
            delegate = try {
                context::class.java.classLoader!!
                    .loadClass(delegateClassName)
                    .newInstance() // instantiate using 0-args constructor
                    .let { it as CustomViewDelegate }
            } catch(e:ClassNotFoundException){
                this::class.java.classLoader!!
                    .loadClass(delegateClassName)
                    .newInstance() // instantiate using 0-args constructor
                    .let { it as CustomViewDelegate }
            }
        }
    }

    var accessibilityView: View? = null

    private data class Touch(
        var x: Float,
        var y: Float,
        var id: Int
    )

    @SuppressLint("UseSparseArrays")
    private val touches = HashMap<Int, Touch>()

    @PlatformSpecific
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (accessibilityView != null) return super.onTouchEvent(event)
        var takenCareOf = false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val pointerId = event.getPointerId(event.actionIndex)
                val touch = Touch(
                    x = event.getX(event.actionIndex),
                    y = event.getY(event.actionIndex),
                    id = pointerId
                )
                touches[pointerId] = touch
                takenCareOf = (delegate?.onTouchDown(touch.id, touch.x, touch.y, width.toFloat(), height.toFloat()) ?: false) || takenCareOf
            }
            MotionEvent.ACTION_MOVE -> {
                for (pointerIndex in 0 until event.pointerCount) {
                    val pointerId = event.getPointerId(pointerIndex)
                    val touch = touches[pointerId]
                    if (touch != null) {
                        touch.x = event.getX(pointerIndex)
                        touch.y = event.getY(pointerIndex)
                        takenCareOf = (delegate?.onTouchMove(touch.id, touch.x, touch.y, width.toFloat(), height.toFloat()) ?: false) || takenCareOf
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                val pointerId = event.getPointerId(event.actionIndex)
                val touch = touches.remove(pointerId)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val pointerId = event.getPointerId(event.actionIndex)
                val touch = touches.remove(pointerId)
                if (touch != null) {
                    takenCareOf = (delegate?.onTouchUp(touch.id, touch.x, touch.y, width.toFloat(), height.toFloat()) ?: false) || takenCareOf
                }
            }
        }
        return takenCareOf
    }

    private val metrics = context.resources.displayMetrics
    @PlatformSpecific
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (accessibilityView == null) {
            delegate?.draw(canvas, width.toFloat(), height.toFloat(), metrics)
        }
    }
    @PlatformSpecific
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            else -> min(delegate?.sizeThatFitsWidth(widthSize.toFloat(), heightSize.toFloat())?.toInt() ?: widthSize, widthSize)
        }
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            else -> min(delegate?.sizeThatFitsHeight(width.toFloat(), heightSize.toFloat())?.toInt() ?: heightSize, heightSize)
        }
        setMeasuredDimension(
            width,
            height
        )
    }
}
