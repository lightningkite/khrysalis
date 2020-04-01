export interface Array<T> {
    add(item: T): void;
}

Array.prototype.add = function (item: any) {
    this.push(item);
};

export interface Number {
    toByte(): number;

    toShort(): number;

    toInt(): number;

    toLong(): number;

    toFloat(): number;

    toDouble(): number;
}

Number.prototype.toByte = function (): number {
    return Math.floor(this)
};
Number.prototype.toShort = Number.prototype.toByte;
Number.prototype.toInt = Number.prototype.toByte;
Number.prototype.toLong = Number.prototype.toByte;
Number.prototype.toFloat = function (): number {
    return this
};
Number.prototype.toDouble = Number.prototype.toFloat;

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
