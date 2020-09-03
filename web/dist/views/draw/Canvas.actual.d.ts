import { Paint } from './Paint.actual';
import { AlignPair } from '../geometry/Align.shared';
export declare function xCanvasDrawTextCentered(this_: CanvasRenderingContext2D, text: string, centerX: number, centerY: number, paint: Paint): void;
export declare function xCanvasDrawText(this_: CanvasRenderingContext2D, text: string, x: number, y: number, gravity: AlignPair, paint: Paint): void;
export declare function xCanvasDrawBitmap(this_: CanvasRenderingContext2D, bitmap: ImageBitmap, left: number, top: number, right: number, bottom: number): void;
export declare function applyMatrixToCanvas(canvas: CanvasRenderingContext2D, matrix: DOMMatrix): void;
