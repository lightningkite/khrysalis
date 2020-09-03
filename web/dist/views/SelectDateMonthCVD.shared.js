"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MonthCVD_shared_1 = require("./MonthCVD.shared");
const Paint_actual_1 = require("./draw/Paint.actual");
const Kotlin_1 = require("../Kotlin");
const Date_actual_1 = require("../time/Date.actual");
const StandardObservableProperty_shared_1 = require("../observables/StandardObservableProperty.shared");
const DisposeCondition_actual_1 = require("../rx/DisposeCondition.actual");
//! Declares com.lightningkite.khrysalis.views.SelectDateMonthCVD
class SelectDateMonthCVD extends MonthCVD_shared_1.MonthCVD {
    constructor() {
        super();
        this.selected = new StandardObservableProperty_shared_1.StandardObservableProperty(null, undefined);
        const it_338 = this.selected.value;
        if (it_338 !== null) {
            this.currentMonthObs.value = Date_actual_1.copyDateAloneMod(it_338, Date.prototype.setDate, 1);
        }
        ;
        DisposeCondition_actual_1.xDisposableForever(this.selected.onChange.subscribe((value) => {
            this === null || this === void 0 ? void 0 : this.invalidate();
        }, undefined, undefined));
        this.selectedDayPaint = new Paint_actual_1.Paint();
        this.selectedPaint = new Paint_actual_1.Paint();
    }
    generateAccessibilityView() {
        return null;
    }
    drawDay(canvas, showingMonth, day, displayMetrics, outer, inner) {
        if (Kotlin_1.safeEq(day, this.selected.value)) {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.dayBackground(canvas, inner, this.selectedPaint);
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.selectedDayPaint);
        }
        else {
            MonthCVD_shared_1.CalendarDrawing.INSTANCE.day(canvas, showingMonth, day, inner, this.dayPaint);
        }
    }
    measure(width, height, displayMetrics) {
        super.measure(width, height, displayMetrics);
        this.selectedDayPaint.textSize = this.dayPaint.textSize;
    }
    onTap(day) {
        this.selected.value = day;
    }
}
exports.SelectDateMonthCVD = SelectDateMonthCVD;
//# sourceMappingURL=SelectDateMonthCVD.shared.js.map