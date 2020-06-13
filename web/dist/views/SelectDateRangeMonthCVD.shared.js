"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MonthCVD_shared_1 = require("./MonthCVD.shared");
//! Declares com.lightningkite.khrysalis.views.SelectDateRangeMonthCVD
class SelectDateRangeMonthCVD extends MonthCVD_shared_1.MonthCVD {
    constructor() { super(); }
    generateAccessibilityView() { return null; }
    measure(width, height, displayMetrics) {
        super.measure(width, height, displayMetrics);
        this.selectedDayPaint.textSize = this.dayPaint.textSize;
    }
    drawDay(canvas, showingMonth, day, displayMetrics, outer, inner) {
        var _a, _b, _c, _d;
        if (day.equals(this.start.value) && (day.equals(this.endInclusive.value) || this.endInclusive.value.equals(null))) {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackground(canvas, inner, this.selectedPaint);
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.selectedDayPaint);
        }
        else if (day.equals(this.start.value)) {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackgroundStart(canvas, inner, outer, this.selectedPaint);
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.selectedDayPaint);
        }
        else if (day.equals(this.endInclusive.value)) {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackgroundEnd(canvas, inner, outer, this.selectedPaint);
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.selectedDayPaint);
        }
        else if (day.comparable > ((_b = (_a = this.start.value) === null || _a === void 0 ? void 0 : _a.comparable) !== null && _b !== void 0 ? _b : 2147483647) && day.comparable < ((_d = (_c = this.endInclusive.value) === null || _c === void 0 ? void 0 : _c.comparable) !== null && _d !== void 0 ? _d : -2147483648)) {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackgroundMid(canvas, inner, outer, this.selectedPaint);
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.selectedDayPaint);
        }
        else {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.dayPaint);
        }
    }
    onTap(day) {
        if (!(this.start.value.equals(null)) && this.start.value.equals(this.endInclusive.value) && day.comparable > this.start.value.comparable) {
            this.endInclusive.value = day;
        }
        else {
            this.start.value = day;
            this.endInclusive.value = day;
        }
    }
    onTouchDownDate(day) {
        if (!(day.equals(this.start.value)) && !(day.equals(this.endInclusive.value))) {
            return false;
        }
        this.startedDraggingOn = day;
        //If on start/end - drag
        //If after, extend
        //If before, extend
        //If middle, collapse all
        const startValue = this.start.value;
        const endInclusiveValue = this.endInclusive.value;
        if (startValue.equals(null) || endInclusiveValue.equals(null)) {
            this.start.value = day;
            this.endInclusive.value = day;
            this.draggingStart = false;
        }
        else if (day.equals(endInclusiveValue)) {
            this.draggingStart = false;
        }
        else if (day.equals(startValue)) {
            this.draggingStart = true;
        }
        else if (day.comparable > endInclusiveValue.comparable && startValue.equals(endInclusiveValue)) {
            this.endInclusive.value = day;
            this.draggingStart = false;
        }
        else {
            this.start.value = day;
            this.endInclusive.value = day;
            this.draggingStart = false;
        }
        return true;
    }
    onTouchMoveDate(day) {
        const startValue = this.start.value;
        const endInclusiveValue = this.endInclusive.value;
        if (startValue.equals(null) || endInclusiveValue.equals(null)) {
        }
        else if (this.draggingStart && day.comparable > endInclusiveValue.comparable) {
            this.start.value = this.endInclusive.value;
            this.endInclusive.value = day;
            this.draggingStart = false;
            return true;
        }
        else if (!this.draggingStart && day.comparable < startValue.comparable) {
            this.endInclusive.value = this.start.value;
            this.start.value = day;
            this.draggingStart = true;
            return true;
        }
        const obs = this.draggingStart ? this.start : this.endInclusive;
        obs.value = day;
        return true;
    }
    onTouchUpDate(day) {
        this.onTouchMoveDate(day);
        return true;
    }
}
exports.SelectDateRangeMonthCVD = SelectDateRangeMonthCVD;
