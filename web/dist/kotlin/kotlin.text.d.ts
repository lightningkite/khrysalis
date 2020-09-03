export declare function xCharSequenceIsBlank(s: string | null): boolean;
export declare function xStringSubstringBefore(s: string, delimeter: string, defaultResult?: string): string;
export declare function xStringSubstringAfter(s: string, delimeter: string, defaultResult?: string): string;
export declare function xStringSubstringBeforeLast(s: string, delimeter: string, defaultResult?: string): string;
export declare function xStringSubstringAfterLast(s: string, delimeter: string, defaultResult?: string): string;
export declare function xStringTrimIndent(c: string): string;
export declare function xCharIsWhitespace(c: string): boolean;
export declare function xCharIsUpperCase(c: string): boolean;
export declare function xCharIsLowerCase(c: string): boolean;
export declare function xCharIsLetter(c: string): boolean;
export declare function xCharIsDigit(c: string): boolean;
export declare function xCharIsLetterOrDigit(c: string): boolean;
export declare class StringBuilder {
    value: string;
    toString(): string;
}
export declare function xCharSequenceIndexOfAny(c: string, set: Array<string>, start: number, caseSensitive: boolean): number;
