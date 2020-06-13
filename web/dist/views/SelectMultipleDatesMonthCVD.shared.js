"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Date_actual_1 = require("../time/Date.actual");
const iterable_operator_1 = require("iterable-operator");
const MonthCVD_shared_1 = require("./MonthCVD.shared");
const DateAlone_shared_1 = require("../time/DateAlone.shared");
//! Declares com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD
class SelectMultipleDatesMonthCVD extends MonthCVD_shared_1.MonthCVD {
    constructor() { super(); }
    generateAccessibilityView() { return null; }
    measure(width, height, displayMetrics) {
        super.measure(width, height, displayMetrics);
        this.selectedDayPaint.textSize = this.dayPaint.textSize;
    }
    drawDay(canvas, showingMonth, day, displayMetrics, outer, inner) {
        if (this.dates.value.has(day)) {
            const leftDate = Date_actual_1.dateAloneModRelative(DateAlone_shared_1.comLightningkiteKhrysalisTimeDateAloneSet(this.drawDay_dateAlone, day), Date.prototype.getDate, Date.prototype.setDate, -1);
            const left = this.dates.value.has(leftDate);
            const rightDate = Date_actual_1.dateAloneModRelative(DateAlone_shared_1.comLightningkiteKhrysalisTimeDateAloneSet(this.drawDay_dateAlone, day), Date.prototype.getDate, Date.prototype.setDate, 1);
            const right = this.dates.value.has(rightDate);
            if (!left && !right) {
                MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackground(canvas, inner, this.selectedPaint);
            }
            else if (!left && right) {
                MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackgroundStart(canvas, inner, outer, this.selectedPaint);
            }
            else if (left && !right) {
                MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackgroundEnd(canvas, inner, outer, this.selectedPaint);
            }
            else if (left && right) {
                MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackgroundMid(canvas, inner, outer, this.selectedPaint);
            }
            else {
                MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackground(canvas, inner, this.selectedPaint);
            }
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.selectedDayPaint);
        }
        else {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.dayPaint);
        }
    }
    onTap(day) {
        this.adding = (!iterable_operator_1.some(this.dates.value, (it) => day.equals(it)));
        this.onTouchMoveDate(day);
    }
    onTouchDownDate(day) {
        this.adding = (!iterable_operator_1.some(this.dates.value, (it) => day.equals(it)));
        this.onTouchMoveDate(day);
        return true;
    }
    onTouchMoveDate(day) {
        if (this.adding) {
            if ((!iterable_operator_1.some(this.dates.value, (it) => day.equals(it)))) {
                this.dates.value = new Set([...this.dates.value, day]);
            }
        }
        else {
            this.dates.value = iterable_operator_1.toSet(iterable_operator_1.toArray(iterable_operator_1.filter(this.dates.value, (it) => !(it.equals(day)))));
        }
        return true;
    }
    onTouchUpDate(day) {
        return true;
    }
}
exports.SelectMultipleDatesMonthCVD = SelectMultipleDatesMonthCVD;
