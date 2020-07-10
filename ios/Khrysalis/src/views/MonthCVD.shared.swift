// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: views/MonthCVD.shared.kt
// Package: com.lightningkite.khrysalis.views
import Foundation

public class MonthCVD : CustomViewDelegate {
    override public init() {
        self.currentMonthObs = StandardObservableProperty(underlyingValue: Date().dateAlone.setDayOfMonth(value: 1))
        self.dragEnabled = true
        self.labelFontSp = 12
        self.dayFontSp = 16
        self.internalPaddingDp = 8
        self.dayCellMarginDp = 8
        self.internalPadding = 0
        self.dayLabelHeight = 0
        self.dayCellHeight = 0
        self.dayCellWidth = 0
        self.dayCellMargin = 0
        self._currentOffset = 0
        self.dragStartX = 0
        self.lastOffset = 0
        self.lastOffsetTime = 0
        self.DRAGGING_NONE = -1
        self.draggingId = self.DRAGGING_NONE
        self.labelPaint = Paint()
        self.dayPaint = Paint()
        self.calcMonth = DateAlone(year: 1, month: 1, day: 1)
        self.calcMonthB = DateAlone(year: 0, month: 0, day: 0)
        self.drawDate = DateAlone(year: 1, month: 1, day: 1)
        self.rectForReuse = RectF()
        self.rectForReuseB = RectF()
        self.isTap = false
        self.dragStartY = 0
        super.init()
        self.currentMonthObs.subscribeBy(onNext:  { (value: DateAlone) -> Void in self?.postInvalidate() }).forever()
        self.labelPaint.isAntiAlias = true
        self.labelPaint.style = Paint.Style.FILL
        self.labelPaint.color = 0xFF808080.asColor()
        self.dayPaint.isAntiAlias = true
        self.dayPaint.style = Paint.Style.FILL
        self.dayPaint.color = 0xFF202020.asColor()
        animationFrame.subscribeBy(onNext: { (timePassed: Float) -> Void in if self.draggingId == self.DRAGGING_NONE, self.currentOffset != 0 {
                    var newOffset = self.currentOffset * max(0, (1 - 8 * timePassed))
                    let min = 0.001
                    if newOffset > min {
                        newOffset = newOffset - min
                    } else if newOffset < -min {
                        newOffset = newOffset + min
                    } else  {
                        newOffset = 0
                    }
                    self.currentOffset = newOffset
        } }).until(condition: self.removed)
    }
    
    override public func generateAccessibilityView() -> View? { return nil }
    
    public let currentMonthObs: MutableObservableProperty<DateAlone>
    public var currentMonth: DateAlone {
        get { return self.currentMonthObs.value }
        set(value) {
            self.currentMonthObs.value = value
        }
    }
    
    public var dragEnabled: Bool
    
    
    
    public var labelFontSp: CGFloat
    public var dayFontSp: CGFloat
    public var internalPaddingDp: CGFloat
    public var dayCellMarginDp: CGFloat
    private var internalPadding: CGFloat
    private var dayLabelHeight: CGFloat
    private var dayCellHeight: CGFloat
    private var dayCellWidth: CGFloat
    private var dayCellMargin: CGFloat
    
    private var _currentOffset: CGFloat
    public var currentOffset: CGFloat {
        get {
            return self._currentOffset
        }
        set(value) {
            self._currentOffset = value
            self.customView?.postInvalidate()
        }
    }
    private var dragStartX: CGFloat
    private var lastOffset: CGFloat
    private var lastOffsetTime: Int64
    private let DRAGGING_NONE: Int
    private var draggingId: Int
    
    public func animateNextMonth() -> Void {
        self.currentMonthObs.value.setAddMonthOfYear(value: 1)
        self.currentMonthObs.update()
        self.currentOffset = 1
    }
    
    public func animatePreviousMonth() -> Void {
        self.currentMonthObs.value.setAddMonthOfYear(value: -1)
        self.currentMonthObs.update()
        self.currentOffset = -1
    }
    
    public let labelPaint: Paint
    public let dayPaint: Paint
    
    
    
    private let calcMonth: DateAlone
    
    public func dayAtPixel(x: CGFloat, y: CGFloat, existing: DateAlone? = nil) -> DateAlone? {
        if y < self.dayLabelHeight { return nil }
        //        val columnRaw = (x / dayCellWidth - (dayCellWidth + currentOffset) * 7).toInt()
        let columnRawBeforeDrag = x / self.dayCellWidth
        let columnDrag = self.currentOffset * 7
        let columnRaw = Int((columnDrag + columnRawBeforeDrag))
        let column = columnRaw.floorMod(other: 7)
        let monthOffset = columnRaw.floorDiv(other: 7)
        let row = Int(((y - self.dayLabelHeight) / self.dayCellHeight))
        if row < 0 || row > 5 { return nil }
        if column < 0 || column > 6 { return nil }
        return self.dayAt(month: self.calcMonth.set(other: self.currentMonth).setAddMonthOfYear(value: monthOffset), row: row, column: column, existing: existing ?? DateAlone(year: 0, month: 0, day: 0))
    }
    
    public func dayAt(month: DateAlone, row: Int, column: Int, existing: DateAlone = DateAlone(year: 0, month: 0, day: 0)) -> DateAlone {
        return existing
        .set(other: month)
        .setDayOfMonth(value: 1)
        .setDayOfWeek(value: 1)
        .setAddDayOfMonth(value: row * 7 + column)
    }
    
    public func measure(width: CGFloat, height: CGFloat, displayMetrics: DisplayMetrics) -> Void {
        self.internalPadding = displayMetrics.density * self.internalPaddingDp
        self.dayCellMargin = displayMetrics.density * self.dayCellMarginDp
        self.labelPaint.textSize = self.labelFontSp * displayMetrics.scaledDensity
        self.dayPaint.textSize = self.dayFontSp * displayMetrics.scaledDensity
        self.dayLabelHeight = self.labelPaint.textSize * 1.5 + self.internalPadding * 2
        self.dayCellWidth = width / 7
        self.dayCellHeight = (height - self.dayLabelHeight) / 6
    }
    
    private let calcMonthB: DateAlone
    
    override public func draw(canvas: Canvas, width: CGFloat, height: CGFloat, displayMetrics: DisplayMetrics) -> Void {
        self.measure(width: width, height: height, displayMetrics: displayMetrics)
        if self.currentOffset > 0 {
            //draw past month and current month
            self.drawMonth(canvas: canvas, xOffset: (self.currentOffset - 1) * width, width: width, month: self.calcMonthB.set(other: self.currentMonth).setAddMonthOfYear(value: -1), displayMetrics: displayMetrics)
            self.drawMonth(canvas: canvas, xOffset: self.currentOffset * width, width: width, month: self.currentMonth, displayMetrics: displayMetrics)
        } else { if self.currentOffset < 0 {
                //draw future month and current month
                self.drawMonth(canvas: canvas, xOffset: (self.currentOffset + 1) * width, width: width, month: self.calcMonthB.set(other: self.currentMonth).setAddMonthOfYear(value: 1), displayMetrics: displayMetrics)
                self.drawMonth(canvas: canvas, xOffset: self.currentOffset * width, width: width, month: self.currentMonth, displayMetrics: displayMetrics)
            } else {
                //Nice, it's exactly zero.  We can just draw one.
                self.drawMonth(canvas: canvas, xOffset: self.currentOffset * width, width: width, month: self.currentMonth, displayMetrics: displayMetrics)
        } }
    }
    
    private var drawDate: DateAlone
    private let rectForReuse: RectF
    private let rectForReuseB: RectF
    public func drawMonth(canvas: Canvas, xOffset: CGFloat, width: CGFloat, month: DateAlone, displayMetrics: DisplayMetrics) -> Void {
        for day in ((Int(1)...Int(7))){
            let col = day - 1
            self.rectForReuse.set(p0: xOffset + Float(col) * self.dayCellWidth - 0.01, p1: -0.01, p2: xOffset + (Float(col) + 1) * self.dayCellWidth + 0.01, p3: self.dayLabelHeight + 0.01)
            self.rectForReuseB.set(p0: self.rectForReuse)
            self.rectForReuse.inset(p0: self.internalPadding, p1: self.internalPadding)
            self.drawLabel(canvas: canvas, dayOfWeek: day, displayMetrics: displayMetrics, outer: self.rectForReuse, inner: self.rectForReuseB)
        }
        for row in ((Int(0)...Int(5))){
            for col in ((Int(0)...Int(6))){
                let day = self.dayAt(month: month, row: row, column: col, existing: self.drawDate)
                self.rectForReuse.set(p0: xOffset + Float(col) * self.dayCellWidth - 0.01, p1: self.dayLabelHeight + Float(row) * self.dayCellHeight - 0.01, p2: xOffset + (Float(col) + 1) * self.dayCellWidth + 0.01, p3: self.dayLabelHeight + (Float(row) + 1) * self.dayCellHeight + 0.01)
                if self.rectForReuse.left > width {
                    continue
                }
                if self.rectForReuse.right < 0 {
                    continue
                }
                self.rectForReuseB.set(p0: self.rectForReuse)
                self.rectForReuse.inset(p0: self.dayCellMargin, p1: self.dayCellMargin)
                self.drawDay(canvas: canvas, showingMonth: month, day: day, displayMetrics: displayMetrics, outer: self.rectForReuseB, inner: self.rectForReuse)
            }
        }
    }
    
    public func drawLabel(canvas: Canvas, dayOfWeek: Int, displayMetrics: DisplayMetrics, outer: RectF, inner: RectF) -> Void {
        CalendarDrawing.INSTANCE.label(canvas: canvas, dayOfWeek: dayOfWeek, inner: inner, paint: self.labelPaint)
    }
    
    public func drawDay(canvas: Canvas, showingMonth: DateAlone, day: DateAlone, displayMetrics: DisplayMetrics, outer: RectF, inner: RectF) -> Void {
        CalendarDrawing.INSTANCE.day(canvas: canvas, month: showingMonth, date: day, inner: outer, paint: self.dayPaint)
    }
    
    public var isTap: Bool
    public var dragStartY: CGFloat
    public func onTap(day: DateAlone) -> Void {}
    
    public func onTouchDown(day: DateAlone) -> Bool { return false }
    override public func onTouchDown(id: Int, x: CGFloat, y: CGFloat, width: CGFloat, height: CGFloat) -> Bool {
        let day = self.dayAtPixel(x: x, y: y)
        if let it = (day) { 
            if self.onTouchDown(day: it) {
                return true
            }
        }
        self.dragStartX = x / width
        self.dragStartY = y / height
        self.draggingId = id
        self.lastOffsetTime = System.currentTimeMillis()
        self.isTap = true
        
        return true
    }
    
    public func onTouchMove(day: DateAlone) -> Bool { return false }
    override public func onTouchMove(id: Int, x: CGFloat, y: CGFloat, width: CGFloat, height: CGFloat) -> Bool {
        if self.draggingId == id {
            self.lastOffset = self.currentOffset
            self.lastOffsetTime = System.currentTimeMillis()
            if self.dragEnabled {
                self.currentOffset = (x / width) - self.dragStartX
                if abs((x / width - self.dragStartX)) > 0.05 || abs((y / height - self.dragStartY)) > 0.05 {
                    self.isTap = false
                }
            }
        } else {
            if let it = (self.dayAtPixel(x: x, y: y)) { 
                return self.onTouchMove(day: it)
            }
        }
        return true
    }
    
    public func onTouchUp(day: DateAlone) -> Bool { return false }
    override public func onTouchUp(id: Int, x: CGFloat, y: CGFloat, width: CGFloat, height: CGFloat) -> Bool {
        if self.draggingId == id {
            if self.isTap {
                if let it = (self.dayAtPixel(x: x, y: y)) { 
                    self.onTap(day: it)
                }
            } else { if self.dragEnabled {
                    let weighted = self.currentOffset + (self.currentOffset - self.lastOffset) * 200 / Float((System.currentTimeMillis() - self.lastOffsetTime))
                    if weighted > 0.5 {
                        //shift right one
                        self.currentMonthObs.value.setAddMonthOfYear(value: -1)
                        self.currentMonthObs.update()
                        self.currentOffset = self.currentOffset - 1
                    } else { if weighted < -0.5 {
                            //shift left one
                            self.currentMonthObs.value.setAddMonthOfYear(value: 1)
                            self.currentMonthObs.update()
                            self.currentOffset = self.currentOffset + 1
                    } }
            } }
            self.draggingId = self.DRAGGING_NONE
        } else {
            if let it = (self.dayAtPixel(x: x, y: y)) { 
                return self.onTouchUp(day: it)
            }
        }
        return true
    }
    
    
    override public func sizeThatFitsWidth(width: CGFloat, height: CGFloat) -> CGFloat {
        return self.dayLabelHeight * 28
    }
    
    override public func sizeThatFitsHeight(width: CGFloat, height: CGFloat) -> CGFloat {
        return width * 6 / 7 + self.dayLabelHeight
    }
}

public class CalendarDrawing {
    private init() {
    }
    public static let INSTANCE = CalendarDrawing()
    
    public func day(canvas: Canvas, month: DateAlone, date: DateAlone, inner: RectF, paint: Paint) -> Void {
        if date.month == month.month, date.year == month.year {
            canvas.drawTextCentered(text: String(describing: date.day), centerX: inner.centerX(), centerY: inner.centerY(), paint: paint)
        } else {
            let originalColor = paint.color
            var myPaint = paint
            myPaint.color = paint.color.colorAlpha(desiredAlpha: 64)
            canvas.drawTextCentered(text: String(describing: date.day), centerX: inner.centerX(), centerY: inner.centerY(), paint: myPaint)
            myPaint.color = originalColor
        }
    }
    
    public func label(canvas: Canvas, dayOfWeek: Int, inner: RectF, paint: Paint) -> Void {
        let text = TimeNames.INSTANCE.shortWeekdayName(oneIndexedPosition: dayOfWeek)
        canvas.drawTextCentered(text: text, centerX: inner.centerX(), centerY: inner.centerY(), paint: paint)
    }
    
    public func dayBackground(canvas: Canvas, inner: RectF, paint: Paint) -> Void {
        canvas.drawCircle(p0: inner.centerX(), p1: inner.centerY(), p2: min(inner.width() / 2, inner.height() / 2), p3: paint)
    }
    
    public func dayBackgroundStart(canvas: Canvas, inner: RectF, outer: RectF, paint: Paint) -> Void {
        canvas.drawCircle(p0: inner.centerX(), p1: inner.centerY(), p2: min(inner.width() / 2, inner.height() / 2), p3: paint)
        canvas.drawRect(p0: outer.centerX(), p1: inner.top, p2: outer.right, p3: inner.bottom, p4: paint)
    }
    
    public func dayBackgroundMid(canvas: Canvas, inner: RectF, outer: RectF, paint: Paint) -> Void {
        canvas.drawRect(p0: outer.left, p1: inner.top, p2: outer.right, p3: inner.bottom, p4: paint)
    }
    
    public func dayBackgroundEnd(canvas: Canvas, inner: RectF, outer: RectF, paint: Paint) -> Void {
        canvas.drawCircle(p0: inner.centerX(), p1: inner.centerY(), p2: min(inner.width() / 2, inner.height() / 2), p3: paint)
        canvas.drawRect(p0: outer.left, p1: inner.top, p2: outer.centerX(), p3: inner.bottom, p4: paint)
    }
}


