"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Date_actual_1 = require("../time/Date.actual");
const delay_actual_1 = require("../delay.actual");
const DateAlone_shared_1 = require("../time/DateAlone.shared");
const Kotlin_1 = require("../Kotlin");
const ObservableProperty_ext_shared_1 = require("../observables/ObservableProperty.ext.shared");
const CustomViewDelegate_shared_1 = require("./CustomViewDelegate.shared");
const Math_shared_1 = require("../Math.shared");
const StandardObservableProperty_shared_1 = require("../observables/StandardObservableProperty.shared");
const DisposeCondition_actual_1 = require("../rx/DisposeCondition.actual");
const Date_actual_2 = require("../time/Date.actual");
const Paint_actual_1 = require("./draw/Paint.actual");
const TimeNames_actual_1 = require("../time/TimeNames.actual");
const DateAlone_actual_1 = require("../time/DateAlone.actual");
const Canvas_actual_1 = require("./draw/Canvas.actual");
const Colors_actual_1 = require("./Colors.actual");
const CustomView_actual_1 = require("./CustomView.actual");
const RectF_actual_1 = require("./geometry/RectF.actual");
const Path_actual_1 = require("./draw/Path.actual");
//! Declares com.lightningkite.khrysalis.views.MonthCVD
class MonthCVD extends CustomViewDelegate_shared_1.CustomViewDelegate {
    constructor() {
        super();
        this.currentMonthObs = new StandardObservableProperty_shared_1.StandardObservableProperty(Date_actual_2.dateAloneMod(Date_actual_1.xDateDateAloneGet(new Date()), Date.prototype.setDate, 1), undefined);
        this.dragEnabled = true;
        DisposeCondition_actual_1.xDisposableForever(ObservableProperty_ext_shared_1.xObservablePropertySubscribeBy(this.currentMonthObs, undefined, undefined, (value) => {
            this === null || this === void 0 ? void 0 : this.postInvalidate();
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
        this.labelPaint = new Paint_actual_1.Paint();
        this.dayPaint = new Paint_actual_1.Paint();
        this.labelPaint.isAntiAlias = true;
        this.labelPaint.style = Paint_actual_1.Paint.Style.FILL;
        this.labelPaint.color = Colors_actual_1.numberToColor(0xFF808080);
        this.dayPaint.isAntiAlias = true;
        this.dayPaint.style = Paint_actual_1.Paint.Style.FILL;
        this.dayPaint.color = Colors_actual_1.numberToColor(0xFF202020);
        DisposeCondition_actual_1.xDisposableUntil(delay_actual_1.getAnimationFrame().subscribe((timePassed) => {
            if (this.draggingId === this.DRAGGING_NONE && !(this.currentOffset === 0)) {
                let newOffset = this.currentOffset * Math.max(0, (1 - 8 * timePassed));
                const min = 0.001;
                if (newOffset > min) {
                    newOffset = newOffset - min;
                }
                else if (newOffset < (-min)) {
                    newOffset = newOffset + min;
                }
                else {
                    newOffset = 0;
                }
                this.currentOffset = newOffset;
            }
        }, undefined, undefined), this.removed);
        this.calcMonth = new DateAlone_actual_1.DateAlone(1, 1, 1);
        this.calcMonthB = new DateAlone_actual_1.DateAlone(0, 0, 0);
        this.drawDate = new DateAlone_actual_1.DateAlone(1, 1, 1);
        this.rectForReuse = new RectF_actual_1.RectF();
        this.rectForReuseB = new RectF_actual_1.RectF();
        this.isTap = false;
        this.dragStartY = 0;
    }
    generateAccessibilityView() {
        return null;
    }
    //! Declares com.lightningkite.khrysalis.views.MonthCVD.currentMonth
    get currentMonth() { return this.currentMonthObs.value; }
    set currentMonth(value) {
        this.currentMonthObs.value = value;
    }
    //! Declares com.lightningkite.khrysalis.views.MonthCVD.currentOffset
    get currentOffset() {
        return this._currentOffset;
    }
    set currentOffset(value) {
        this._currentOffset = value;
        const temp188 = this.customView;
        if (temp188 !== null) {
            CustomView_actual_1.customViewInvalidate(temp188);
        }
        ;
    }
    animateNextMonth() {
        Date_actual_2.dateAloneModRelative(this.currentMonthObs.value, Date.prototype.getMonth, Date.prototype.setMonth, 1);
        this.currentMonthObs.update();
        this.currentOffset = 1;
    }
    animatePreviousMonth() {
        Date_actual_2.dateAloneModRelative(this.currentMonthObs.value, Date.prototype.getMonth, Date.prototype.setMonth, (-1));
        this.currentMonthObs.update();
        this.currentOffset = (-1);
    }
    dayAtPixel(x, y, existing = null) {
        if (y < this.dayLabelHeight) {
            return null;
        }
        //        val columnRaw = (x / dayCellWidth - (dayCellWidth + currentOffset) * 7).toInt()
        const columnRawBeforeDrag = x / this.dayCellWidth;
        const columnDrag = this.currentOffset * 7;
        const columnRaw = Math.floor((columnDrag + columnRawBeforeDrag));
        const column = Math_shared_1.xIntFloorMod(columnRaw, 7);
        const monthOffset = Math_shared_1.xIntFloorDiv(columnRaw, 7);
        const row = Math.floor(((y - this.dayLabelHeight) / this.dayCellHeight));
        if (row < 0 || row > 5) {
            return null;
        }
        if (column < 0 || column > 6) {
            return null;
        }
        return this.dayAt(Date_actual_2.dateAloneModRelative(DateAlone_shared_1.xDateAloneSet(this.calcMonth, this.currentMonth), Date.prototype.getMonth, Date.prototype.setMonth, monthOffset), row, column, existing !== null && existing !== void 0 ? existing : new DateAlone_actual_1.DateAlone(0, 0, 0));
    }
    dayAt(month, row, column, existing = new DateAlone_actual_1.DateAlone(0, 0, 0)) {
        return Date_actual_2.dateAloneModRelative(Date_actual_2.dateAloneMod(Date_actual_2.dateAloneMod(DateAlone_shared_1.xDateAloneSet(existing, month), Date.prototype.setDate, 1), function (newDay) {
            const diff = newDay - this.getDay();
            this.setDate(this.getDate() + diff);
        }, 1), Date.prototype.getDate, Date.prototype.setDate, row * 7 + column);
    }
    measure(width, height, displayMetrics) {
        this.internalPadding = displayMetrics.density * this.internalPaddingDp;
        this.dayCellMargin = displayMetrics.density * this.dayCellMarginDp;
        this.labelPaint.textSize = this.labelFontSp * displayMetrics.scaledDensity;
        this.dayPaint.textSize = this.dayFontSp * displayMetrics.scaledDensity;
        this.dayLabelHeight = this.labelPaint.textSize * 1.5 + this.internalPadding * 2;
        this.dayCellWidth = width / 7;
        this.dayCellHeight = (height - this.dayLabelHeight) / 6;
    }
    draw(canvas, width, height, displayMetrics) {
        this.measure(width, height, displayMetrics);
        if (this.currentOffset > 0) {
            //draw past month and current month
            this.drawMonth(canvas, (this.currentOffset - 1) * width, width, Date_actual_2.dateAloneModRelative(DateAlone_shared_1.xDateAloneSet(this.calcMonthB, this.currentMonth), Date.prototype.getMonth, Date.prototype.setMonth, (-1)), displayMetrics);
            this.drawMonth(canvas, this.currentOffset * width, width, this.currentMonth, displayMetrics);
        }
        else {
            if (this.currentOffset < 0) {
                //draw future month and current month
                this.drawMonth(canvas, (this.currentOffset + 1) * width, width, Date_actual_2.dateAloneModRelative(DateAlone_shared_1.xDateAloneSet(this.calcMonthB, this.currentMonth), Date.prototype.getMonth, Date.prototype.setMonth, 1), displayMetrics);
                this.drawMonth(canvas, this.currentOffset * width, width, this.currentMonth, displayMetrics);
            }
            else {
                //Nice, it's exactly zero.  We can just draw one.
                this.drawMonth(canvas, this.currentOffset * width, width, this.currentMonth, displayMetrics);
            }
        }
    }
    drawMonth(canvas, xOffset, width, month, displayMetrics) {
        for (const day of new Kotlin_1.NumberRange(1, 7)) {
            const col = day - 1;
            this.rectForReuse.set(xOffset + col * this.dayCellWidth - 0.01, (-0.01), xOffset + (col + 1) * this.dayCellWidth + 0.01, this.dayLabelHeight + 0.01);
            this.rectForReuseB.set(this.rectForReuse);
            this.rectForReuse.inset(this.internalPadding, this.internalPadding);
            this.drawLabel(canvas, day, displayMetrics, this.rectForReuse, this.rectForReuseB);
        }
        for (const row of new Kotlin_1.NumberRange(0, 5)) {
            for (const col of new Kotlin_1.NumberRange(0, 6)) {
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
    drawLabel(canvas, dayOfWeek, displayMetrics, outer, inner) {
        CalendarDrawing.INSTANCE.label(canvas, dayOfWeek, inner, this.labelPaint);
    }
    drawDay(canvas, showingMonth, day, displayMetrics, outer, inner) {
        CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, outer, this.dayPaint);
    }
    onTap(day) { }
    onTouchDownDate(day) {
        return false;
    }
    onTouchDown(id, x, y, width, height) {
        const day = this.dayAtPixel(x, y, undefined);
        const it_264 = day;
        if (it_264 !== null) {
            if (this.onTouchDownDate(it_264)) {
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
    onTouchMoveDate(day) {
        return false;
    }
    onTouchMove(id, x, y, width, height) {
        if (this.draggingId === id) {
            this.lastOffset = this.currentOffset;
            this.lastOffsetTime = Date.now();
            if (this.dragEnabled) {
                this.currentOffset = (x / width) - this.dragStartX;
                if (Math.abs((x / width - this.dragStartX)) > 0.05 || Math.abs((y / height - this.dragStartY)) > 0.05) {
                    this.isTap = false;
                }
            }
        }
        else {
            const it_276 = this.dayAtPixel(x, y, undefined);
            if (it_276 !== null) {
                return this.onTouchMoveDate(it_276);
            }
        }
        return true;
    }
    onTouchUpDate(day) {
        return false;
    }
    onTouchUp(id, x, y, width, height) {
        if (this.draggingId === id) {
            if (this.isTap) {
                const it_278 = this.dayAtPixel(x, y, undefined);
                if (it_278 !== null) {
                    this.onTap(it_278);
                }
            }
            else {
                if (this.dragEnabled) {
                    const weighted = this.currentOffset + (this.currentOffset - this.lastOffset) * 200 / (Date.now() - this.lastOffsetTime);
                    if (weighted > 0.5) {
                        //shift right one
                        Date_actual_2.dateAloneModRelative(this.currentMonthObs.value, Date.prototype.getMonth, Date.prototype.setMonth, (-1));
                        this.currentMonthObs.update();
                        this.currentOffset = this.currentOffset - 1;
                    }
                    else {
                        if (weighted < (-0.5)) {
                            //shift left one
                            Date_actual_2.dateAloneModRelative(this.currentMonthObs.value, Date.prototype.getMonth, Date.prototype.setMonth, 1);
                            this.currentMonthObs.update();
                            this.currentOffset = this.currentOffset + 1;
                        }
                    }
                }
            }
            this.draggingId = this.DRAGGING_NONE;
        }
        else {
            const it_295 = this.dayAtPixel(x, y, undefined);
            if (it_295 !== null) {
                return this.onTouchUpDate(it_295);
            }
        }
        return true;
    }
    sizeThatFitsWidth(width, height) {
        return this.dayLabelHeight * 28;
    }
    sizeThatFitsHeight(width, height) {
        return width * 6 / 7 + this.dayLabelHeight;
    }
}
exports.MonthCVD = MonthCVD;
//! Declares com.lightningkite.khrysalis.views.CalendarDrawing
class CalendarDrawing {
    constructor() {
    }
    day(canvas, month, date, inner, paint) {
        if (date.month === month.month && date.year === month.year) {
            Canvas_actual_1.xCanvasDrawTextCentered(canvas, date.day.toString(), inner.centerX(), inner.centerY(), paint);
        }
        else {
            const originalColor = paint.color;
            let myPaint = paint;
            myPaint.color = Colors_actual_1.applyAlphaToColor(paint.color, 64);
            Canvas_actual_1.xCanvasDrawTextCentered(canvas, date.day.toString(), inner.centerX(), inner.centerY(), myPaint);
            myPaint.color = originalColor;
        }
    }
    label(canvas, dayOfWeek, inner, paint) {
        const text = TimeNames_actual_1.TimeNames.INSTANCE.shortWeekdayName(dayOfWeek);
        Canvas_actual_1.xCanvasDrawTextCentered(canvas, text, inner.centerX(), inner.centerY(), paint);
    }
    dayBackground(canvas, inner, paint) {
        canvas.beginPath();
        canvas.arc(inner.centerX(), inner.centerY(), Math.min(inner.width() / 2, inner.height() / 2), 0, Math.PI * 2);
        paint.complete(canvas);
    }
    dayBackgroundStart(canvas, inner, outer, paint) {
        canvas.beginPath();
        canvas.arc(inner.centerX(), inner.centerY(), Math.min(inner.width() / 2, inner.height() / 2), 0, Math.PI * 2);
        paint.complete(canvas);
        paint.render(canvas, Path_actual_1.pathFromLTRB(outer.centerX(), inner.top, outer.right, inner.bottom));
    }
    dayBackgroundMid(canvas, inner, outer, paint) {
        paint.render(canvas, Path_actual_1.pathFromLTRB(outer.left, inner.top, outer.right, inner.bottom));
    }
    dayBackgroundEnd(canvas, inner, outer, paint) {
        canvas.beginPath();
        canvas.arc(inner.centerX(), inner.centerY(), Math.min(inner.width() / 2, inner.height() / 2), 0, Math.PI * 2);
        paint.complete(canvas);
        paint.render(canvas, Path_actual_1.pathFromLTRB(outer.left, inner.top, outer.centerX(), inner.bottom));
    }
}
exports.CalendarDrawing = CalendarDrawing;
CalendarDrawing.INSTANCE = new CalendarDrawing();
//# sourceMappingURL=MonthCVD.shared.js.map