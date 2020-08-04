import { CanvasGradientMaker } from "./LinearGradient.actual";
export declare class Paint {
    color: string;
    strokeWidth: number;
    alpha: number;
    style: Paint.Style;
    textSize: number;
    shader: CanvasGradientMaker | null;
    isAntiAlias: boolean;
    isFakeBoldText: boolean;
    apply(context: CanvasRenderingContext2D): void;
    complete(context: CanvasRenderingContext2D): void;
    render(context: CanvasRenderingContext2D, path: Path2D): void;
    text(context: CanvasRenderingContext2D, text: string, x: number, y: number, maxWidth?: number): void;
}
export declare namespace Paint {
    enum Style {
        FILL = 0,
        STROKE = 1,
        FILL_AND_STROKE = 2
    }
}
export declare function getAndroidGraphicsPaintTextHeight(this_: Paint): number;
