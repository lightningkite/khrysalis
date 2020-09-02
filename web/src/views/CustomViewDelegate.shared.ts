// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/CustomViewDelegate.shared.kt
// Package: com.lightningkite.khrysalis.views
import { DisplayMetrics } from './DisplayMetrics.actual'
import { DisposeCondition } from '../rx/DisposeCondition.shared'
import { customViewInvalidate } from './CustomView.actual'
import { SubscriptionLike } from 'rxjs'

//! Declares com.lightningkite.khrysalis.views.CustomViewDelegate
export abstract class CustomViewDelegate {
    protected constructor() {
        this.customView = null;
        this.toDispose = [];
        this._removed = null;
        this._removed = new DisposeCondition((it: SubscriptionLike): void => {
                this.toDispose.push(it);
        });
    }
    
    public customView: (HTMLCanvasElement | null);
    
    public abstract generateAccessibilityView(): (HTMLElement | null) 
    public abstract draw(canvas: CanvasRenderingContext2D, width: number, height: number, displayMetrics: DisplayMetrics): void 
    public onTouchDown(id: number, x: number, y: number, width: number, height: number): boolean { 
        return false; 
    }
    public onTouchMove(id: number, x: number, y: number, width: number, height: number): boolean { 
        return false; 
    }
    public onTouchCancelled(id: number, x: number, y: number, width: number, height: number): boolean { 
        return false; 
    }
    public onTouchUp(id: number, x: number, y: number, width: number, height: number): boolean { 
        return false; 
    }
    public onWheel(delta: number): boolean { 
        return false; 
    }
    public sizeThatFitsWidth(width: number, height: number): number { 
        return width; 
    }
    public sizeThatFitsHeight(width: number, height: number): number { 
        return height; 
    }
    
    public invalidate(): void { const temp167 = this.customView;
        if(temp167 !== null) { 
            customViewInvalidate(temp167)
    }; }
    public postInvalidate(): void { const temp168 = this.customView;
        if(temp168 !== null) { 
            customViewInvalidate(temp168)
    }; }
    
    public readonly toDispose: Array<SubscriptionLike>;
    
    private _removed: (DisposeCondition | null);
    
    //! Declares com.lightningkite.khrysalis.views.CustomViewDelegate.removed
    public get removed(): DisposeCondition { return this._removed!!; }
    
    
    public dispose(): void {
        for (const item of this.toDispose) {
            item.unsubscribe();
        }
    }
}

