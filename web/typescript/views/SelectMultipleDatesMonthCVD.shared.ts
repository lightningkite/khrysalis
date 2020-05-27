// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/SelectMultipleDatesMonthCVD.shared.kt
// Package: com.lightningkite.khrysalis.views
// FQImport: com.lightningkite.khrysalis.views.CalendarDrawing.dayBackgroundEnd TS dayBackgroundEnd
// FQImport: com.lightningkite.khrysalis.views.CalendarDrawing.dayBackgroundStart TS dayBackgroundStart
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.drawDay.showingMonth TS showingMonth
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.selectedPaint TS selectedPaint
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.onTouchMove TS onTouchMove
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.drawDay.left TS left
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.drawDay_dateAlone TS drawDay_dateAlone
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.drawDay.outer TS outer
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.drawDay.leftDate TS leftDate
// FQImport: kotlin.collections.Set TS Set
// FQImport: com.lightningkite.khrysalis.views.MonthCVD TS MonthCVD
// FQImport: com.lightningkite.khrysalis.time.set>com.lightningkite.khrysalis.time.DateAlone TS comLightningkiteKhrysalisTimeDateAloneSet
// FQImport: kotlin.collections.plus>kotlin.collections.Set<kotlin.Any> TS kotlinCollectionsSetPlus
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.measure.height TS height
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.onTouchMove.<anonymous>.it TS it
// FQImport: android.graphics.Paint.textSize TS setAndroidGraphicsPaintTextSize
// FQImport: android.graphics.RectF TS RectF
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.drawDay.canvas TS canvas
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.dates TS dates
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.onTap.day TS day
// FQImport: com.lightningkite.khrysalis.time.DateAlone TS DateAlone
// FQImport: android.util.DisplayMetrics TS DisplayMetrics
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.onTouchMove.day TS day
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.drawDay.inner TS inner
// FQImport: android.graphics.Paint.textSize TS getAndroidGraphicsPaintTextSize
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.selectedDayPaint TS selectedDayPaint
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.measure.displayMetrics TS displayMetrics
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.adding TS adding
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.onTouchDown.day TS day
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.dayPaint TS dayPaint
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.drawDay.day TS day
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.drawDay.rightDate TS rightDate
// FQImport: com.lightningkite.khrysalis.views.CalendarDrawing.dayBackground TS dayBackground
// FQImport: com.lightningkite.khrysalis.views.MonthCVD.measure TS measure
// FQImport: kotlin.collections.Set.contains TS contains
// FQImport: com.lightningkite.khrysalis.views.CalendarDrawing TS CalendarDrawing
// FQImport: com.lightningkite.khrysalis.observables.StandardObservableProperty TS StandardObservableProperty
// FQImport: com.lightningkite.khrysalis.views.CalendarDrawing.dayBackgroundMid TS dayBackgroundMid
// FQImport: kotlin.collections.toSet>kotlin.collections.Iterable<kotlin.Any> TS kotlinCollectionsIterableToSet
// FQImport: android.graphics.Paint TS Paint
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.drawDay.right TS right
// FQImport: com.lightningkite.khrysalis.observables.StandardObservableProperty.value TS value
// FQImport: com.lightningkite.khrysalis.views.CalendarDrawing.day TS day
// FQImport: com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD.measure.width TS width
import { StandardObservableProperty } from './../observables/StandardObservableProperty.shared'
import { DateAlone } from './../time/DateAlone.actual'
import { CalendarDrawing, MonthCVD } from './MonthCVD.shared'
import { dateModRelative } from 'time/Date.actual'
import { Paint } from './draw/Paint.actual'
import { comLightningkiteKhrysalisTimeDateAloneSet } from './../time/DateAlone.shared'

//! Declares com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD
export class SelectMultipleDatesMonthCVD extends MonthCVD {
    public constructor() { super(); }
    public generateAccessibilityView(): (HTMLElement | null){ return null; }
    
    public readonly dates: StandardObservableProperty<Set<DateAlone>>;
    
    public readonly selectedDayPaint: Paint;
    
    public readonly selectedPaint: Paint;
    
    
    
    
    public measure(width: number, height: number, displayMetrics: DisplayMetrics): void{
        .measure(width, height, displayMetrics);
        setAndroidGraphicsPaintTextSize(this.selectedDayPaint, getAndroidGraphicsPaintTextSize(this.dayPaint));
    }
    
    public readonly drawDay_dateAlone: DateAlone;
    
    public drawDay(canvas: CanvasRenderingContext2D, showingMonth: DateAlone, day: DateAlone, displayMetrics: DisplayMetrics, outer: RectF, inner: RectF): void{
        if (this.dates.value.contains(day)) {
            const leftDate = dateModRelative(comLightningkiteKhrysalisTimeDateAloneSet(this.drawDay_dateAlone, day), Date.prototype.getDate, Date.prototype.setDate, -1);
            
            const left = this.dates.value.contains(leftDate);
            
            const rightDate = dateModRelative(comLightningkiteKhrysalisTimeDateAloneSet(this.drawDay_dateAlone, day), Date.prototype.getDate, Date.prototype.setDate, 1);
            
            const right = this.dates.value.contains(rightDate);
            
            
            if (!left && !right){
                CalendarDrawing.INSTANCE.dayBackground(canvas, inner, this.selectedPaint);
            } else if (!left && right){
                CalendarDrawing.INSTANCE.dayBackgroundStart(canvas, inner, outer, this.selectedPaint);
            } else if (left && !right){
                CalendarDrawing.INSTANCE.dayBackgroundEnd(canvas, inner, outer, this.selectedPaint);
            } else if (left && right){
                CalendarDrawing.INSTANCE.dayBackgroundMid(canvas, inner, outer, this.selectedPaint);
            } else {
                CalendarDrawing.INSTANCE.dayBackground(canvas, inner, this.selectedPaint);
            }
            CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.selectedDayPaint);
        } else {
            CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.dayPaint);
        }
    }
    
    public onTap(day: DateAlone): boolean{
        this.adding = ;
        this.onTouchMove(day);
    }
    
    public adding: boolean;
    
    public onTouchDown(day: DateAlone): boolean{
        this.adding = ;
        this.onTouchMove(day);
        return true;
    }
    
    public onTouchMove(day: DateAlone): boolean{
        if (this.adding) {
            if () {
                this.dates.value = kotlinCollectionsSetPlus(this.dates.value, day);
            }
        } else {
            this.dates.value = kotlinCollectionsIterableToSet(this.dates.value.filter((it) => !(it.equals(day))));
        }
        return true;
    }
    
    public onTouchUp(day: DateAlone): boolean{
        return true;
    }
}

