package com.lightningkite.kwift.views.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.lightningkite.kwift.R
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.time.DayOfWeek
import java.util.*
import kotlin.math.min

open class QuickMonthView : View {

    data class ColorSet(
        val foreground: Int = Color.BLACK,
        val background: Int = Color.WHITE
    )

    private var _firstDay: Calendar = Calendar.getInstance()
    val firstDay: Calendar get() = _firstDay
    var month: Calendar = Calendar.getInstance()
        set(value) {
            value.set(Calendar.DAY_OF_MONTH, 1)
            value.set(Calendar.HOUR_OF_DAY, 0)
            value.set(Calendar.MINUTE, 0)
            value.set(Calendar.SECOND, 0)
            value.set(Calendar.MILLISECOND, 0)
            field = value

            _firstDay.timeInMillis = value.timeInMillis
            _firstDay.set(Calendar.DAY_OF_WEEK, 1)

            invalidate()
        }

    var labelColorSet = ColorSet()
    var defaultColorSet = ColorSet()
    var selectedColorSet = ColorSet(foreground = Color.WHITE, background = Color.RED)
    var labelFontSp: Float = 12f
    var dayFontSp: Float = 16f
    var internalPaddingDp: Float = 8f
    var dayCellMarginDp: Float = 8f

    val paint = Paint().apply {
        flags = flags or Paint.ANTI_ALIAS_FLAG
    }

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    var dayLabelHeight: Float = 0f
    private var dayCellHeight: Float = 0f
    private var dayCellWidth: Float = 0f
    var dayCellMargin: Float = 0f
    private fun measure(context: Context) {
        val internalPadding = context.resources.displayMetrics.density * internalPaddingDp
        dayCellMargin = context.resources.displayMetrics.density * dayCellMarginDp
        paint.textSize = labelFontSp * context.resources.displayMetrics.scaledDensity
        dayLabelHeight = paint.fontMetrics.let { it.descent - it.ascent } + internalPadding * 2
        dayCellWidth = width / 7f
        dayCellHeight = (height - dayLabelHeight) / 6f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        measure(context)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width = when (widthMode) {
            MeasureSpec.AT_MOST -> min(measureX(), widthSize)
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.UNSPECIFIED -> min(measureX(), widthSize)
            else -> min(measureX(), widthSize)
        }
        val height = when (heightMode) {
            MeasureSpec.AT_MOST -> min(measureY(width), heightSize)
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.UNSPECIFIED -> min(measureY(width), heightSize)
            else -> min(measureY(width), heightSize)
        }
        setMeasuredDimension(
            width,
            height
        )
    }

    private fun measureX(): Int {
        paint.apply {
            this.textSize = labelFontSp * context.resources.displayMetrics.scaledDensity
        }
        return DayOfWeek.values().sumBy { paint.measureText(symbols.shortWeekdays[it.ordinal + 1]).toInt() }
    }

    private fun measureY(givenWidth: Int): Int {
        return givenWidth * 6 / 7 + dayLabelHeight.toInt()
    }


    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) {
        measure(context)
        month = Calendar.getInstance()
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.QuickMonthView, defStyleAttr, 0)
        defaultColorSet = ColorSet(
            foreground = a.getColor(R.styleable.QuickMonthView_selectedForegroundColor, Color.BLACK),
            background = a.getColor(R.styleable.QuickMonthView_selectedBackgroundColor, Color.WHITE)
        )
        selectedColorSet = ColorSet(
            foreground = a.getColor(R.styleable.QuickMonthView_defaultForegroundColor, Color.WHITE),
            background = a.getColor(
                R.styleable.QuickMonthView_defaultBackgroundColor,
                Color.BLUE
            )
        )
        labelColorSet = ColorSet(
            foreground = a.getColor(R.styleable.QuickMonthView_labelForegroundColor, Color.BLACK),
            background = a.getColor(R.styleable.QuickMonthView_labelBackgroundColor, Color.WHITE)
        )
    }

    open fun onTouchDown(calendar: Calendar): Boolean {
        println("Down on ${DateFormat.getDateInstance().format(calendar.time)}")
        return false
    }

    open fun onTouchMove(calendar: Calendar): Boolean {
        println("Move on ${DateFormat.getDateInstance().format(calendar.time)}")
        return false
    }

    open fun onTouchUp(calendar: Calendar): Boolean {
        println("Up on ${DateFormat.getDateInstance().format(calendar.time)}")
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val position = dayAtPixel(event.x, event.y) ?: return false
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> onTouchDown(position)
            MotionEvent.ACTION_MOVE -> onTouchMove(position)
            MotionEvent.ACTION_UP -> onTouchUp(position)
            else -> false
        }
    }

    override fun performClick(): Boolean {
        //Trigger normal date selection dialog?
        //TODO: Accessibility
        return super.performClick()
    }

    fun dayAtPixel(x: Float, y: Float, out: Calendar = Calendar.getInstance()): Calendar? {
        if (y < dayLabelHeight) return null
        val column = (x / dayCellWidth).toInt()
        val row = ((y - dayLabelHeight) / dayCellHeight).toInt()
        if (row !in 0..5) return null
        if (column !in 0..6) return null
        return dayAt(row, column, out)
    }

    fun dayAt(row: Int, column: Int, out: Calendar = Calendar.getInstance()): Calendar {
        out.timeInMillis = firstDay.timeInMillis
        out.add(Calendar.DAY_OF_MONTH, row * 7 + column)
        return out
    }

    val symbols = DateFormatSymbols()
    val forReuse: Calendar = Calendar.getInstance()
    val rectForReuse: RectF = RectF()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLabels(canvas)
        for (row in 0..5) {
            for (col in 0..6) {
                val day = dayAt(row, col, forReuse)
                rectForReuse.set(
                    col * dayCellWidth,
                    dayLabelHeight + row * dayCellHeight,
                    (col + 1) * dayCellWidth,
                    dayLabelHeight + (row + 1) * dayCellHeight
                )
                drawDayCell(canvas, rectForReuse, day)
            }
        }
    }

    protected open fun drawDayCell(canvas: Canvas, rectangle: RectF, day: Calendar) {
        drawDayCellBackground(canvas, rectangle, defaultColorSet)
        drawDayCellText(day, canvas, rectangle, defaultColorSet)
    }

    protected fun drawDayCellBackground(canvas: Canvas, rectangle: RectF, colorSet: ColorSet) {
        drawDayCellBackground(canvas, rectangle) {
            color = colorSet.background
            style = Paint.Style.FILL
        }
    }

    protected inline fun drawDayCellBackground(canvas: Canvas, rectangle: RectF, paintSetup: Paint.() -> Unit) {
        val size = min(rectangle.width(), rectangle.height()) / 2 - dayCellMargin
        canvas.drawCircle(rectangle.centerX(), rectangle.centerY(), size, paint.apply(paintSetup))
    }

    protected fun drawDayCellBackgroundStart(canvas: Canvas, rectangle: RectF, colorSet: ColorSet) {
        drawDayCellBackgroundStart(canvas, rectangle) {
            color = colorSet.background
            style = Paint.Style.FILL
        }
    }

    protected inline fun drawDayCellBackgroundStart(canvas: Canvas, rectangle: RectF, paintSetup: Paint.() -> Unit) {
        val size = min(rectangle.width(), rectangle.height()) / 2 - dayCellMargin
        canvas.drawCircle(rectangle.centerX(), rectangle.centerY(), size, paint.apply(paintSetup))
        canvas.drawRect(
            rectangle.centerX(),
            rectangle.top + dayCellMargin,
            rectangle.right + 1,
            rectangle.bottom - dayCellMargin,
            paint
        )
    }

    protected fun drawDayCellBackgroundEnd(canvas: Canvas, rectangle: RectF, colorSet: ColorSet) {
        drawDayCellBackgroundEnd(canvas, rectangle) {
            color = colorSet.background
            style = Paint.Style.FILL
        }
    }

    protected inline fun drawDayCellBackgroundEnd(canvas: Canvas, rectangle: RectF, paintSetup: Paint.() -> Unit) {
        val size = min(rectangle.width(), rectangle.height()) / 2 - dayCellMargin
        canvas.drawCircle(rectangle.centerX(), rectangle.centerY(), size, paint.apply(paintSetup))
        canvas.drawRect(
            rectangle.left - 1,
            rectangle.top + dayCellMargin,
            rectangle.centerX(),
            rectangle.bottom - dayCellMargin,
            paint
        )
    }

    protected fun drawDayCellBackgroundMiddle(canvas: Canvas, rectangle: RectF, colorSet: ColorSet) {
        drawDayCellBackgroundMiddle(canvas, rectangle) {
            color = colorSet.background
            style = Paint.Style.FILL
        }
    }

    protected inline fun drawDayCellBackgroundMiddle(canvas: Canvas, rectangle: RectF, paintSetup: Paint.() -> Unit) {
        canvas.drawRect(
            rectangle.left - 1,
            rectangle.top + dayCellMargin,
            rectangle.right + 1,
            rectangle.bottom - dayCellMargin,
            paint.apply(paintSetup)
        )
    }


    protected fun drawDayCellText(
        day: Calendar,
        canvas: Canvas,
        rectangle: RectF,
        colorSet: ColorSet
    ): Unit = drawDayCellText(day, canvas, rectangle) { isInMonth ->
        this.color = if (isInMonth) {
            colorSet.foreground
        } else {
            colorSet.foreground.colorAlpha(64)
        }

        this.style = Paint.Style.FILL
        this.textSize = dayFontSp * context.resources.displayMetrics.scaledDensity
    }

    protected inline fun drawDayCellText(
        day: Calendar,
        canvas: Canvas,
        rectangle: RectF,
        paintSetup: Paint.(isInMonth: Boolean) -> Unit = { isInMonth ->
        }
    ) {
        paint.apply { paintSetup(day.get(Calendar.MONTH) == month.get(Calendar.MONTH)) }
        canvas.drawTextCentered(
            day.get(Calendar.DAY_OF_MONTH).toString(),
            rectangle.centerX(),
            rectangle.centerY(),
            paint
        )
    }

    protected open fun drawLabels(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), dayLabelHeight, paint.apply {
            this.color = labelColorSet.background
            this.style = Paint.Style.FILL
        })
        paint.apply {
            this.color = labelColorSet.foreground
            this.style = Paint.Style.FILL
            this.textSize = labelFontSp * context.resources.displayMetrics.scaledDensity
        }
        for (day in DayOfWeek.values()) {
            val text = symbols.shortWeekdays[day.ordinal + 1]
            val left = day.ordinal * dayCellWidth
            val right = (day.ordinal + 1) * dayCellWidth
            val centerX = (left + right) / 2
            val centerY = dayLabelHeight / 2
            canvas.drawTextCentered(text, centerX, centerY, paint)
        }
    }

}
