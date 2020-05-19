// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/geometry/RectF.actual.kt
// Package: com.lightningkite.khrysalis.views.geometry


/* SHARED DECLARATIONS
class RectF {
    var right: Float
    var bottom: Float
    var top: Float
    var left: Float
    fun set(left: Float, top: Float, right: Float, bottom: Float)
    fun set(rect: RectF)
    fun centerX(): Float
    fun centerY(): Float
    fun width(): Float
    fun height(): Float
    fun inset(dx: Float, dy: Float)
}
 */

class RectF {
    right: number = 0.0;
    bottom: number = 0.0;
    top: number = 0.0;
    left: number = 0.0;
    set(left: number | RectF, top?: number, right?: number, bottom?: number) {
        if(left instanceof RectF){
            this.right = left.right;
            this.bottom = left.bottom;
            this.top = left.top;
            this.left = left.left;
        } else {
            this.right = right;
            this.bottom = bottom;
            this.top = top;
            this.left = left;
        }
    }
    centerX(): number { return (this.left + this.right) / 2 }
    centerY(): number { return (this.top + this.bottom) / 2 }
    width(): number { return this.right - this.left }
    height(): number { return this.bottom - this.top }
    inset(dx: number, dy: number): RectF {
        const r = new RectF();
        r.left = this.left + dx;
        r.right = this.right - dx;
        r.top = this.top + dy;
        r.bottom = this.bottom - dy;
        return r
    }
}