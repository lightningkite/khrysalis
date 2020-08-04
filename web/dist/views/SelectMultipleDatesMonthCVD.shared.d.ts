import { DisplayMetrics } from './DisplayMetrics.actual';
import { MonthCVD } from './MonthCVD.shared';
import { Paint } from './draw/Paint.actual';
import { DateAlone } from '../time/DateAlone.actual';
import { StandardObservableProperty } from '../observables/StandardObservableProperty.shared';
import { RectF } from './geometry/RectF.actual';
export declare class SelectMultipleDatesMonthCVD extends MonthCVD {
    constructor();
    generateAccessibilityView(): (HTMLElement | null);
    readonly dates: StandardObservableProperty<Set<DateAlone>>;
    readonly selectedDayPaint: Paint;
    readonly selectedPaint: Paint;
    measure(width: number, height: number, displayMetrics: DisplayMetrics): void;
    readonly drawDay_dateAlone: DateAlone;
    drawDay(canvas: CanvasRenderingContext2D, showingMonth: DateAlone, day: DateAlone, displayMetrics: DisplayMetrics, outer: RectF, inner: RectF): void;
    onTap(day: DateAlone): void;
    adding: boolean;
    onTouchDownDate(day: DateAlone): boolean;
    onTouchMoveDate(day: DateAlone): boolean;
    onTouchUpDate(day: DateAlone): boolean;
}
