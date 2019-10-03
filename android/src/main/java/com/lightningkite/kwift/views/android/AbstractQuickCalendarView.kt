package com.lightningkite.kwift.views.android

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lightningkite.kwift.R
import com.lightningkite.kwift.observables.shared.StandardObservableProperty
import com.lightningkite.kwift.observables.shared.addAndRunWeak
import java.text.SimpleDateFormat
import java.util.*

abstract class AbstractQuickCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var monthHeader: View? = null
    var showMonthHeader: Boolean = true
        set(value) {
            field = value
            monthHeader?.visibility = if (value) View.VISIBLE else View.GONE
        }

    private val center = 400 * 12
    val currentPage: StandardObservableProperty<Int> =
        StandardObservableProperty(positionFromMonth(Calendar.getInstance()))
    val childAttributeSet: AttributeSet? = attrs

    abstract fun makeChildView(): QuickMonthView
    open val ignoreDragOnDay: Boolean get() = false


    fun monthFromPosition(index: Int, out: Calendar = Calendar.getInstance()): Calendar {
        out.set(1970, Calendar.JANUARY, 1)
        out.add(Calendar.MONTH, index - center)
        return out
    }

    fun positionFromMonth(calendar: Calendar): Int {
        return center + (calendar.get(Calendar.YEAR) - 1970) * 12 + calendar.get(Calendar.MONTH)
    }

    init {
        this.orientation = LinearLayout.VERTICAL
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.AbstractQuickCalendarView, defStyleAttr, 0)
        showMonthHeader = a.getBoolean(R.styleable.AbstractQuickCalendarView_showMonthHeader, true)

        lateinit var pager: ViewPager

        LinearLayout(context).apply {
            monthHeader = this
            this.orientation = LinearLayout.HORIZONTAL
            this.gravity = Gravity.CENTER

            Button(context, childAttributeSet).apply {
                background = null
                text = "<"
                textSize = 16f
                setOnClickListener {
                    currentPage.value -= 1
                }
            }.let { addView(it, LayoutParams(WRAP_CONTENT, WRAP_CONTENT)) }

            Space(context).let { addView(it, LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f)) }

            TextView(context, childAttributeSet).apply {
                val cal = Calendar.getInstance()
                currentPage.addAndRunWeak(this) { self, value ->
                    self.text = SimpleDateFormat("MMM yyyy").format(monthFromPosition(value, cal).time)
                }
            }.let { addView(it, LayoutParams(WRAP_CONTENT, WRAP_CONTENT)) }

            Space(context).let { addView(it, LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f)) }

            Button(context, childAttributeSet).apply {
                background = null
                text = ">"
                textSize = 16f
                setOnClickListener {
                    currentPage.value += 1
                }
            }.let { addView(it, LayoutParams(WRAP_CONTENT, WRAP_CONTENT)) }

            monthHeader?.visibility = if (showMonthHeader) View.VISIBLE else View.GONE
        }.let { addView(it, LayoutParams(MATCH_PARENT, WRAP_CONTENT)) }

        object : ViewPager(context) {
            override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
                return if (ignoreDragOnDay) false else super.onInterceptTouchEvent(ev)
            }
        }.apply {
            adapter = object : PagerAdapter() {
                override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

                override fun getCount(): Int = center * 2

                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    val view = makeChildView()
                    view.month = monthFromPosition(position)
                    view.layoutParams = ViewPager.LayoutParams().apply {
                        this.width = MATCH_PARENT
                        this.height = MATCH_PARENT
                    }
                    container.addView(view)
                    return view
                }

                override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                    container.removeView(`object` as View)
                }
            }

            currentPage.addAndRunWeak(this) { self, value ->
                self.currentItem = value
            }
            this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {}
                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
                override fun onPageSelected(p0: Int) {
                    currentPage.value = p0
                }
            })
        }.let { addView(it, LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1f)) }
    }
}
