export declare class HttpPhase {
    private constructor();
    static Connect: HttpPhase;
    static Write: HttpPhase;
    static Waiting: HttpPhase;
    static Read: HttpPhase;
    static Done: HttpPhase;
    private static _values;
    static values(): Array<HttpPhase>;
    readonly name: string;
    readonly jsonName: string;
    static valueOf(name: string): HttpPhase;
    toString(): string;
    toJSON(): string;
}
export declare class HttpProgress {
    readonly phase: HttpPhase;
    readonly ratio: number;
    constructor(phase: HttpPhase, ratio: number);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(phase?: HttpPhase, ratio?: number): HttpProgress;
    get approximate(): number;
}
export declare namespace HttpProgress {
    class Companion {
        private constructor();
        static INSTANCE: Companion;
        readonly connecting: HttpProgress;
        readonly waiting: HttpProgress;
        readonly done: HttpProgress;
    }
}
export declare class HttpOptions {
    readonly callTimeout: (number | null);
    readonly writeTimeout: (number | null);
    readonly readTimeout: (number | null);
    readonly connectTimeout: (number | null);
    readonly cacheMode: HttpCacheMode;
    constructor(callTimeout?: (number | null), writeTimeout?: (number | null), readTimeout?: (number | null), connectTimeout?: (number | null), cacheMode?: HttpCacheMode);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(callTimeout?: (number | null), writeTimeout?: (number | null), readTimeout?: (number | null), connectTimeout?: (number | null), cacheMode?: HttpCacheMode): HttpOptions;
}
export declare class HttpCacheMode {
    private constructor();
    static Default: HttpCacheMode;
    static NoStore: HttpCacheMode;
    static Reload: HttpCacheMode;
    static NoCache: HttpCacheMode;
    static ForceCache: HttpCacheMode;
    static OnlyIfCached: HttpCacheMode;
    private static _values;
    static values(): Array<HttpCacheMode>;
    readonly name: string;
    readonly jsonName: string;
    static valueOf(name: string): HttpCacheMode;
    toString(): string;
    toJSON(): string;
}
