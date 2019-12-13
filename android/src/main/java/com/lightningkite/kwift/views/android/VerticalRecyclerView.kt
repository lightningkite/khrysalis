package com.lightningkite.kwift.views.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lightningkite.kwift.R
import com.lightningkite.kwift.observables.shared.StandardObservableProperty
import com.lightningkite.kwift.observables.shared.addAndRunWeak
import java.text.SimpleDateFormat
import java.util.*

class VerticalRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    companion object {
        const val FLAG_DIVIDER_START = 0x1
        const val FLAG_DIVIDER_BETWEEN = 0x2
        const val FLAG_DIVIDER_END = 0x4
    }

    init {
        layoutManager = LinearLayoutManager(context)
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.VerticalRecyclerView, defStyleAttr, 0)
            val dividerPositions = ta.getInt(R.styleable.VerticalRecyclerView_dividerPositions, 0)
            val dividerColor = ta.getColor(R.styleable.VerticalRecyclerView_dividerColor, 0xFF808080.toInt())
            val paint = Paint().apply {
                style = Paint.Style.FILL
                color = dividerColor
            }
            val dividerSize = ta.getDimension(R.styleable.VerticalRecyclerView_dividerSize, 1f)
            val dividerPadding = ta.getDimension(R.styleable.VerticalRecyclerView_dividerHorizontalPadding, 0f)
            if (dividerPositions != 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {

                    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                        val left = parent.paddingLeft + dividerPadding
                        val right = parent.width - parent.paddingRight - dividerPadding

                        val childCount = parent.childCount
                        for (i in 0..childCount - 1) {
                            val child = parent.getChildAt(i)

                            val top = with(child) {
                                top - (
                                        (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
                                        ) + (translationY + .5f).toInt()
                            } - dividerSize

                            if (i != 0 || dividerPositions and FLAG_DIVIDER_START != 0) {
                                c.drawRect(left, top, right, top + dividerSize, paint)
                            }

                            val bottom = with(child) {
                                bottom + (
                                        (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
                                        ) + (translationY + .5f).toInt()
                            }

                            if (i == parent.childCount - 1 && dividerPositions and FLAG_DIVIDER_END != 0) {
                                c.drawRect(left, bottom.toFloat(), right, bottom + dividerSize, paint)
                            }
                        }
                    }

                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.set(0, 0, 0, dividerSize.toInt())
                    }
                })
            }
            ta.recycle()
        }
    }
}
