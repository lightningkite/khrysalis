"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MonthCVD_shared_1 = require("./MonthCVD.shared");
const Paint_actual_1 = require("./draw/Paint.actual");
const ObservableProperty_ext_shared_1 = require("../observables/ObservableProperty.ext.shared");
const DateAlone_actual_1 = require("../time/DateAlone.actual");
const Kotlin_1 = require("../Kotlin");
const Date_actual_1 = require("../time/Date.actual");
const StandardObservableProperty_shared_1 = require("../observables/StandardObservableProperty.shared");
const DisposeCondition_actual_1 = require("../rx/DisposeCondition.actual");
//! Declares com.lightningkite.khrysalis.views.SelectDateRangeMonthCVD
class SelectDateRangeMonthCVD extends MonthCVD_shared_1.MonthCVD {
    constructor() {
        super();
        this.draggingStart = true;
        this.start = new StandardObservableProperty_shared_1.StandardObservableProperty(null, undefined);
        this.endInclusive = new StandardObservableProperty_shared_1.StandardObservableProperty(null, undefined);
        const it_341 = this.start.value;
        if (it_341 !== null) {
            this.currentMonthObs.value = Date_actual_1.copyDateAloneMod(it_341, Date.prototype.setDate, 1);
        }
        ;
        DisposeCondition_actual_1.xDisposableForever(this.start.onChange.subscribe((value) => {
            this === null || this === void 0 ? void 0 : this.invalidate();
        }, undefined, undefined));
        DisposeCondition_actual_1.xDisposableForever(ObservableProperty_ext_shared_1.xObservablePropertySubscribeBy(this.endInclusive, undefined, undefined, (value) => {
            this === null || this === void 0 ? void 0 : this.invalidate();
        }));
        this.selectedDayPaint = new Paint_actual_1.Paint();
        this.selectedPaint = new Paint_actual_1.Paint();
        this.drawDay_dateAlone = new DateAlone_actual_1.DateAlone(0, 0, 0);
        this.startedDraggingOn = null;
    }
    generateAccessibilityView() {
        return null;
    }
    measure(width, height, displayMetrics) {
        super.measure(width, height, displayMetrics);
        this.selectedDayPaint.textSize = this.dayPaint.textSize;
    }
    drawDay(canvas, showingMonth, day, displayMetrics, outer, inner) {
        var _a, _b, _c, _d, _e, _f;
        if (Kotlin_1.safeEq(day, this.start.value) && (Kotlin_1.safeEq(day, this.endInclusive.value) || this.endInclusive.value === null)) {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackground(canvas, inner, this.selectedPaint);
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.selectedDayPaint);
        }
        else if (Kotlin_1.safeEq(day, this.start.value)) {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackgroundStart(canvas, inner, outer, this.selectedPaint);
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.selectedDayPaint);
        }
        else if (Kotlin_1.safeEq(day, this.endInclusive.value)) {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackgroundEnd(canvas, inner, outer, this.selectedPaint);
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.selectedDayPaint);
        }
        else if (day.comparable > ((_c = ((_b = (_a = this.start.value) === null || _a === void 0 ? void 0 : _a.comparable) !== null && _b !== void 0 ? _b : null)) !== null && _c !== void 0 ? _c : 2147483647) && day.comparable < ((_f = ((_e = (_d = this.endInclusive.value) === null || _d === void 0 ? void 0 : _d.comparable) !== null && _e !== void 0 ? _e : null)) !== null && _f !== void 0 ? _f : -2147483648)) {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackgroundMid(canvas, inner, outer, this.selectedPaint);
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.selectedDayPaint);
        }
        else {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.dayPaint);
        }
    }
    onTap(day) {
        if (this.start.value !== null && Kotlin_1.safeEq(this.start.value, this.endInclusive.value) && day.comparable > this.start.value.comparable) {
            this.endInclusive.value = day;
        }
        else {
            this.start.value = day;
            this.endInclusive.value = day;
        }
    }
    onTouchDownDate(day) {
        if (!Kotlin_1.safeEq(day, this.start.value) && !Kotlin_1.safeEq(day, this.endInclusive.value)) {
            return false;
        }
        this.startedDraggingOn = day;
        //If on start/end - drag
        //If after, extend
        //If before, extend
        //If middle, collapse all
        const startValue = this.start.value;
        const endInclusiveValue = this.endInclusive.value;
        if (startValue === null || endInclusiveValue === null) {
            this.start.value = day;
            this.endInclusive.value = day;
            this.draggingStart = false;
        }
        else if (Kotlin_1.safeEq(day, endInclusiveValue)) {
            this.draggingStart = false;
        }
        else if (Kotlin_1.safeEq(day, startValue)) {
            this.draggingStart = true;
        }
        else if (day.comparable > endInclusiveValue.comparable && Kotlin_1.safeEq(startValue, endInclusiveValue)) {
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
        if (startValue === null || endInclusiveValue === null) {
        }
        else if (this.draggingStart && day.comparable > endInclusiveValue.comparable) {
            this.start.value = this.endInclusive.value;
            this.endInclusive.value = day;
            this.draggingStart = false;
            return true;
        }
        else if ((!this.draggingStart) && day.comparable < startValue.comparable) {
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
//# sourceMappingURL=SelectDateRangeMonthCVD.shared.js.map