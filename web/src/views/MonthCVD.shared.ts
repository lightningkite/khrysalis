// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/MonthCVD.shared.kt
// Package: com.lightningkite.khrysalis.views
import { DisplayMetrics } from './DisplayMetrics.actual'
import { getAnimationFrame } from '../delay.actual'
import { androidGraphicsCanvasDrawTextCentered } from './draw/Canvas.actual'
import { MutableObservableProperty } from '../observables/MutableObservableProperty.shared'
import { getJavaUtilDateDateAlone } from '../time/Date.actual'
import { NumberRange } from '../Kotlin'
import { CustomViewDelegate } from './CustomViewDelegate.shared'
import { StandardObservableProperty } from '../observables/StandardObservableProperty.shared'
import { kotlinIntFloorDiv, kotlinIntFloorMod } from '../Math.shared'
import { ioReactivexDisposablesDisposableForever, ioReactivexDisposablesDisposableUntil } from '../rx/DisposeCondition.actual'
import { dateAloneMod, dateAloneModRelative } from '../time/Date.actual'
import { Paint } from './draw/Paint.actual'
import { TimeNames } from '../time/TimeNames.actual'
import { comLightningkiteKhrysalisTimeDateAloneSet } from '../time/DateAlone.shared'
import { SubscriptionLike } from 'rxjs'
import { DateAlone } from '../time/DateAlone.actual'
import { applyAlphaToColor, numberToColor } from './Colors.actual'
import { customViewInvalidate } from './CustomView.actual'
import { RectF } from './geometry/RectF.actual'
import { runOrNull } from '../kotlin/Language'
import { pathFromLTRB } from './draw/Path.actual'
import { comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy } from '../observables/ObservableProperty.ext.shared'

//! Declares com.lightningkite.khrysalis.views.MonthCVD
export class MonthCVD extends CustomViewDelegate {
    public constructor() {
        super();
        this.currentMonthObs = new StandardObservableProperty<DateAlone>(dateAloneMod(getJavaUtilDateDateAlone(new Date()), Date.prototype.setDate, 1), undefined);
        this.dragEnabled = true;
        ioReactivexDisposablesDisposableForever<SubscriptionLike>(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy<DateAlone>(this.currentMonthObs, undefined, undefined,  (value: DateAlone): void => {
                    this?.postInvalidate();
        }));
        this.labelFontSp = 12;
        this.dayFontSp = 16;
        this.internalPaddingDp = 8;
        this.dayCellMarginDp = 8;
        this.internalPadding = 0;
        this.dayLabelHeight = 0;
        this.dayCellHeight = 0;
        this.dayCellWidth = 0;
        this.dayCellMargin = 0;
        this._currentOffset = 0;
        this.dragStartX = 0;
        this.lastOffset = 0;
        this.lastOffsetTime = 0;
        this.DRAGGING_NONE = (-1);
        this.draggingId = this.DRAGGING_NONE;
        this.labelPaint = new Paint();
        this.dayPaint = new Paint();
        this.labelPaint.isAntiAlias = true;
        this.labelPaint.style = Paint.Style.FILL;
        this.labelPaint.color = numberToColor(0xFF808080);
        this.dayPaint.isAntiAlias = true;
        this.dayPaint.style = Paint.Style.FILL;
        this.dayPaint.color = numberToColor(0xFF202020);
        ioReactivexDisposablesDisposableUntil<SubscriptionLike>(getAnimationFrame().subscribe((timePassed: number): void => {
                    if (this.draggingId === this.DRAGGING_NONE && !(this.currentOffset === 0)) {
                        let newOffset: number = this.currentOffset * Math.max(0, (1 - 8 * timePassed));
                        
                        const min: number = 0.001;
                        
                        if (newOffset > min) {
                            newOffset = newOffset - min
                        } else if (newOffset < (-min)) {
                            newOffset = newOffset + min
                        } else  {
                            newOffset = 0
                        }
                        this.currentOffset = newOffset;
                    }
        }, undefined, undefined), this.removed);
        this.calcMonth = new DateAlone(1, 1, 1);
        this.calcMonthB = new DateAlone(0, 0, 0);
        this.drawDate = new DateAlone(1, 1, 1);
        this.rectForReuse = new RectF();
        this.rectForReuseB = new RectF();
        this.isTap = false;
        this.dragStartY = 0;
    }
    
    public generateAccessibilityView(): (HTMLElement | null) { 
        return null; 
    }
    
    public readonly currentMonthObs: MutableObservableProperty<DateAlone>;
    
    //! Declares com.lightningkite.khrysalis.views.MonthCVD.currentMonth
    public get currentMonth(): DateAlone { return this.currentMonthObs.value; }
    public set currentMonth(value: DateAlone) {
        this.currentMonthObs.value = value;
    }
    
    
    public dragEnabled: boolean;
    
    
    
    
    public labelFontSp: number;
    
    public dayFontSp: number;
    
    public internalPaddingDp: number;
    
    public dayCellMarginDp: number;
    
    private internalPadding: number;
    
    private dayLabelHeight: number;
    
    private dayCellHeight: number;
    
    private dayCellWidth: number;
    
    private dayCellMargin: number;
    
    
    private _currentOffset: number;
    
    //! Declares com.lightningkite.khrysalis.views.MonthCVD.currentOffset
    public get currentOffset(): number {
        return this._currentOffset;
    }
    public set currentOffset(value: number) {
        this._currentOffset = value;
        const temp196 = this.customView;
        if(temp196 !== null) { 
            customViewInvalidate(temp196)
        };
    }
    
    private dragStartX: number;
    
    private lastOffset: number;
    
    private lastOffsetTime: number;
    
    private readonly DRAGGING_NONE: number;
    
    private draggingId: number;
    
    
    public animateNextMonth(): void {
        dateAloneModRelative(this.currentMonthObs.value, Date.prototype.getMonth, Date.prototype.setMonth, 1);
        this.currentMonthObs.update();
        this.currentOffset = 1;
    }
    
    public animatePreviousMonth(): void {
        dateAloneModRelative(this.currentMonthObs.value, Date.prototype.getMonth, Date.prototype.setMonth, (-1));
        this.currentMonthObs.update();
        this.currentOffset = (-1);
    }
    
    public readonly labelPaint: Paint;
    
    public readonly dayPaint: Paint;
    
    
    
    
    private readonly calcMonth: DateAlone;
    
    
    public dayAtPixel(x: number, y: number, existing: (DateAlone | null) = null): (DateAlone | null) {
        if (y < this.dayLabelHeight) { return null }
        //        val columnRaw = (x / dayCellWidth - (dayCellWidth + currentOffset) * 7).toInt()
        const columnRawBeforeDrag = x / this.dayCellWidth;
        
        const columnDrag = this.currentOffset * 7;
        
        const columnRaw = Math.floor((columnDrag + columnRawBeforeDrag));
        
        const column = kotlinIntFloorMod(columnRaw, 7);
        
        const monthOffset = kotlinIntFloorDiv(columnRaw, 7);
        
        const row = Math.floor(((y - this.dayLabelHeight) / this.dayCellHeight));
        
        if (row < 0 || row > 5) { return null }
        if (column < 0 || column > 6) { return null }
        return this.dayAt(dateAloneModRelative(comLightningkiteKhrysalisTimeDateAloneSet(this.calcMonth, this.currentMonth), Date.prototype.getMonth, Date.prototype.setMonth, monthOffset), row, column, existing ?? new DateAlone(0, 0, 0));
    }
    
    public dayAt(month: DateAlone, row: number, column: number, existing: DateAlone = new DateAlone(0, 0, 0)): DateAlone {
        return dateAloneModRelative(dateAloneMod(dateAloneMod(comLightningkiteKhrysalisTimeDateAloneSet(existing, month), Date.prototype.setDate, 1), function(this: Date, newDay: number){
                    const diff = newDay - this.getDay();
                    this.setDate(this.getDate() + diff);
            }, 1)
        , Date.prototype.getDate, Date.prototype.setDate, row * 7 + column);
    }
    
    public measure(width: number, height: number, displayMetrics: DisplayMetrics): void {
        this.internalPadding = displayMetrics.density * this.internalPaddingDp;
        this.dayCellMargin = displayMetrics.density * this.dayCellMarginDp;
        this.labelPaint.textSize = this.labelFontSp * displayMetrics.scaledDensity;
        this.dayPaint.textSize = this.dayFontSp * displayMetrics.scaledDensity;
        this.dayLabelHeight = this.labelPaint.textSize * 1.5 + this.internalPadding * 2;
        this.dayCellWidth = width / 7;
        this.dayCellHeight = (height - this.dayLabelHeight) / 6;
    }
    
    private readonly calcMonthB: DateAlone;
    
    
    public draw(canvas: CanvasRenderingContext2D, width: number, height: number, displayMetrics: DisplayMetrics): void {
        this.measure(width, height, displayMetrics);
        if (this.currentOffset > 0) {
            //draw past month and current month
            this.drawMonth(canvas, (this.currentOffset - 1) * width, width, dateAloneModRelative(comLightningkiteKhrysalisTimeDateAloneSet(this.calcMonthB, this.currentMonth), Date.prototype.getMonth, Date.prototype.setMonth, (-1)), displayMetrics);
            this.drawMonth(canvas, this.currentOffset * width, width, this.currentMonth, displayMetrics);
        } else { if (this.currentOffset < 0) {
                //draw future month and current month
                this.drawMonth(canvas, (this.currentOffset + 1) * width, width, dateAloneModRelative(comLightningkiteKhrysalisTimeDateAloneSet(this.calcMonthB, this.currentMonth), Date.prototype.getMonth, Date.prototype.setMonth, 1), displayMetrics);
                this.drawMonth(canvas, this.currentOffset * width, width, this.currentMonth, displayMetrics);
            } else {
                //Nice, it's exactly zero.  We can just draw one.
                this.drawMonth(canvas, this.currentOffset * width, width, this.currentMonth, displayMetrics);
        } }
    }
    
    private drawDate: DateAlone;
    
    private readonly rectForReuse: RectF;
    
    private readonly rectForReuseB: RectF;
    
    public drawMonth(canvas: CanvasRenderingContext2D, xOffset: number, width: number, month: DateAlone, displayMetrics: DisplayMetrics): void {
        for (const day of new NumberRange(1, 7)) {
            const col = day - 1;
            
            this.rectForReuse.set(xOffset + col * this.dayCellWidth - 0.01, (-0.01), xOffset + (col + 1) * this.dayCellWidth + 0.01, this.dayLabelHeight + 0.01);
            this.rectForReuseB.set(this.rectForReuse);
            this.rectForReuse.inset(this.internalPadding, this.internalPadding);
            this.drawLabel(canvas, day, displayMetrics, this.rectForReuse, this.rectForReuseB);
        }
        for (const row of new NumberRange(0, 5)) {
            for (const col of new NumberRange(0, 6)) {
                const day = this.dayAt(month, row, col, this.drawDate);
                
                this.rectForReuse.set(xOffset + col * this.dayCellWidth - 0.01, this.dayLabelHeight + row * this.dayCellHeight - 0.01, xOffset + (col + 1) * this.dayCellWidth + 0.01, this.dayLabelHeight + (row + 1) * this.dayCellHeight + 0.01);
                if (this.rectForReuse.left > width) {
                    continue;
                }
                if (this.rectForReuse.right < 0) {
                    continue;
                }
                this.rectForReuseB.set(this.rectForReuse);
                this.rectForReuse.inset(this.dayCellMargin, this.dayCellMargin);
                this.drawDay(canvas, month, day, displayMetrics, this.rectForReuseB, this.rectForReuse);
            }
        }
    }
    
    public drawLabel(canvas: CanvasRenderingContext2D, dayOfWeek: number, displayMetrics: DisplayMetrics, outer: RectF, inner: RectF): void {
        CalendarDrawing.INSTANCE.label(canvas, dayOfWeek, inner, this.labelPaint);
    }
    
    public drawDay(canvas: CanvasRenderingContext2D, showingMonth: DateAlone, day: DateAlone, displayMetrics: DisplayMetrics, outer: RectF, inner: RectF): void {
        CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, outer, this.dayPaint);
    }
    
    public isTap: boolean;
    
    public dragStartY: number;
    
    public onTap(day: DateAlone): void {}
    
    public onTouchDownDate(day: DateAlone): boolean { 
        return false; 
    }
    public onTouchDown(id: number, x: number, y: number, width: number, height: number): boolean {
        const day = this.dayAtPixel(x, y, undefined);
        
        const it_272 = day;
        if (it_272 !== null) { 
            if (this.onTouchDownDate(it_272)) {
                return true;
            }
        }
        this.dragStartX = x / width;
        this.dragStartY = y / height;
        this.draggingId = id;
        this.lastOffsetTime = Date.now();
        this.isTap = true;
        
        return true;
    }
    
    public onTouchMoveDate(day: DateAlone): boolean { 
        return false; 
    }
    public onTouchMove(id: number, x: number, y: number, width: number, height: number): boolean {
        if (this.draggingId === id) {
            this.lastOffset = this.currentOffset;
            this.lastOffsetTime = Date.now();
            if (this.dragEnabled) {
                this.currentOffset = (x / width) - this.dragStartX;
                if (Math.abs((x / width - this.dragStartX)) > 0.05 || Math.abs((y / height - this.dragStartY)) > 0.05) {
                    this.isTap = false;
                }
            }
        } else {
            const it_284 = this.dayAtPixel(x, y, undefined);
            if (it_284 !== null) { 
                return this.onTouchMoveDate(it_284);
            }
        }
        return true;
    }
    
    public onTouchUpDate(day: DateAlone): boolean { 
        return false; 
    }
    public onTouchUp(id: number, x: number, y: number, width: number, height: number): boolean {
        if (this.draggingId === id) {
            if (this.isTap) {
                const it_286 = this.dayAtPixel(x, y, undefined);
                if (it_286 !== null) { 
                    this.onTap(it_286);
                }
            } else { if (this.dragEnabled) {
                    const weighted = this.currentOffset + (this.currentOffset - this.lastOffset) * 200 / (Date.now() - this.lastOffsetTime);
                    
                    if (weighted > 0.5) {
                        //shift right one
                        dateAloneModRelative(this.currentMonthObs.value, Date.prototype.getMonth, Date.prototype.setMonth, (-1));
                        this.currentMonthObs.update();
                        this.currentOffset = this.currentOffset - 1;
                    } else { if (weighted < (-0.5)) {
                            //shift left one
                            dateAloneModRelative(this.currentMonthObs.value, Date.prototype.getMonth, Date.prototype.setMonth, 1);
                            this.currentMonthObs.update();
                            this.currentOffset = this.currentOffset + 1;
                    } }
            } }
            this.draggingId = this.DRAGGING_NONE;
        } else {
            const it_303 = this.dayAtPixel(x, y, undefined);
            if (it_303 !== null) { 
                return this.onTouchUpDate(it_303);
            }
        }
        return true;
    }
    
    
    public sizeThatFitsWidth(width: number, height: number): number {
        return this.dayLabelHeight * 28;
    }
    
    public sizeThatFitsHeight(width: number, height: number): number {
        return width * 6 / 7 + this.dayLabelHeight;
    }
}

//! Declares com.lightningkite.khrysalis.views.CalendarDrawing
export class CalendarDrawing {
    private constructor() {
    }
    public static INSTANCE = new CalendarDrawing();
    
    day(canvas: CanvasRenderingContext2D, month: DateAlone, date: DateAlone, inner: RectF, paint: Paint): void {
        if (date.month === month.month && date.year === month.year) {
            androidGraphicsCanvasDrawTextCentered(canvas, date.day.toString(), inner.centerX(), inner.centerY(), paint);
        } else {
            const originalColor = paint.color;
            
            let myPaint = paint;
            
            myPaint.color = applyAlphaToColor(paint.color, 64);
            androidGraphicsCanvasDrawTextCentered(canvas, date.day.toString(), inner.centerX(), inner.centerY(), myPaint);
            myPaint.color = originalColor;
        }
    }
    
    label(canvas: CanvasRenderingContext2D, dayOfWeek: number, inner: RectF, paint: Paint): void {
        const text = TimeNames.INSTANCE.shortWeekdayName(dayOfWeek);
        
        androidGraphicsCanvasDrawTextCentered(canvas, text, inner.centerX(), inner.centerY(), paint);
    }
    
    dayBackground(canvas: CanvasRenderingContext2D, inner: RectF, paint: Paint): void {
        canvas.beginPath(); canvas.arc(inner.centerX(), inner.centerY(), Math.min(inner.width() / 2, inner.height() / 2), 0, Math.PI * 2); paint.complete(canvas);
    }
    
    dayBackgroundStart(canvas: CanvasRenderingContext2D, inner: RectF, outer: RectF, paint: Paint): void {
        canvas.beginPath(); canvas.arc(inner.centerX(), inner.centerY(), Math.min(inner.width() / 2, inner.height() / 2), 0, Math.PI * 2); paint.complete(canvas);
        paint.render(canvas, pathFromLTRB(outer.centerX(), inner.top, outer.right, inner.bottom));
    }
    
    dayBackgroundMid(canvas: CanvasRenderingContext2D, inner: RectF, outer: RectF, paint: Paint): void {
        paint.render(canvas, pathFromLTRB(outer.left, inner.top, outer.right, inner.bottom));
    }
    
    dayBackgroundEnd(canvas: CanvasRenderingContext2D, inner: RectF, outer: RectF, paint: Paint): void {
        canvas.beginPath(); canvas.arc(inner.centerX(), inner.centerY(), Math.min(inner.width() / 2, inner.height() / 2), 0, Math.PI * 2); paint.complete(canvas);
        paint.render(canvas, pathFromLTRB(outer.left, inner.top, outer.centerX(), inner.bottom));
    }
}


