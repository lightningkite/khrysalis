"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MonthCVD_shared_1 = require("./MonthCVD.shared");
//! Declares com.lightningkite.khrysalis.views.SelectDateMonthCVD
class SelectDateMonthCVD extends MonthCVD_shared_1.MonthCVD {
    constructor() { super(); }
    generateAccessibilityView() { return null; }
    drawDay(canvas, showingMonth, day, displayMetrics, outer, inner) {
        if (day.equals(this.selected.value)) {
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
