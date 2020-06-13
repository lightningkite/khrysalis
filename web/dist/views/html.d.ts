export declare function loadHtmlFromString(data: string): HTMLElement;
export declare function findViewById<T extends HTMLElement>(view: HTMLElement, id: string): T | null;
export declare function getViewById<T extends HTMLElement>(view: HTMLElement, id: string): T;
export declare function replaceViewWithId(base: HTMLElement, withElement: () => HTMLElement, id: string): void;
