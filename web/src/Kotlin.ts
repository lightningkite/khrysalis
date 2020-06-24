// export type FullType = Array<any>;

//! Declares kotlin.Exception
//! Declares java.lang.Exception
export class Exception extends Error {
    cause: any;
    constructor(message: string, cause: any) {
        super(message);
        this.cause = cause;
    }
    printStackTrace(){
        console.trace(this);
    }
}
//! Declares kotlin.IllegalArgumentException
//! Declares java.lang.IllegalArgumentException
export class IllegalArgumentException extends Exception {}
//! Declares kotlin.IllegalStateException
//! Declares java.lang.IllegalStateException
export class IllegalStateException extends Exception {}
//! Declares kotlin.NoSuchElementException
//! Declares java.lang.NoSuchElementException
export class NoSuchElementException extends Exception {}

export function hashString(item: string): number {
    let hash = 0, i, chr;
    for (i = 0; i < this.length; i++) {
        chr = this.charCodeAt(i);
        hash = ((hash << 5) - hash) + chr;
        hash |= 0; // Convert to 32bit integer
    }
    return hash;
}

export function hashAnything(item: any): number {
    if(item === null || item === undefined) return 0;
    switch(typeof item){
        case "object":
            return item.hashCode();
        case "number":
            return Math.floor(item);
        case "string":
            return hashString(item);
        case "boolean":
            return item ? 1 : 0;
        default:
            return 0;
    }
}

export function safeEq(left: any, right: any): boolean {
    if(typeof left === "object") {
        return left.equals(right)
    } else {
        return left === right
    }
}

export function checkReified<T>(item: any, fullType: Array<any>): item is T {
    const type = fullType[0];
    switch(type){
        case String:
            return typeof item === "string";
        case Number:
            return typeof item === "number";
        case Boolean:
            return typeof item === "boolean";
        case undefined:
            return typeof item === "undefined";
        case null:
            return !item;
        default:
            return item instanceof type;
    }
}

export function checkIsInterface<T>(item: any, key: string): item is T {
    return (item.constructor as any)["implementsInterface" + key]
}

export function tryCastInterface<T>(item: any, key: string): T | null {
    if ((item.constructor as any)["implementsInterface" + key]) {
        return item as T;
    } else {
        return null;
    }
}

export function tryCastPrimitive<T>(item: any, key: string): T | null {
    if (typeof item === key) {
        return item as T;
    } else {
        return null;
    }
}

export function tryCastClass<T>(item: any, erasedType: any): T | null {
    if (item instanceof erasedType) {
        return item as T;
    } else {
        return null;
    }
}

export function also<T>(item: T, action: (a: T) => void): T {
    action(item);
    return item;
}

export function takeIf<T>(item: T, action: (a: T) => boolean): T | null {
    if(action(item)) return item;
    else return null;
}

export function takeUnless<T>(item: T, action: (a: T) => boolean): T | null {
    if(!action(item)) return item;
    else return null;
}

export function parseIntOrNull(s: string): number | null {
    const r = parseInt(s);
    if(isNaN(r)) return null;
    return r;
}
export function parseFloatOrNull(s: string): number | null {
    const r = parseFloat(s);
    if(isNaN(r)) return null;
    return r;
}

declare global {
    export interface Object {
        hashCode(): number

        equals(other: any): boolean
    }
}

Object.defineProperty(Object.prototype, "hashCode", {
    value: function (): number {
        const existing = (this as any)["__randomizedHash"];
        if(existing) return existing;
        const r = Math.random() * 1000000;
        (this as any)["__randomizedHash"] = r;
        return r
    }
})
Object.defineProperty(Object.prototype, "equals", {
    value: function (other: any): boolean {
        return this == other
    }
})

export interface Comparable<T> {
    compareTo(other: T): number
}

declare global {
    interface Number extends Comparable<Number> {
    }

    interface String extends Comparable<String> {
    }
}
Object.defineProperty(Number.prototype, "compareTo", {
    value: function (other: number) {
        return (this > other ? 1 : this < other ? -1 : 0)
    }
})
Object.defineProperty(Number.prototype, "implementsInterfaceKotlinComparable", {value: true})
Object.defineProperty(String.prototype, "compareTo", {
    value: function (other: string) {
        return (this > other ? 1 : this < other ? -1 : 0)
    }
})
Object.defineProperty(String.prototype, "implementsInterfaceKotlinComparable", {value: true})

export class Range<T> {
    start: T;
    endInclusive: T;

    constructor(start: T, endInclusive: T) {
        this.start = start;
        this.endInclusive = endInclusive;
    }

    contains(element: T): boolean {
        return element >= this.start && element <= this.endInclusive
    }
}

export class NumberRange extends Range<number> implements Iterable<number> {
    constructor(start: number, endInclusive: number) {
        super(start, endInclusive);
    }

    [Symbol.iterator](): Iterator<number> {
        return new NumberRangeIterator(this)
    }
}

class NumberRangeIterator implements Iterator<number> {
    start: number;
    endInclusive: number;

    constructor(range: NumberRange) {
        this.start = range.start;
        this.endInclusive = range.endInclusive;
    }

    next(): IteratorResult<number> {
        if(this.endInclusive < this.start) return { done: true, value: undefined }
        const result = {done: this.start > this.endInclusive, value: this.start};
        this.start++;
        return result
    }
}

export class CharRange extends Range<string> implements Iterable<string> {
    constructor(start: string, endInclusive: string) {
        super(start, endInclusive);
    }

    [Symbol.iterator](): Iterator<string> {
        return new CharRangeIterator(this)
    }
}

class CharRangeIterator implements Iterator<string> {
    startCode: number;
    endInclusiveCode: number;

    constructor(range: CharRange) {
        this.startCode = range.start.charCodeAt(0);
        this.endInclusiveCode = range.endInclusive.charCodeAt(0);
    }

    next(): IteratorResult<string> {
        const result = {done: this.startCode >= this.endInclusiveCode, value: String.fromCharCode(this.startCode)};
        this.startCode++;
        return result
    }
}

// interface Collection<E> extends Iterable<E> {
//     readonly size: number; //Why?  Because that's what Array uses, and we conform to Array.
//     contains(e: E): boolean
//     containsAll(e: E): boolean
// }
// interface MutableCollection<E> extends Collection<E> {
//     add(element: E): boolean
//     remove(element: E): boolean
//     addAll(elements: Collection<E>): boolean
//     removeAll(elements: Collection<E>): boolean
//     retainAll(elements: Collection<E>): boolean
//     clear(): boolean
// }

import {Observable} from "rxjs";
import {defer as rxDefer} from "rxjs";

export function doOnSubscribe<T>(observable: Observable<T>, action: (x: any) => void) {
    return rxDefer(() => {
        action(null);
        return observable;
    })
}
