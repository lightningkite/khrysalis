import {Exception, hashAnything, safeEq} from "./Language";
import {filter, flatten, map} from "./lazyOp";
import {xIterableJoinToString} from "./Iterables";
import {safeCompare} from "./Comparable";

export function mapForKeyType<K, V>(type: any): Map<K, V> {
    switch (type) {
        case Number:
        case String:
        case Boolean:
            return new Map();
        default:
            if (type.prototype.equals) {
                return new EqualOverrideMap();
            } else {
                return new Map();
            }
    }
}

export function setForType<V>(type: any): Set<V> {
    switch (type) {
        case Number:
        case String:
        case Boolean:
            return new Set();
        default:
            if (type.prototype.equals) {
                return new EqualOverrideSet();
            } else {
                return new Set();
            }
    }
}

function getFullIter<T>(iter: Iterable<T>): IterableIterator<T> {
    const iterator = iter[Symbol.iterator]();
    return {
        [Symbol.iterator](): IterableIterator<T> {
            return this;
        }, next(args: any): IteratorResult<T, any> {
            return iterator.next(args);
        }, return(value: any): IteratorResult<T, any> {
            if(iterator.return){
                return iterator.return(value);
            } else {
                throw new Exception("Function not implemented in parent", undefined)
            }
        }, throw(e: any): IteratorResult<T, any> {
            if(iterator.throw){
                return iterator.throw(e);
            } else {
                throw new Exception("Function not implemented in parent", undefined)
            }
        }
    }
}

export class EqualOverrideSet<T extends Object> implements Set<T> {
    private readonly map: EqualOverrideMap<T, T>

    public constructor(
        items?: Iterable<T>,
        hasher: (k: T) => number = (k) => hashAnything(k),
        equaler: (k1: T, k2: T) => boolean = (k1, k2) => safeEq(k1, k2)
    ) {
        this.map = new EqualOverrideMap<T, T>(
            items ? map(items, (x) => [x, x]): [],
            hasher,
            equaler
        );
    }

    add(element: T): this {
        this.map.set(element, element)
        return this;
    }

    clear() {
        this.map.clear()
    }

    delete(element: T): boolean {
        return this.map.delete(element)
    }

    has(element: T): boolean {
        return this.map.has(element)
    }

    toString(): string {
        return xIterableJoinToString(
            this.keys(),
            ", ",
            "[",
            "]",
            undefined,
            undefined,
            (x) => `${x}`
        )
    }

    values(): IterableIterator<T> {
        return this.map.keys()[Symbol.iterator]()
    }

    keys(): IterableIterator<T> {
        return this.map.keys()[Symbol.iterator]()
    }

    forEach(callbackfn: (value: T, value2: T, set: Set<T>) => void, thisArg?: any): void {
        let index = 0
        for (const sublist of this.map.internalEntries) {
            for (const entry of sublist[1]) {
                const item = entry[0];
                callbackfn(item, item, this);
                index++;
            }
        }
    }

    get size(): number {
        return this.map.size;
    }

    [Symbol.iterator](): IterableIterator<T> {
        return this.map.keys()
    }

    entries(): IterableIterator<[any, T]> {
        return this.map.entries()
    }

    get [Symbol.toStringTag](): string {
        return "EqualOverrideSet"
    };
}

export class EqualOverrideMap<K extends Object, V> implements Map<K, V> {
    internalEntries: Map<number, Array<[K, V]>> = new Map();
    public size: number = 0;
    public hasher: (k: K) => number
    public equaler: (k1: K, k2: K) => boolean

    public constructor(
        items?: Iterable<[K, V]>,
        hasher: (k: K) => number = (k) => hashAnything(k),
        equaler: (k1: K, k2: K) => boolean = (k1, k2) => safeEq(k1, k2)
    ) {
        this.hasher = hasher;
        this.equaler = equaler;
        if (items) {
            for (const x of items) {
                this.set(x[0], x[1]);
            }
        }
    }

    private getListForHash(hash: number): Array<[K, V]> {
        let e = this.internalEntries.get(hash)
        if (e === undefined) {
            e = []
            this.internalEntries.set(hash, e)
        }
        return e
    }

    private getMaybeListForHash(hash: number): Array<[K, V]> | null {
        return this.internalEntries.get(hash) ?? null
    }

    private getEntryFromList(list: Array<[K, V]>, key: K): [K, V] | null {
        for (const entry of list) {
            if (this.equaler(entry[0], key)) {
                return entry
            }
        }
        return null
    }

    set(key: K, value: V): this {
        const list = this.getListForHash(this.hasher(key));
        const entry = this.getEntryFromList(list, key);
        if (entry != null) {
            entry[1] = value;
        } else {
            list.push([key, value]);
            this.size++;
        }
        return this;
    }

    get(key: K): V | undefined {
        const list = this.getListForHash(this.hasher(key));
        const entry = this.getEntryFromList(list, key);
        if (entry == null) return undefined;
        return entry[1];
    }

    has(key: K): boolean {
        const list = this.getListForHash(this.hasher(key));
        const entry = this.getEntryFromList(list, key);
        return entry != null;
    }

    keys(): IterableIterator<K> {
        return getFullIter(map(flatten<[K, V]>(this.internalEntries.values()), (x) => x[0]));
    }

    delete(key: K): boolean {
        const list = this.getMaybeListForHash(this.hasher(key));
        if (!list) return false;
        let index = 0;
        for (const pair of list) {
            if (this.equaler(pair[0], key)) {
                list.splice(index, 1);
                this.size--;
                return true;
            }
            index++;
        }
        return false;
    }

    values(): IterableIterator<V> {
        return getFullIter(map(flatten<[K, V]>(this.internalEntries.values()), (x) => x[1]));
    }

    entries(): IterableIterator<[K, V]> {
        return getFullIter(flatten<[K, V]>(this.internalEntries.values()));
    }

    clear() {
        this.internalEntries = new Map();
        this.size = 0;
    }

    toString(): string {
        return xIterableJoinToString(
            this.entries(),
            ", ",
            "[",
            "]",
            undefined,
            undefined,
            (x) => `${x[0]}: ${x[1]}`
        )
    }

    [Symbol.iterator](): IterableIterator<[K, V]> {
        const flattened = flatten<[K, V]>(this.internalEntries.values())[Symbol.iterator]();
        (flattened as any)[Symbol.iterator] = function x(): IterableIterator<[K, V]> {
            return this;
        };
        return flattened as IterableIterator<[K, V]>;
    }

    get [Symbol.toStringTag](): string {
        return "EqualOverrideMap"
    };

    forEach(callbackfn: (value: V, key: K, map: Map<K, V>) => void, thisArg?: any): void {
        let index = 0
        for (const sublist of this.internalEntries) {
            for (const entry of sublist[1]) {
                callbackfn(entry[1], entry[0], this);
                index++;
            }
        }
    }
}

export interface Collection<V> extends Iterable<V> {
    size: number

    entries(): IterableIterator<[any, V]>;

    keys(): IterableIterator<any>;

    values(): IterableIterator<V>;
}

declare global {
    interface Array<T> extends Collection<T> {
        size: number;

        equals(other: any): boolean;
    }

    interface Set<T> extends Collection<T> {
    }
}
//Freakin' JS inconsistency.  We'll have to fix it.
Object.defineProperty(Array.prototype, "size", {
    get: function () {
        return this.length
    }
});
Object.defineProperty(Array.prototype, "equals", {
    value: function (other: any): boolean {
        if (Array.isArray(other) && this.length === other.length) {
            for (let i = 0; i < this.length; i++) {
                if (!safeEq(this[i], other[i])) {
                    return false;
                }
            }
            return true;
        } else {
            return false
        }
    }
});

export function listFilterNotNull<T>(array: Array<T | null>): Array<T> {
    return array.filter((it: T | null) => it !== null) as Array<T>
}

export function iterableFilterNotNull<T>(iterable: Iterable<T | null>): Iterable<T> {
    return filter(iterable, (x) => x != null) as Iterable<T>
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
//! Declares kotlin.collections.minus
export function xIterableMinus<T>(this_: Iterable<T>, item: T): Array<T> {
    let array = [...this_];
    listRemoveFirst(array, (x) => safeEq(item, x));
    return array;
}

export function iterFirstOrNull<T>(iterable: Iterable<T>): (T | null) {
    const it = iterable[Symbol.iterator]();
    const result = it.next();
    if (result.done) return null;
    return result.value as T;
}

export function iterLastOrNull<T>(iterable: Iterable<T>): (T | null) {
    let result: (T | null) = null;
    for (const item of iterable) {
        result = item;
    }
    return result;
}

export function iterCount<T>(iterable: Iterable<T>, func: (a: T)=>boolean): number {
    let count = 0;
    for (const item of iterable) {
        if(func(item)) count++;
    }
    return count;
}

export function setAddCausedChange<T>(set: Set<T>, item: T): boolean {
    if (set.has(item)) return false;
    set.add(item);
    return true;
}

//! Declares kotlin.collections.getOrPut
export function xMutableMapGetOrPut<K, V>(map: Map<K, V>, key: K, valueGenerator: () => V): V {
    if (map.has(key)) {
        return map.get(key) as V
    } else {
        const newValue = valueGenerator();
        map.set(key, newValue);
        return newValue
    }
}

export function iterMaxBy<T, V>(iter: Iterable<T>, selector: (t: T)=>V): T | null {
    let result: T | null = null;
    let best: V | null = null;
    for(const item of iter){
        const sel = selector(item);
        if(best === null || safeCompare(sel, best) > 0) {
            result = item;
            best = sel;
        }
    }
    return result;
}

export function iterMinBy<T, V>(iter: Iterable<T>, selector: (t: T)=>V): T | null {
    let result: T | null = null;
    let best: V | null = null;
    for(const item of iter){
        const sel = selector(item);
        if(best === null || safeCompare(sel, best) < 0) {
            result = item;
            best = sel;
        }
    }
    return result;
}

//! Declares kotlin.collections.plus
export function xMapPlus<K, V>(lhs: Map<K, V>, rhs: Map<K, V>): Map<K, V> {
    const newMap: Map<K, V> = lhs instanceof EqualOverrideMap ? new EqualOverrideMap() : new Map();
    xMapPutAll(newMap, lhs)
    xMapPutAll(newMap, rhs)
    return newMap;
}
//! Declares kotlin.collections.putAll
export function xMapPutAll<K, V>(map: Map<K, V>, other: Map<K, V>) {
    for(let [key, value] of other){
        map.set(key, value);
    }
}