import { safeEq} from "./Kotlin";
import {filter} from "iterable-operator";


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

export function listFilterNotNull<T>(array: Array<T | null>): Array<T> {
    return this.filter((it: T | null) => it !== null)
}

export function iterableFilterNotNull<T>(iterable: Iterable<T | null>): Iterable<T> {
    return filter(iterable, (x)=>x != null) as Iterable<T>
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