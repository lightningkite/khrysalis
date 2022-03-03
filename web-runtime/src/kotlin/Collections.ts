import {Exception, hashAnything, safeEq} from "./Language";
import {safeCompare} from "./Comparable";
import {join} from "./Iterables";
import {execPipe, flatMap, map, flat, filter} from "iter-tools-es";

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

export class EqualOverrideSet<T> implements Set<T> {
    private readonly map: EqualOverrideMap<T, T>

    public constructor(
        items?: Iterable<T>,
        hasher: (k: T) => number = (k) => hashAnything(k),
        equaler: (k1: T, k2: T) => boolean = (k1, k2) => safeEq(k1, k2)
    ) {
        this.map = new EqualOverrideMap<T, T>(
            items ? map((x) => [x, x], items): [],
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
        return "[" + execPipe(this.keys(), map(x => `${x}`), join(", ")) + "]"
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

export class EqualOverrideMap<K, V> implements Map<K, V> {
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
        return getFullIter(map(x => x[0], flat(1, this.internalEntries.values())));
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
        return getFullIter(map(x => x[1], flat(1, this.internalEntries.values())));
    }

    entries(): IterableIterator<[K, V]> {
        return getFullIter(flat(1, this.internalEntries.values()));
    }

    clear() {
        this.internalEntries = new Map();
        this.size = 0;
    }

    toString(): string {
        return "[" + execPipe(this.entries(), map(x => `${x[0]}: ${x[1]}`), join(", ")) + "]"
    }

    [Symbol.iterator](): IterableIterator<[K, V]> {
        const flattened = flat(1, this.internalEntries.values())[Symbol.iterator]();
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

//! Declares kotlin.collections.Collection
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
if(Array.prototype.size === undefined){
    Object.defineProperty(Array.prototype, "size", {
        get: function () {
            return this.length
        }
    });
}
if(Array.prototype.equals === undefined){
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

export function xIterableMinus<T>(this_: Iterable<T>, item: T): Array<T> {
    let array = [...this_];
    listRemoveFirst(array, (x) => safeEq(item, x));
    return array;
}

export function xIterableMinusMultiple<T>(this_: Iterable<T>, items: Array<T>): Array<T> {
    let array = [...this_];
    for(const item in items) {
        listRemoveFirst(array, (x) => safeEq(item, x));
    }
    return array;
}

export function setAddCausedChange<T>(set: Set<T>, item: T): boolean {
    if (set.has(item)) return false;
    set.add(item);
    return true;
}

export function xMutableMapGetOrPut<K, V>(map: Map<K, V>, key: K, valueGenerator: () => V): V {
    if (map.has(key)) {
        return map.get(key) as V
    } else {
        const newValue = valueGenerator();
        map.set(key, newValue);
        return newValue
    }
}

export function xMapPlus<K, V>(lhs: Map<K, V>, rhs: Map<K, V>): Map<K, V> {
    const newMap: Map<K, V> = lhs instanceof EqualOverrideMap ? new EqualOverrideMap() : new Map();
    xMapPutAll(newMap, lhs)
    xMapPutAll(newMap, rhs)
    return newMap;
}
export function xMapPutAll<K, V>(map: Map<K, V>, other: Map<K, V>) {
    for(let [key, value] of other){
        map.set(key, value);
    }
}
export function xMapMapValues<K, V, VOUT>(this_: Map<K, V>, transform: (entry: [K, V]) => VOUT) {
    const newMap: Map<K, VOUT> = this_ instanceof EqualOverrideMap ? new EqualOverrideMap() : new Map();
    for(const entry of this_.entries()) {
        newMap.set(entry[0], transform(entry))
    }
    return newMap
}
export function xMapFilter<K, V>(this_: Map<K, V>, predicate: (entry: [K, V]) => boolean) {
    const newMap: Map<K, V> = this_ instanceof EqualOverrideMap ? new EqualOverrideMap() : new Map();
    for(const entry of this_.entries()) {
        if(predicate(entry)) {
            newMap.set(entry[0], entry[1])
        }
    }
    return newMap
}
export function xMapPlusPair<K, V>(this_: Map<K, V>, pair: [K, V]): Map<K, V> {
    const newMap: Map<K, V> = this_ instanceof EqualOverrideMap ? new EqualOverrideMap() : new Map();
    xMapPutAll(newMap, this_)
    newMap.set(pair[0], pair[1])
    return newMap;
}
export function xMapMinus<K, V>(this_: Map<K, V>, key: K): Map<K, V> {
    const newMap: Map<K, V> = this_ instanceof EqualOverrideMap ? new EqualOverrideMap() : new Map();
    xMapPutAll(newMap, this_)
    this_.delete(key)
    return newMap;
}