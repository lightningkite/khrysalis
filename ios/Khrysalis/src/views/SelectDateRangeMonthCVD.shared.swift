// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: views/SelectDateRangeMonthCVD.shared.kt
// Package: com.lightningkite.khrysalis.views
import Foundation
import CoreGraphics

open class SelectDateRangeMonthCVD : MonthCVD {
    override public init() {
        let draggingStart: Bool = true
        self.draggingStart = draggingStart
        let start: MutableObservableProperty<DateAlone?> = StandardObservableProperty(underlyingValue: nil)
        self.start = start
        let endInclusive: MutableObservableProperty<DateAlone?> = StandardObservableProperty(underlyingValue: nil)
        self.endInclusive = endInclusive
        let selectedDayPaint: Paint = Paint()
        self.selectedDayPaint = selectedDayPaint
        let selectedPaint: Paint = Paint()
        self.selectedPaint = selectedPaint
        let drawDay_dateAlone: DateAlone = DateAlone(year: 0, month: 0, day: 0)
        self.drawDay_dateAlone = drawDay_dateAlone
        let startedDraggingOn: DateAlone? = nil
        self.startedDraggingOn = startedDraggingOn
        super.init()
        if let it = (self.start.value) { 
            self.currentMonthObs.value = it.dayOfMonth(value: 1)
        }
        self.start.onChange.subscribeBy(onNext:  { [weak self] (value: DateAlone?) -> Void in self?.invalidate() }).forever()
        self.endInclusive.subscribeBy(onNext:  { [weak self] (value: DateAlone?) -> Void in self?.invalidate() }).forever()
    }
    
    override public func generateAccessibilityView() -> View? { return nil }
    
    public var draggingStart: Bool
    public var start: MutableObservableProperty<DateAlone?>
    public var endInclusive: MutableObservableProperty<DateAlone?>
    
    
    public let selectedDayPaint: Paint
    public let selectedPaint: Paint
    
    override public func measure(width: CGFloat, height: CGFloat, displayMetrics: DisplayMetrics) -> Void {
        super.measure(width: width, height: height, displayMetrics: displayMetrics)
        self.selectedDayPaint.textSize = self.dayPaint.textSize
    }
    
    public let drawDay_dateAlone: DateAlone
    override public func drawDay(canvas: Canvas, showingMonth: DateAlone, day: DateAlone, displayMetrics: DisplayMetrics, outer: CGRect, inner: CGRect) -> Void {
        if day == self.start.value && (day == self.endInclusive.value || self.endInclusive.value == nil){
            CalendarDrawing.INSTANCE.dayBackground(canvas: canvas, inner: inner, paint: self.selectedPaint)
            CalendarDrawing.INSTANCE.day(canvas: canvas, month: showingMonth, date: day, inner: inner, paint: self.selectedDayPaint)
        } else if day == self.start.value{
            CalendarDrawing.INSTANCE.dayBackgroundStart(canvas: canvas, inner: inner, outer: outer, paint: self.selectedPaint)
            CalendarDrawing.INSTANCE.day(canvas: canvas, month: showingMonth, date: day, inner: inner, paint: self.selectedDayPaint)
        } else if day == self.endInclusive.value{
            CalendarDrawing.INSTANCE.dayBackgroundEnd(canvas: canvas, inner: inner, outer: outer, paint: self.selectedPaint)
            CalendarDrawing.INSTANCE.day(canvas: canvas, month: showingMonth, date: day, inner: inner, paint: self.selectedDayPaint)
        } else if day.comparable > (self.start.value?.comparable ?? Int.max) && day.comparable < (self.endInclusive.value?.comparable ?? Int.min){
            CalendarDrawing.INSTANCE.dayBackgroundMid(canvas: canvas, inner: inner, outer: outer, paint: self.selectedPaint)
            CalendarDrawing.INSTANCE.day(canvas: canvas, month: showingMonth, date: day, inner: inner, paint: self.selectedDayPaint)
        } else {
            CalendarDrawing.INSTANCE.day(canvas: canvas, month: showingMonth, date: day, inner: inner, paint: self.dayPaint)
        }
    }
    
    
    private var startedDraggingOn: DateAlone?
    
    override public func onTap(day: DateAlone) -> Void {
        if self.start.value != nil, self.start.value == self.endInclusive.value, day.comparable > self.start.value!.comparable {
            self.endInclusive.value = day
        } else {
            self.start.value = day
            self.endInclusive.value = day
        }
    }
    
    override public func onTouchDown(day: DateAlone) -> Bool {
        if day != self.start.value, day != self.endInclusive.value {
            return false
        }
        self.startedDraggingOn = day
        //If on start/end - drag
        //If after, extend
        //If before, extend
        //If middle, collapse all
        let startValue = self.start.value
        let endInclusiveValue = self.endInclusive.value
        
        if startValue == nil || endInclusiveValue == nil{
            self.start.value = day
            self.endInclusive.value = day
            self.draggingStart = false
        } else if day == endInclusiveValue{
            self.draggingStart = false
        } else if day == startValue{
            self.draggingStart = true
        } else if day.comparable > endInclusiveValue!.comparable && startValue == endInclusiveValue{
            self.endInclusive.value = day
            self.draggingStart = false
        } else {
            self.start.value = day
            self.endInclusive.value = day
            self.draggingStart = false
        }
        return true
    }
    
    override public func onTouchMove(day: DateAlone) -> Bool {
        let startValue = self.start.value
        let endInclusiveValue = self.endInclusive.value
        if startValue == nil || endInclusiveValue == nil{
        } else if self.draggingStart && day.comparable > endInclusiveValue!.comparable{
            self.start.value = self.endInclusive.value
            self.endInclusive.value = day
            self.draggingStart = false
            return true
        } else if !self.draggingStart && day.comparable < startValue!.comparable{
            self.endInclusive.value = self.start.value
            self.start.value = day
            self.draggingStart = true
            return true
        }
        
        let obs: MutableObservableProperty<DateAlone?> = self.draggingStart ? self.start : self.endInclusive
        obs.value = day
        return true
    }
    
    override public func onTouchUp(day: DateAlone) -> Bool {
        self.onTouchMove(day: day)
        return true
    }
}


