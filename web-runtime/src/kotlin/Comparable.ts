//! Declares kotlin.Comparator
export type Comparator<T> = (lhs: T, rhs: T)=>number

export function safeCompare(left: any, right: any): number {
    if(left === null) {
        if(right === null) { return 0 }
        return -1
    }
    if(right === null) { return 1 }
    if(typeof left === "object" && !(left instanceof Date)) {
        return (left as Comparable<any>).compareTo(right)
    } else {
        if(left < right)
            return -1
        else if(left == right)
            return 0
        else
            return 1
    }
}

export function cMin<T>(a: T, b: T): T {
    if(safeCompare(a, b) < 0) {
        return a;
    } else {
        return b;
    }
}
export function cMax<T>(a: T, b: T): T {
    if(safeCompare(a, b) > 0) {
        return a;
    } else {
        return b;
    }
}
export function cCoerce<T>(value: T, low: T, high: T): T {
    return cMin(high, cMax(value, low));
}

export interface Comparable<T> {
    compareTo(other: T): number
}

declare global {
    interface Number extends Comparable<Number> {
    }

    interface String extends Comparable<String> {
    }

    interface Boolean extends Comparable<Boolean> {
    }
}
Object.defineProperty(Number.prototype, "implementsInterfaceKotlinComparable", {value: true})
Object.defineProperty(String.prototype, "implementsInterfaceKotlinComparable", {value: true})
Object.defineProperty(Boolean.prototype, "implementsInterfaceKotlinComparable", {value: true})

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
        if (this.endInclusive < this.start) return {done: true, value: undefined}
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