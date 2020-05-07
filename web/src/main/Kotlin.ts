export function hashString(item: string): number {
    let hash = 0, i, chr;
    for (i = 0; i < this.length; i++) {
        chr   = this.charCodeAt(i);
        hash  = ((hash << 5) - hash) + chr;
        hash |= 0; // Convert to 32bit integer
    }
    return hash;
}

export function also<T>(item: T, action: (T)=>void): T {
    action(item);
    return item;
}

export interface Object {
    hashCode(): number
    equals(other: any): boolean
}
Object.defineProperty(Object.prototype, "hashCode", { value: function(): number { return hashString(this.toString()) } })
Object.defineProperty(Object.prototype, "equals", { value: function(other: any): boolean { return this == other } })

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
        return new RangeIterator(this)
    }
}
class RangeIterator implements Iterator<Number> {
    start: number;
    endInclusive: number;
    constructor(range: NumberRange) {
        this.start = range.start;
        this.endInclusive = range.endInclusive;
    }
    next(): IteratorResult<Number> {
        const result = { done: this.start >= this.endInclusive, value: this.start };
        this.start++;
        return result
    }
}
export function makeRange(start: number, endInclusive: number): NumberRange
export function makeRange<T>(start: T, endInclusive: T): Range<T>
export function makeRange(start: any, endInclusive: any): any {
    if (typeof start == "number" && typeof endInclusive == "number") {
        return new NumberRange(start, endInclusive);
    } else {
        return new Range(start, endInclusive);
    }
}
