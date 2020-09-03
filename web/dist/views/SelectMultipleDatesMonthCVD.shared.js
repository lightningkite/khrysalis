"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const KotlinCollections_1 = require("../KotlinCollections");
const MonthCVD_shared_1 = require("./MonthCVD.shared");
const Paint_actual_1 = require("./draw/Paint.actual");
const lazyOp_1 = require("../kotlin/lazyOp");
const DateAlone_shared_1 = require("../time/DateAlone.shared");
const DateAlone_actual_1 = require("../time/DateAlone.actual");
const Date_actual_1 = require("../time/Date.actual");
const Kotlin_1 = require("../Kotlin");
const StandardObservableProperty_shared_1 = require("../observables/StandardObservableProperty.shared");
const DisposeCondition_actual_1 = require("../rx/DisposeCondition.actual");
//! Declares com.lightningkite.khrysalis.views.SelectMultipleDatesMonthCVD
class SelectMultipleDatesMonthCVD extends MonthCVD_shared_1.MonthCVD {
    constructor() {
        super();
        this.dates = new StandardObservableProperty_shared_1.StandardObservableProperty(new KotlinCollections_1.EqualOverrideSet([]), undefined);
        this.selectedDayPaint = new Paint_actual_1.Paint();
        this.selectedPaint = new Paint_actual_1.Paint();
        const it_357 = KotlinCollections_1.iterFirstOrNull(this.dates.value);
        if (it_357 !== null) {
            this.currentMonthObs.value = Date_actual_1.copyDateAloneMod(it_357, Date.prototype.setDate, 1);
        }
        ;
        DisposeCondition_actual_1.xDisposableForever(this.dates.onChange.subscribe((value) => {
            this === null || this === void 0 ? void 0 : this.invalidate();
        }, undefined, undefined));
        this.drawDay_dateAlone = new DateAlone_actual_1.DateAlone(0, 0, 0);
        this.adding = false;
    }
    generateAccessibilityView() {
        return null;
    }
    measure(width, height, displayMetrics) {
        super.measure(width, height, displayMetrics);
        this.selectedDayPaint.textSize = this.dayPaint.textSize;
    }
    drawDay(canvas, showingMonth, day, displayMetrics, outer, inner) {
        if (this.dates.value.has(day)) {
            const leftDate = Date_actual_1.dateAloneModRelative(DateAlone_shared_1.xDateAloneSet(this.drawDay_dateAlone, day), Date.prototype.getDate, Date.prototype.setDate, (-1));
            const left = this.dates.value.has(leftDate);
            const rightDate = Date_actual_1.dateAloneModRelative(DateAlone_shared_1.xDateAloneSet(this.drawDay_dateAlone, day), Date.prototype.getDate, Date.prototype.setDate, 1);
            const right = this.dates.value.has(rightDate);
            if ((!left) && (!right)) {
                MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackground(canvas, inner, this.selectedPaint);
            }
            else if ((!left) && right) {
                MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackgroundStart(canvas, inner, outer, this.selectedPaint);
            }
            else if (left && (!right)) {
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
        this.adding = (!lazyOp_1.some(this.dates.value, (it) => Kotlin_1.safeEq(day, it)));
        this.onTouchMoveDate(day);
    }
    onTouchDownDate(day) {
        this.adding = (!lazyOp_1.some(this.dates.value, (it) => Kotlin_1.safeEq(day, it)));
        this.onTouchMoveDate(day);
        return true;
    }
    onTouchMoveDate(day) {
        if (this.adding) {
            if ((!lazyOp_1.some(this.dates.value, (it) => Kotlin_1.safeEq(day, it)))) {
                this.dates.value = new KotlinCollections_1.EqualOverrideSet([...this.dates.value, day]);
            }
        }
        else {
            this.dates.value = new KotlinCollections_1.EqualOverrideSet(lazyOp_1.toArray(lazyOp_1.filter(this.dates.value, (it) => !Kotlin_1.safeEq(it, day))));
        }
        return true;
    }
    onTouchUpDate(day) {
        return true;
    }
}
exports.SelectMultipleDatesMonthCVD = SelectMultipleDatesMonthCVD;
//# sourceMappingURL=SelectMultipleDatesMonthCVD.shared.js.map