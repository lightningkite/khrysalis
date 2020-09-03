import { ClockPartSize } from './ClockPartSize.shared';
export declare class DateAlone {
    year: number;
    month: number;
    day: number;
    constructor(year: number, month: number, day: number);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(year?: number, month?: number, day?: number): DateAlone;
    toJSON(): any;
    static Companion: {
        new (): {
            now(): DateAlone;
            readonly farPast: DateAlone;
            readonly farFuture: DateAlone;
            iso(string: string): DateAlone | null;
            fromMonthInEra(monthInEra: number): DateAlone;
        };
        INSTANCE: {
            now(): DateAlone;
            readonly farPast: DateAlone;
            readonly farFuture: DateAlone;
            iso(string: string): DateAlone | null;
            fromMonthInEra(monthInEra: number): DateAlone;
        };
    };
    get monthInEra(): number;
    get comparable(): number;
    get dayOfWeek(): number;
}
export declare function xDateAloneSetDayOfWeek(this_: DateAlone, value: number): DateAlone;
export declare function xDateAloneSetAddDayOfWeek(this_: DateAlone, value: number): DateAlone;
export declare function xDateAloneDayOfWeek(this_: DateAlone, value: number): DateAlone;
export declare function xDateAloneAddDayOfWeek(this_: DateAlone, value: number): DateAlone;
export declare function xDateAloneIso8601(this_: DateAlone): string;
export declare function xDateAloneFormatYearless(this_: DateAlone, clockPartSize: ClockPartSize): string;
