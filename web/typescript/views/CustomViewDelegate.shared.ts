// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/CustomViewDelegate.shared.kt
// Package: com.lightningkite.khrysalis.views
// FQImport: android.util.DisplayMetrics TS DisplayMetrics
// FQImport: kotlin.Boolean TS Boolean
// FQImport: com.lightningkite.khrysalis.views.CustomViewDelegate.customView TS customView
// FQImport: com.lightningkite.khrysalis.views.CustomView TS CustomView
// FQImport: android.graphics.Canvas TS Canvas
// FQImport: com.lightningkite.khrysalis.views.CustomViewDelegate.sizeThatFitsHeight.height TS height
// FQImport: com.lightningkite.khrysalis.views.CustomViewDelegate.sizeThatFitsWidth.width TS width
// FQImport: com.lightningkite.khrysalis.views.CustomView.postInvalidate TS postInvalidate
import { CustomView } from './CustomView.actual'

//! Declares com.lightningkite.khrysalis.views.CustomViewDelegate
export abstract class CustomViewDelegate {
    
    public customView: (CustomView | null);
    
    public abstract generateAccessibilityView(): (HTMLElement | null)
    public abstract draw(canvas: Canvas, width: number, height: number, displayMetrics: DisplayMetrics): void
    public onTouchDown(id: number, x: number, y: number, width: number, height: number): Boolean{ return false; }
    public onTouchMove(id: number, x: number, y: number, width: number, height: number): Boolean{ return false; }
    public onTouchUp(id: number, x: number, y: number, width: number, height: number): Boolean{ return false; }
    public sizeThatFitsWidth(width: number, height: number): number{ return width; }
    public sizeThatFitsHeight(width: number, height: number): number{ return height; }
    
    public invalidate(): void{ const temp137 = this.customView;
    if(temp137 !== null) /* Invalidate.  Not needed in JS. */; }
    public postInvalidate(): void{ this.customView?.postInvalidate(); }
}

