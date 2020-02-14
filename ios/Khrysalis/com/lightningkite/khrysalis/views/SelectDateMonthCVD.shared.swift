//Package: com.lightningkite.khrysalis.views
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



open class SelectDateMonthCVD: MonthCVD {
    
    
    
    override public func generateAccessibilityView() -> View?  {
        return nil
    }
    public var selected: MutableObservableProperty<DateAlone?>
    public var selectedDayPaint: Paint
    public var selectedPaint: Paint
    
    override public func drawDay(canvas: Canvas, showingMonth: DateAlone, day: DateAlone, displayMetrics: DisplayMetrics, outer: RectF, inner: RectF) -> Void {
        if day == selected.value {
            CalendarDrawing.dayBackground(canvas, inner, selectedPaint)
            CalendarDrawing.day(canvas, showingMonth, day, inner, selectedDayPaint)
        } else  {
            CalendarDrawing.day(canvas, showingMonth, day, inner, dayPaint)
        }
    }
    override public func drawDay(_ canvas: Canvas, _ showingMonth: DateAlone, _ day: DateAlone, _ displayMetrics: DisplayMetrics, _ outer: RectF, _ inner: RectF) -> Void {
        return drawDay(canvas: canvas, showingMonth: showingMonth, day: day, displayMetrics: displayMetrics, outer: outer, inner: inner)
    }
    
    override public func measure(width: Float, height: Float, displayMetrics: DisplayMetrics) -> Void {
        super.measure(width: width, height: height, displayMetrics: displayMetrics)
        selectedDayPaint.textSize = dayPaint.textSize
    }
    override public func measure(_ width: Float, _ height: Float, _ displayMetrics: DisplayMetrics) -> Void {
        return measure(width: width, height: height, displayMetrics: displayMetrics)
    }
    
    override public func onTap(day: DateAlone) -> Void {
        selected.value = day
    }
    override public func onTap(_ day: DateAlone) -> Void {
        return onTap(day: day)
    }
    
    override public init() {
        let selected: MutableObservableProperty<DateAlone?> = StandardObservableProperty(nil)
        self.selected = selected
        let selectedDayPaint: Paint = Paint()
        self.selectedDayPaint = selectedDayPaint
        let selectedPaint: Paint = Paint()
        self.selectedPaint = selectedPaint
        super.init()
        
        if let it = (selected.value) {
            self.currentMonthObs.value = it.dayOfMonth(1) 
        }
        self.selected.onChange.addWeak(self) { (self, value) in 
            self.invalidate()
        }
    }
}
 
