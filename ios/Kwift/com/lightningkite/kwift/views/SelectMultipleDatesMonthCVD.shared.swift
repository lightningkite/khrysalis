//Package: com.lightningkite.kwift.views
//Converted using Kwift2

import Foundation



open class SelectMultipleDatesMonthCVD: MonthCVD {
    
    
    
    override public func generateAccessibilityView() -> View?  {
        return nil
    }
    public var dates: StandardObservableProperty<Set<DateAlone>>
    public var selectedDayPaint: Paint
    public var selectedPaint: Paint
    
    override public func measure(width: Float, height: Float, displayMetrics: DisplayMetrics) -> Void {
        super.measure(width, height, displayMetrics)
        selectedDayPaint.textSize = dayPaint.textSize
    }
    override public func measure(_ width: Float, _ height: Float, _ displayMetrics: DisplayMetrics) -> Void {
        return measure(width: width, height: height, displayMetrics: displayMetrics)
    }
    public var drawDay_dateAlone: DateAlone
    
    override public func drawDay(canvas: Canvas, showingMonth: DateAlone, day: DateAlone, displayMetrics: DisplayMetrics, outer: RectF, inner: RectF) -> Void {
        if dates.value.contains(day) {
            var leftDate = drawDay_dateAlone.set(day).setAddDayOfMonth(-1)
            var left = dates.value.contains(leftDate)
            var rightDate = drawDay_dateAlone.set(day).setAddDayOfMonth(1)
            var right = dates.value.contains(rightDate)
            if !left && !right {
                CalendarDrawing.dayBackground(canvas, inner, selectedPaint)
            } else if !left && right {
                CalendarDrawing.dayBackgroundStart(canvas, inner, outer, selectedPaint)
            } else if left && !right {
                CalendarDrawing.dayBackgroundEnd(canvas, inner, outer, selectedPaint)
            } else if left && right {
                CalendarDrawing.dayBackgroundMid(canvas, inner, outer, selectedPaint)
            } else  {
                CalendarDrawing.dayBackground(canvas, inner, selectedPaint)
            }
            CalendarDrawing.day(canvas, showingMonth, day, inner, selectedDayPaint)
        } else {
            CalendarDrawing.day(canvas, showingMonth, day, inner, dayPaint)
        }
    }
    override public func drawDay(_ canvas: Canvas, _ showingMonth: DateAlone, _ day: DateAlone, _ displayMetrics: DisplayMetrics, _ outer: RectF, _ inner: RectF) -> Void {
        return drawDay(canvas: canvas, showingMonth: showingMonth, day: day, displayMetrics: displayMetrics, outer: outer, inner: inner)
    }
    
    override public func onTap(day: DateAlone) -> Void {
        adding = dates.value.none{ (it) in 
            day == it
        }
        onTouchMove(day)
    }
    override public func onTap(_ day: DateAlone) -> Void {
        return onTap(day: day)
    }
    public var adding: Bool
    
    override public func onTouchDown(day: DateAlone) -> Bool {
        adding = dates.value.none{ (it) in 
            day == it
        }
        onTouchMove(day)
        return true
    }
    override public func onTouchDown(_ day: DateAlone) -> Bool {
        return onTouchDown(day: day)
    }
    
    override public func onTouchMove(day: DateAlone) -> Bool {
        if adding {
            if dates.value.none({ (it) in 
                day == it
            }) {
                dates.value = dates.value.plus(day)
            }
        } else {
            dates.value = dates.value.filter{ (it) in 
                it != day
            }.toSet()
        }
        return true
    }
    override public func onTouchMove(_ day: DateAlone) -> Bool {
        return onTouchMove(day: day)
    }
    
    override public func onTouchUp(day: DateAlone) -> Bool {
        return true
    }
    override public func onTouchUp(_ day: DateAlone) -> Bool {
        return onTouchUp(day: day)
    }
    
    override public init() {
        let dates: StandardObservableProperty<Set<DateAlone>> = StandardObservableProperty<Set<DateAlone>>([])
        self.dates = dates
        let selectedDayPaint: Paint = Paint()
        self.selectedDayPaint = selectedDayPaint
        let selectedPaint: Paint = Paint()
        self.selectedPaint = selectedPaint
        let drawDay_dateAlone: DateAlone = DateAlone(0, 0, 0)
        self.drawDay_dateAlone = drawDay_dateAlone
        let adding: Bool = false
        self.adding = adding
        super.init()
        self.dates.onChange.addWeak(self) { (self, value) in 
            self.invalidate()
        }
    }
}
 
