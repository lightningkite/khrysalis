export declare class TimeInterval {
    readonly milliseconds: number;
    constructor(milliseconds: number);
    get seconds(): number;
}
export declare function kotlinIntMilliseconds(this_: number): TimeInterval;
export declare function kotlinIntSeconds(this_: number): TimeInterval;
export declare function kotlinIntMinutes(this_: number): TimeInterval;
export declare function kotlinIntHours(this_: number): TimeInterval;
export declare function kotlinIntDays(this_: number): TimeInterval;
