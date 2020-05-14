// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/SelectMultipleDatesMonthCVD.shared.kt
// Package: com.lightningkite.khrysalis.views
import { StandardObservableProperty } from './../observables/StandardObservableProperty.shared'
import { ComLightningkiteKhrysalisTimeDateAloneSetAddDayOfMonth, DateAlone } from './../time/DateAlone.actual'
import { MonthCVD } from './MonthCVD.shared'
import { ComLightningkiteKhrysalisTimeDateAloneSet } from './../time/TimeAlone.shared'

//! Declares com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD
export class SelectMultipleDatesMonthCVD extends MonthCVD {
    constructor() { super(); }
    public generateAccessibilityView(): (View | null){ return null; }
    
    public readonly dates: StandardObservableProperty<Set<DateAlone>> = new StandardObservableProperty(setOf(), undefined);
    
    public readonly selectedDayPaint: Paint = Paint.constructor();
    
    public readonly selectedPaint: Paint = Paint.constructor();
    
    
    
    
    public measure(width: number, height: number, displayMetrics: DisplayMetrics){
        .measure(width, height, displayMetrics);
        setAndroidGraphicsPaintTextSize(selectedDayPaint, getAndroidGraphicsPaintTextSize(dayPaint));
    }
    
    public readonly drawDay_dateAlone: DateAlone = new DateAlone(0, 0, 0);
    
    public drawDay(
        canvas: Canvas,
        showingMonth: DateAlone,
        day: DateAlone,
        displayMetrics: DisplayMetrics,
        outer: RectF,
        inner: RectF
    ){
        if (dates.value.contains(day)) {
            const leftDate = ComLightningkiteKhrysalisTimeDateAloneSetAddDayOfMonth(ComLightningkiteKhrysalisTimeDateAloneSet(drawDay_dateAlone, day), -1);
            
            const left = dates.value.contains(leftDate);
            
            const rightDate = ComLightningkiteKhrysalisTimeDateAloneSetAddDayOfMonth(ComLightningkiteKhrysalisTimeDateAloneSet(drawDay_dateAlone, day), 1);
            
            const right = dates.value.contains(rightDate);
            
            
            if(left.not() && right.not()){
                CalendarDrawing.INSTANCE.dayBackground(canvas, inner, this.selectedPaint);
            }else if(left.not() && right){
                CalendarDrawing.INSTANCE.dayBackgroundStart(canvas, inner, outer, this.selectedPaint);
            }else if(left && right.not()){
                CalendarDrawing.INSTANCE.dayBackgroundEnd(canvas, inner, outer, this.selectedPaint);
            }else if(left && right){
                CalendarDrawing.INSTANCE.dayBackgroundMid(canvas, inner, outer, this.selectedPaint);
            }else {
                CalendarDrawing.INSTANCE.dayBackground(canvas, inner, this.selectedPaint);
            };
            CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.selectedDayPaint);
        } else {
            CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.dayPaint);
        }
    }
    
    public onTap(day: DateAlone){
        this.adding = ;
        return onTouchMove(day);
    }
    
    public adding: Boolean = false;
    
    public onTouchDown(day: DateAlone): Boolean{
        this.adding = ;
        onTouchMove(day);
        return true;
    }
    
    public onTouchMove(day: DateAlone): Boolean{
        if (this.adding) {
            if () {
                dates.value = KotlinCollectionsSetPlus(dates.value, day);
            }
        } else {
            dates.value = KotlinCollectionsIterableToSet(dates.value.filter((it) => !(it.equals(day))));
        }
        return true;
    }
    
    public onTouchUp(day: DateAlone): Boolean{
        return true;
    }
}

