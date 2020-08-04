export declare class TimeNames {
    private constructor();
    static INSTANCE: TimeNames;
    readonly shortMonthNames: Array<string>;
    readonly monthNames: Array<string>;
    readonly shortWeekdayNames: Array<string>;
    readonly weekdayNames: Array<string>;
    shortMonthName(oneIndexedPosition: number): string;
    monthName(oneIndexedPosition: number): string;
    shortWeekdayName(oneIndexedPosition: number): string;
    weekdayName(oneIndexedPosition: number): string;
}
