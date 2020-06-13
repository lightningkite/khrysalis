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
    static Companion: {
        new (): {
            now(): DateAlone;
            readonly farPast: DateAlone;
            readonly farFuture: DateAlone;
            iso(string: string): DateAlone;
            fromMonthInEra(monthInEra: number): DateAlone;
        };
        INSTANCE: {
            now(): DateAlone;
            readonly farPast: DateAlone;
            readonly farFuture: DateAlone;
            iso(string: string): DateAlone;
            fromMonthInEra(monthInEra: number): DateAlone;
        };
    };
    get monthInEra(): number;
    get comparable(): number;
    get dayOfWeek(): number;
}
export declare function comLightningkiteKhrysalisTimeDateAloneSetDayOfWeek(this_: DateAlone, value: number): DateAlone;
export declare function comLightningkiteKhrysalisTimeDateAloneSetAddDayOfWeek(this_: DateAlone, value: number): DateAlone;
export declare function comLightningkiteKhrysalisTimeDateAloneDayOfWeek(this_: DateAlone, value: number): DateAlone;
export declare function comLightningkiteKhrysalisTimeDateAloneAddDayOfWeek(this_: DateAlone, value: number): DateAlone;
export declare function comLightningkiteKhrysalisTimeDateAloneIso8601(this_: DateAlone): string;
export declare function comLightningkiteKhrysalisTimeDateAloneFormatYearless(this_: DateAlone, clockPartSize: ClockPartSize): string;
