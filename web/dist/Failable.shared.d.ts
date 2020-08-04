export declare class Failable<T> {
    readonly result: (T | null);
    readonly issue: (string | null);
    constructor(result?: (T | null), issue?: (string | null));
}
export declare namespace Failable {
    class Companion {
        private constructor();
        static INSTANCE: Companion;
        failure<T>(message: string): Failable<T>;
        success<T>(value: T): Failable<T>;
    }
}
