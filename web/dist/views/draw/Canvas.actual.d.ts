import { Paint } from './Paint.actual';
import { AlignPair } from '../geometry/Align.shared';
export declare function androidGraphicsCanvasDrawTextCentered(this_: CanvasRenderingContext2D, text: string, centerX: number, centerY: number, paint: Paint): void;
export declare function androidGraphicsCanvasDrawText(this_: CanvasRenderingContext2D, text: string, x: number, y: number, gravity: AlignPair, paint: Paint): void;
export declare function androidGraphicsCanvasDrawBitmap(this_: CanvasRenderingContext2D, bitmap: ImageBitmap, left: number, top: number, right: number, bottom: number): void;
