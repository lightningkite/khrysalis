export declare class TimeZone {
    readonly id: string;
    readonly displayName: string;
    readonly getOffset: (date: number) => number;
    constructor(id: string, displayName: string, getOffset: (date: number) => number);
    static Companion: {
        new (): {
            getDefault(): TimeZone;
        };
        INSTANCE: {
            getDefault(): TimeZone;
        };
    };
}
