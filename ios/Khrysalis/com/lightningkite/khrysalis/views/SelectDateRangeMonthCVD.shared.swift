//Package: com.lightningkite.khrysalis.views
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



open class SelectDateRangeMonthCVD: MonthCVD {
    
    
    
    override public func generateAccessibilityView() -> View?  {
        return nil
    }
    public var draggingStart: Bool
    public var start: MutableObservableProperty<DateAlone?>
    public var endInclusive: MutableObservableProperty<DateAlone?>
    public var selectedDayPaint: Paint
    public var selectedPaint: Paint
    
    override public func measure(width: Float, height: Float, displayMetrics: DisplayMetrics) -> Void {
        super.measure(width: width, height: height, displayMetrics: displayMetrics)
        selectedDayPaint.textSize = dayPaint.textSize
    }
    override public func measure(_ width: Float, _ height: Float, _ displayMetrics: DisplayMetrics) -> Void {
        return measure(width: width, height: height, displayMetrics: displayMetrics)
    }
    public var drawDay_dateAlone: DateAlone
    
    override public func drawDay(canvas: Canvas, showingMonth: DateAlone, day: DateAlone, displayMetrics: DisplayMetrics, outer: RectF, inner: RectF) -> Void {
        if day == start.value && ( day == endInclusive.value || endInclusive.value == nil ) {
            CalendarDrawing.dayBackground(canvas, inner, selectedPaint)
            CalendarDrawing.day(canvas, showingMonth, day, inner, selectedDayPaint)
        } else if day == start.value {
            CalendarDrawing.dayBackgroundStart(canvas, inner, outer, selectedPaint)
            CalendarDrawing.day(canvas, showingMonth, day, inner, selectedDayPaint)
        } else if day == endInclusive.value {
            CalendarDrawing.dayBackgroundEnd(canvas, inner, outer, selectedPaint)
            CalendarDrawing.day(canvas, showingMonth, day, inner, selectedDayPaint)
        } else if day.comparable > ( start.value?.comparable ?? Int32.MAX_VALUE ) && day.comparable < ( endInclusive.value?.comparable ?? Int32.MIN_VALUE ) {
            CalendarDrawing.dayBackgroundMid(canvas, inner, outer, selectedPaint)
            CalendarDrawing.day(canvas, showingMonth, day, inner, selectedDayPaint)
        } else  {
            CalendarDrawing.day(canvas, showingMonth, day, inner, dayPaint)
        }
    }
    override public func drawDay(_ canvas: Canvas, _ showingMonth: DateAlone, _ day: DateAlone, _ displayMetrics: DisplayMetrics, _ outer: RectF, _ inner: RectF) -> Void {
        return drawDay(canvas: canvas, showingMonth: showingMonth, day: day, displayMetrics: displayMetrics, outer: outer, inner: inner)
    }
    private var startedDraggingOn: DateAlone? 
    
    override public func onTap(day: DateAlone) -> Void {
        if start.value != nil, start.value == endInclusive.value, day.comparable > start.value!.comparable {
            endInclusive.value = day
        } else {
            start.value = day
            endInclusive.value = day
        }
    }
    override public func onTap(_ day: DateAlone) -> Void {
        return onTap(day: day)
    }
    
    override public func onTouchDown(day: DateAlone) -> Bool {
        if day != start.value, day != endInclusive.value {
            return false
        }
        startedDraggingOn = day
        var startValue = start.value
        var endInclusiveValue = endInclusive.value
        if startValue == nil || endInclusiveValue == nil {
            start.value = day
            endInclusive.value = day
            draggingStart = false
        } else if day == endInclusiveValue {
            draggingStart = false
        } else if day == startValue {
            draggingStart = true
        } else if day.comparable > endInclusiveValue!.comparable && startValue == endInclusiveValue {
            endInclusive.value = day
            draggingStart = false
        } else  {
            start.value = day
            endInclusive.value = day
            draggingStart = false
        }
        return true
    }
    override public func onTouchDown(_ day: DateAlone) -> Bool {
        return onTouchDown(day: day)
    }
    
    override public func onTouchMove(day: DateAlone) -> Bool {
        var startValue = start.value
        var endInclusiveValue = endInclusive.value
        if startValue == nil || endInclusiveValue == nil {
            } else if draggingStart && day.comparable > endInclusiveValue!.comparable {
            start.value = endInclusive.value
            endInclusive.value = day
            draggingStart = false
            return true
        } else if !draggingStart && day.comparable < startValue!.comparable {
            endInclusive.value = start.value
            start.value = day
            draggingStart = true
            return true
        }
        var obs: MutableObservableProperty<DateAlone?> = {if draggingStart {
            return start
        } else {
            return endInclusive
        }}()
        obs.value = day
        return true
    }
    override public func onTouchMove(_ day: DateAlone) -> Bool {
        return onTouchMove(day: day)
    }
    
    override public func onTouchUp(day: DateAlone) -> Bool {
        onTouchMove(day)
        return true
    }
    override public func onTouchUp(_ day: DateAlone) -> Bool {
        return onTouchUp(day: day)
    }
    
    override public init() {
        let draggingStart: Bool = true
        self.draggingStart = draggingStart
        let start: MutableObservableProperty<DateAlone?> = StandardObservableProperty(nil)
        self.start = start
        let endInclusive: MutableObservableProperty<DateAlone?> = StandardObservableProperty(nil)
        self.endInclusive = endInclusive
        let selectedDayPaint: Paint = Paint()
        self.selectedDayPaint = selectedDayPaint
        let selectedPaint: Paint = Paint()
        self.selectedPaint = selectedPaint
        let drawDay_dateAlone: DateAlone = DateAlone(0, 0, 0)
        self.drawDay_dateAlone = drawDay_dateAlone
        let startedDraggingOn: DateAlone?  = nil
        self.startedDraggingOn = startedDraggingOn
        super.init()
        self.start.onChange.addWeak(self) { (self, value) in 
            self.invalidate()
        }
        self.endInclusive.onChange.addWeak(self) { (self, value) in 
            self.invalidate()
        }
    }
}
 
