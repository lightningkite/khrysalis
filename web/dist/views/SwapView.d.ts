declare const previousViewSymbol: unique symbol;
declare global {
    interface HTMLDivElement {
        [previousViewSymbol]: HTMLElement | undefined;
    }
}
export declare function swapViewSwap(view: HTMLDivElement, to: HTMLElement | null, animation: string): void;
export {};
