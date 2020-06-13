export declare function kotlinCharSequenceIsBlank(s: string | null): boolean;
export declare function kotlinStringSubstringBefore(s: string, delimeter: string, defaultResult: string): string;
export declare function kotlinStringSubstringAfter(s: string, delimeter: string, defaultResult: string): string;
export declare function kotlinStringSubstringBeforeLast(s: string, delimeter: string, defaultResult: string): string;
export declare function kotlinStringSubstringAfterLast(s: string, delimeter: string, defaultResult: string): string;
export declare function kotlinCharIsWhitespace(c: string): boolean;
export declare function kotlinCharIsUpperCase(c: string): boolean;
export declare function kotlinCharIsLowerCase(c: string): boolean;
export declare function kotlinCharIsLetter(c: string): boolean;
export declare function kotlinCharIsDigit(c: string): boolean;
export declare function kotlinCharIsLetterOrDigit(c: string): boolean;
export declare class StringBuilder {
    value: string;
    toString(): string;
}
