
export function hashString(item: string): number {
    let hash = 0, i, chr;
    for (i = 0; i < this.length; i++) {
        chr   = this.charCodeAt(i);
        hash  = ((hash << 5) - hash) + chr;
        hash |= 0; // Convert to 32bit integer
    }
    return hash;
}

export function checkIsInterface<T>(item: any, key: string): T | null {
    return (item.constructor as any)["implementsInterface" + key]
}

export function tryCastInterface<T>(item: any, key: string): T | null {
    if((item.constructor as any)["implementsInterface" + key]) {
        return item as T;
    } else {
        return null;
    }
}
export function tryCastPrimitive<T>(item: any, key: string): T | null {
    if(typeof item === key) {
        return item as T;
    } else {
        return null;
    }
}
export function tryCastClass<T>(item: any, erasedType: any): T | null {
    if(item instanceof erasedType) {
        return item as T;
    } else {
        return null;
    }
}

export function also<T>(item: T, action: (a: T)=>void): T {
    action(item);
    return item;
}

export interface Object {
    hashCode(): number
    equals(other: any): boolean
}
Object.defineProperty(Object.prototype, "hashCode", { value: function(): number { return hashString(this.toString()) } })
Object.defineProperty(Object.prototype, "equals", { value: function(other: any): boolean { return this == other } })

interface Comparable<T> {
    compareTo(other: T): number
}
interface Number extends Comparable<Number> {}
Object.defineProperty(Number.prototype, "compareTo", { value: function(other: number) { return (this > other ? 1 : this < other ? -1 : 0) } } )
Object.defineProperty(Number.prototype, "implementsInterfaceKotlinComparable", { value: true })
interface String extends Comparable<String> {}
Object.defineProperty(String.prototype, "compareTo", { value: function(other: string) { return (this > other ? 1 : this < other ? -1 : 0) } } )
Object.defineProperty(String.prototype, "implementsInterfaceKotlinComparable", { value: true })

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
        const result = { done: this.start >= this.endInclusive, value: this.start };
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
        const result = { done: this.startCode >= this.endInclusiveCode, value: String.fromCharCode(this.startCode) };
        this.startCode++;
        return result
    }
}

import {Observable} from "rxjs";
import { defer as rxDefer } from "rxjs";
export function doOnSubscribe<T>(observable: Observable<T>, action: (x: any)=>void){
    return rxDefer(()=>{
        action(null);
        return observable;
    })
}