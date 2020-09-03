import { TimeInterval } from './TimeInterval.actual';
export declare class TimeAlone {
    hour: number;
    minute: number;
    second: number;
    constructor(hour: number, minute: number, second: number);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(hour?: number, minute?: number, second?: number): TimeAlone;
    toJSON(): any;
    static Companion: {
        new (): {
            now(): TimeAlone;
            iso(string: string): TimeAlone | null;
            readonly min: TimeAlone;
            readonly midnight: TimeAlone;
            readonly noon: TimeAlone;
            readonly max: TimeAlone;
        };
        INSTANCE: {
            now(): TimeAlone;
            iso(string: string): TimeAlone | null;
            readonly min: TimeAlone;
            readonly midnight: TimeAlone;
            readonly noon: TimeAlone;
            readonly max: TimeAlone;
        };
    };
    get comparable(): number;
    get secondsInDay(): number;
    set secondsInDay(value: number);
    get hoursInDay(): number;
    set hoursInDay(value: number);
}
export declare function xTimeAloneIso8601(this_: TimeAlone): string;
export declare function xTimeAloneMinus(this_: TimeAlone, rhs: TimeAlone | TimeInterval): TimeAlone;
export declare function xTimeAlonePlus(this_: TimeAlone, rhs: TimeAlone | TimeInterval): TimeAlone;
