export declare class TimeInterval {
    readonly milliseconds: number;
    constructor(milliseconds: number);
    get seconds(): number;
}
export declare function xIntMilliseconds(this_: number): TimeInterval;
export declare function xIntSeconds(this_: number): TimeInterval;
export declare function xIntMinutes(this_: number): TimeInterval;
export declare function xIntHours(this_: number): TimeInterval;
export declare function xIntDays(this_: number): TimeInterval;
