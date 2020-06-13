import { DisplayMetrics } from './DisplayMetrics.actual';
export declare abstract class CustomViewDelegate {
    customView: (HTMLCanvasElement | null);
    abstract generateAccessibilityView(): (HTMLElement | null);
    abstract draw(canvas: CanvasRenderingContext2D, width: number, height: number, displayMetrics: DisplayMetrics): void;
    onTouchDown(id: number, x: number, y: number, width: number, height: number): boolean;
    onTouchMove(id: number, x: number, y: number, width: number, height: number): boolean;
    onTouchCancelled(id: number, x: number, y: number, width: number, height: number): boolean;
    onTouchUp(id: number, x: number, y: number, width: number, height: number): boolean;
    sizeThatFitsWidth(width: number, height: number): number;
    sizeThatFitsHeight(width: number, height: number): number;
    invalidate(): void;
    postInvalidate(): void;
}
