export declare class ClockPartSize {
    private constructor();
    static None: ClockPartSize;
    static Short: ClockPartSize;
    static Medium: ClockPartSize;
    static Long: ClockPartSize;
    static Full: ClockPartSize;
    private static _values;
    static values(): Array<ClockPartSize>;
    readonly name: string;
    readonly jsonName: string;
    static valueOf(name: string): ClockPartSize;
    toString(): string;
    toJSON(): string;
}
