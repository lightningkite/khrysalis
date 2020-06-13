export interface Closeable {
    isDisposed(): boolean;
    dispose(): void;
    close(): void;
}
export declare class CloseableDefaults {
    static isDisposed(this_: Closeable): boolean;
    static dispose(this_: Closeable): void;
}
export declare class Close implements Closeable {
    static implementsInterfaceComLightningkiteKhrysalisObservablesCloseable: boolean;
    static implementsInterfaceIoReactivexDisposablesDisposable: boolean;
    readonly closer: () => void;
    constructor(closer: () => void);
    disposed: boolean;
    isDisposed(): boolean;
    close(): void;
    dispose(): void;
}
