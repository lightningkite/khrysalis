export declare class Exception extends Error {
    cause: any;
    constructor(message: string, cause: any);
    printStackTrace(): void;
}
export declare class IllegalArgumentException extends Exception {
}
export declare class IllegalStateException extends Exception {
}
export declare class NoSuchElementException extends Exception {
}
export declare function hashString(item: string): number;
export declare function hashAnything(item: any): number;
export declare function safeEq(left: any, right: any): boolean;
export declare function checkReified<T>(item: any, fullType: Array<any>): item is T;
export declare function checkIsInterface<T>(item: any, key: string): item is T;
export declare function tryCastInterface<T>(item: any, key: string): T | null;
export declare function tryCastPrimitive<T>(item: any, key: string): T | null;
export declare function tryCastClass<T>(item: any, erasedType: any): T | null;
export declare function also<T>(item: T, action: (a: T) => void): T;
export declare function takeIf<T>(item: T, action: (a: T) => boolean): T | null;
export declare function takeUnless<T>(item: T, action: (a: T) => boolean): T | null;
export declare function parseIntOrNull(s: string): number | null;
export declare function parseFloatOrNull(s: string): number | null;
declare global {
    export interface Object {
        hashCode(): number;
        equals(other: any): boolean;
    }
}
export interface Comparable<T> {
    compareTo(other: T): number;
}
declare global {
    interface Number extends Comparable<Number> {
    }
    interface String extends Comparable<String> {
    }
    interface Boolean extends Comparable<Boolean> {
    }
}
export declare class Range<T> {
    start: T;
    endInclusive: T;
    constructor(start: T, endInclusive: T);
    contains(element: T): boolean;
}
export declare class NumberRange extends Range<number> implements Iterable<number> {
    constructor(start: number, endInclusive: number);
    [Symbol.iterator](): Iterator<number>;
}
export declare class CharRange extends Range<string> implements Iterable<string> {
    constructor(start: string, endInclusive: string);
    [Symbol.iterator](): Iterator<string>;
}
import { Observable } from "rxjs";
export declare function doOnSubscribe<T>(observable: Observable<T>, action: (x: any) => void): Observable<T>;
