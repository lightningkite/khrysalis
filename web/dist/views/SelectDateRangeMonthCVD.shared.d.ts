import { DisplayMetrics } from './DisplayMetrics.actual';
import { MonthCVD } from './MonthCVD.shared';
import { Paint } from './draw/Paint.actual';
import { MutableObservableProperty } from '../observables/MutableObservableProperty.shared';
import { DateAlone } from '../time/DateAlone.actual';
import { RectF } from './geometry/RectF.actual';
export declare class SelectDateRangeMonthCVD extends MonthCVD {
    constructor();
    generateAccessibilityView(): (HTMLElement | null);
    draggingStart: boolean;
    start: MutableObservableProperty<(DateAlone | null)>;
    endInclusive: MutableObservableProperty<(DateAlone | null)>;
    readonly selectedDayPaint: Paint;
    readonly selectedPaint: Paint;
    measure(width: number, height: number, displayMetrics: DisplayMetrics): void;
    readonly drawDay_dateAlone: DateAlone;
    drawDay(canvas: CanvasRenderingContext2D, showingMonth: DateAlone, day: DateAlone, displayMetrics: DisplayMetrics, outer: RectF, inner: RectF): void;
    private startedDraggingOn;
    onTap(day: DateAlone): void;
    onTouchDownDate(day: DateAlone): boolean;
    onTouchMoveDate(day: DateAlone): boolean;
    onTouchUpDate(day: DateAlone): boolean;
}
