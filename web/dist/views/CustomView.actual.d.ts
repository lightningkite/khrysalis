import { CustomViewDelegate } from "./CustomViewDelegate.shared";
declare const customViewDelegateSymbol: unique symbol;
declare const customViewConfiguredSymbol: unique symbol;
declare global {
    interface HTMLCanvasElement {
        [customViewDelegateSymbol]: CustomViewDelegate | undefined;
        [customViewConfiguredSymbol]: boolean | undefined;
    }
}
export declare function customViewSetDelegate(view: HTMLCanvasElement, delegate: CustomViewDelegate): void;
export declare function customViewInvalidate(view: HTMLCanvasElement): void;
export {};
