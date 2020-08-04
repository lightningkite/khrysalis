export declare const detatchEventSymbol: unique symbol;
declare global {
    interface HTMLElement {
        [detatchEventSymbol]: Array<(view: HTMLElement) => void> | undefined;
    }
}
export declare function addDetatchEvent(view: HTMLElement, action: (view: HTMLElement) => void): void;
export declare function triggerDetatchEvent(view: HTMLElement): void;
