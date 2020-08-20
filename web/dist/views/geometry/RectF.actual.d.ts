export declare class RectF {
    right: number;
    bottom: number;
    top: number;
    left: number;
    constructor(left?: number | RectF, top?: number, right?: number, bottom?: number);
    set(left: number | RectF, top?: number, right?: number, bottom?: number): void;
    centerX(): number;
    centerY(): number;
    width(): number;
    height(): number;
    inset(dx: number, dy: number): RectF;
}
