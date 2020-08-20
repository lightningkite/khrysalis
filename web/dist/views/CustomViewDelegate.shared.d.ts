import { DisplayMetrics } from './DisplayMetrics.actual';
import { DisposeCondition } from '../rx/DisposeCondition.shared';
import { SubscriptionLike } from 'rxjs';
export declare abstract class CustomViewDelegate {
    protected constructor();
    customView: (HTMLCanvasElement | null);
    abstract generateAccessibilityView(): (HTMLElement | null);
    abstract draw(canvas: CanvasRenderingContext2D, width: number, height: number, displayMetrics: DisplayMetrics): void;
    onTouchDown(id: number, x: number, y: number, width: number, height: number): boolean;
    onTouchMove(id: number, x: number, y: number, width: number, height: number): boolean;
    onTouchCancelled(id: number, x: number, y: number, width: number, height: number): boolean;
    onTouchUp(id: number, x: number, y: number, width: number, height: number): boolean;
    onWheel(delta: number): boolean;
    sizeThatFitsWidth(width: number, height: number): number;
    sizeThatFitsHeight(width: number, height: number): number;
    invalidate(): void;
    postInvalidate(): void;
    readonly toDispose: Array<SubscriptionLike>;
    private _removed;
    get removed(): DisposeCondition;
    dispose(): void;
}
