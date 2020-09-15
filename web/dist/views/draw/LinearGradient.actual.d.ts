export declare function newLinearGradient(x0: number, y0: number, x1: number, y1: number, colors: Array<string>, positions: Array<number>, tile: Shader.TileMode): CanvasGradientMaker;
export declare namespace Shader {
    enum TileMode {
        REPEAT = 0,
        CLAMP = 1
    }
}
export declare type CanvasGradientMaker = (context: CanvasRenderingContext2D) => CanvasGradient;
