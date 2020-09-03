export interface Closeable {
    isDisposed(): boolean;
    dispose(): void;
    close(): void;
}
export declare namespace CloseableDefaults {
    function isDisposed(this_: Closeable): boolean;
    function dispose(this_: Closeable): void;
}
export declare class Close implements Closeable {
    static implementsInterfaceComLightningkiteKhrysalisObservablesCloseable: boolean;
    static implementsInterfaceIoReactivexDisposable: boolean;
    readonly closer: (() => void);
    constructor(closer: (() => void));
    disposed: boolean;
    isDisposed(): boolean;
    close(): void;
    dispose(): void;
}
