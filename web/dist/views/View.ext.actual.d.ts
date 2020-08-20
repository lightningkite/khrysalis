export declare function androidViewViewOnClick(this_: HTMLElement, disabledMilliseconds: number | undefined, action: () => void): void;
export declare function androidViewViewOnLongClick(this_: HTMLElement, action: () => void): void;
export declare function getViewVisibility(this_: HTMLElement): string;
export declare function setViewVisibility(this_: HTMLElement, value: string): void;
export declare function findView(view: HTMLElement, predicate: (e: HTMLElement) => boolean): HTMLElement | null;
declare const backgroundClassSymbol: unique symbol;
declare global {
    interface HTMLElement {
        [backgroundClassSymbol]: string | undefined;
    }
}
export declare function setViewBackgroundClass(view: HTMLElement, cssClass: string): void;
export {};
