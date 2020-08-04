import { DisplayMetrics } from './DisplayMetrics.actual';
import { MonthCVD } from './MonthCVD.shared';
import { Paint } from './draw/Paint.actual';
import { MutableObservableProperty } from '../observables/MutableObservableProperty.shared';
import { DateAlone } from '../time/DateAlone.actual';
import { RectF } from './geometry/RectF.actual';
export declare class SelectDateMonthCVD extends MonthCVD {
    constructor();
    generateAccessibilityView(): (HTMLElement | null);
    selected: MutableObservableProperty<(DateAlone | null)>;
    readonly selectedDayPaint: Paint;
    readonly selectedPaint: Paint;
    drawDay(canvas: CanvasRenderingContext2D, showingMonth: DateAlone, day: DateAlone, displayMetrics: DisplayMetrics, outer: RectF, inner: RectF): void;
    measure(width: number, height: number, displayMetrics: DisplayMetrics): void;
    onTap(day: DateAlone): void;
}
