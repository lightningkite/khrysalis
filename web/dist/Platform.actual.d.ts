export declare class Platform {
    constructor(name: string);
    static iOS: Platform;
    static Android: Platform;
    static Web: Platform;
    static Companion: {
        new (): {
            readonly current: Platform;
        };
        INSTANCE: {
            readonly current: Platform;
        };
    };
    private static _values;
    static values(): Array<Platform>;
    readonly name: string;
    static valueOf(name: string): Platform;
    toString(): string;
}
