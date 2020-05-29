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

export function equalityAnything(left: any, right: any): boolean {
    if(typeof left === "object") {
        return left.equals(right)
    } else {
        return left === right
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
        const result = {done: this.start >= this.endInclusive, value: this.start};
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

export class EqualOverrideSet<E extends Object> {
    private readonly map: EqualOverrideMap<E, 0>

    add(element: E): boolean {
        return this.map.set(element, 0) == null
    }

    clear() {
        this.map.clear()
    }

    delete(element: E) {
        this.map.delete(element)
    }

    has(element: E): boolean {
        return this.map.has(element)
    }

    toString(): string {
        return "[" + this.map.internalEntries.flatMap((x) => x).join(", ") + "]"
    }

    values(): IterableIterator<E> {
        return this.map.keys()[Symbol.iterator]()
    }

    keys(): IterableIterator<E> {
        return this.map.keys()[Symbol.iterator]()
    }

    forEach(action: (e: E, index: number | undefined) => void) {
        let index = 0
        for (const sublist of this.map.internalEntries) {
            for (const entry of sublist) {
                action(entry[0], index);
                index++;
            }
        }
    }
}

export class EqualOverrideMap<K extends Object, V> implements MapInterface<K, V> {
    //This implementation is frankly kinda lazy, but it will do the trick.
    internalEntries: Array<Array<[K, V]>> = []
    size: number = 0;

    private getListForHash(hash: number): Array<[K, V]> {
        let e = this.internalEntries[hash]
        if (e === undefined) {
            e = []
            this.internalEntries[hash] = e
        }
        return e
    }

    private getEntryFromList(list: Array<[K, V]>, key: K): [K, V] | null {
        for (const entry of list) {
            if (entry[0]?.equals(key)) {
                return entry
            }
        }
        return null
    }

    set(key: K, value: V): void {
        const list = this.getListForHash(key.hashCode());
        const entry = this.getEntryFromList(list, key);
        if (entry != null) {
            entry[1] = value;
        } else {
            list.push([key, value]);
            this.size++;
        }
    }

    get(key: K): V | null {
        const list = this.getListForHash(key.hashCode());
        const entry = this.getEntryFromList(list, key);
        if (entry == null) return null;
        return entry[1];
    }

    has(key: K): boolean {
        const list = this.getListForHash(key.hashCode());
        const entry = this.getEntryFromList(list, key);
        return entry != null;
    }

    keys(): IterableIterator<K> {
        return this.internalEntries.flatMap((x) => x.map((y) => y[0]))[Symbol.iterator]()
    }

    delete(key: K) {
        const list = this.getListForHash(key.hashCode());
        let index = 0;
        for (const pair of list) {
            if (pair[0].equals(key)) {
                list.splice(index, 1);
                this.size--;
                break
            }
            index++;
        }
    }

    values(): IterableIterator<V> {
        return this.internalEntries.flatMap((x) => x.map((y) => y[1]))[Symbol.iterator]()
    }

    forEachK(action: (e: [K, V], index: number | undefined) => void) {
        let index = 0
        for (const sublist of this.internalEntries) {
            for (const entry of sublist) {
                action(entry, index);
                index++;
            }
        }
    }

    entries(): IterableIterator<[K, V]> {
        return this.internalEntries.flatMap((x) => x)[Symbol.iterator]()
    }

    clear() {
        this.internalEntries = [];
        this.size = 0;
    }

    toString(): string {
        return "[" + this.internalEntries.flatMap((x) => x).map((x) => `${x[0]}: ${x[1]}`).join(", ") + "]"
    }

    [Symbol.iterator](): Iterator<V> {
        return undefined;
    }
}

export interface Collection<V> extends Iterable<V> {
    size: number

    entries(): IterableIterator<[any, V]>;

    keys(): IterableIterator<any>;

    values(): IterableIterator<V>;
}

export interface MapInterface<K, V> {
    size: number

    entries(): IterableIterator<[K, V]>;

    keys(): IterableIterator<K>;

    values(): IterableIterator<V>;

    set(key: K, value: V): void;

    get(key: K): V | null;

    has(key: K): boolean;

    delete(key: K): void;

    clear(): void;

    toString(): string;
}

declare global {
    interface Array<T> extends Collection<T> {
    }

    interface Map<K, V> extends MapInterface<K, V> {
    }

    interface Set<T> extends Collection<T> {
    }
}
//Freakin' JS inconsistency.  We'll have to fix it.
Object.defineProperty(Array.prototype, "size", {
    get: function () {
        let thing: Map<string, string> = new Map()
        return this.length
    }
})

export function safeEq(a: any, b: any): boolean {
    if (typeof a === "object") {
        return a.equals(b)
    } else {
        return a === b
    }
}

export function listFilterNotNull<T>(array: Array<T | null>): Array<T> {
    return this.filter((it: T | null) => it !== null)
}

export function listRemoveAll<T>(array: Array<T>, predicate: (a: T) => boolean) {
    let index = 0
    while (index < array.length) {
        if (predicate(array[index])) {
            array.splice(index, 1);
        } else {
            index++;
        }
    }
}

export function listRemoveFirst<T>(array: Array<T>, predicate: (a: T) => boolean) {
    let index = 0
    while (index < array.length) {
        if (predicate(array[index])) {
            array.splice(index, 1);
            return;
        } else {
            index++;
        }
    }
}

export function listRemoveItem<T>(array: Array<T>, item: T) {
    listRemoveFirst(array, (x) => safeEq(item, x))
}

import {Observable} from "rxjs";
import {defer as rxDefer} from "rxjs";

export function doOnSubscribe<T>(observable: Observable<T>, action: (x: any) => void) {
    return rxDefer(() => {
        action(null);
        return observable;
    })
}
