export declare function mapForKeyType<K, V>(type: any): Map<K, V>;
export declare function setForType<V>(type: any): Set<V>;
export declare class EqualOverrideSet<T extends Object> implements Set<T> {
    private readonly map;
    constructor(items?: Iterable<T>, hasher?: (k: T) => number, equaler?: (k1: T, k2: T) => boolean);
    add(element: T): this;
    clear(): void;
    delete(element: T): boolean;
    has(element: T): boolean;
    toString(): string;
    values(): IterableIterator<T>;
    keys(): IterableIterator<T>;
    forEach(callbackfn: (value: T, value2: T, set: Set<T>) => void, thisArg?: any): void;
    get size(): number;
    [Symbol.iterator](): IterableIterator<T>;
    entries(): IterableIterator<[any, T]>;
    get [Symbol.toStringTag](): string;
}
export declare class EqualOverrideMap<K extends Object, V> implements Map<K, V> {
    internalEntries: Map<number, Array<[K, V]>>;
    size: number;
    hasher: (k: K) => number;
    equaler: (k1: K, k2: K) => boolean;
    constructor(items?: Iterable<[K, V]>, hasher?: (k: K) => number, equaler?: (k1: K, k2: K) => boolean);
    private getListForHash;
    private getMaybeListForHash;
    private getEntryFromList;
    set(key: K, value: V): this;
    get(key: K): V | null;
    has(key: K): boolean;
    keys(): IterableIterator<K>;
    delete(key: K): boolean;
    values(): IterableIterator<V>;
    entries(): IterableIterator<[K, V]>;
    clear(): void;
    toString(): string;
    [Symbol.iterator](): IterableIterator<[K, V]>;
    get [Symbol.toStringTag](): string;
    forEach(callbackfn: (value: V, key: K, map: Map<K, V>) => void, thisArg?: any): void;
}
export interface Collection<V> extends Iterable<V> {
    size: number;
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
export declare function listFilterNotNull<T>(array: Array<T | null>): Array<T>;
export declare function iterableFilterNotNull<T>(iterable: Iterable<T | null>): Iterable<T>;
export declare function listRemoveAll<T>(array: Array<T>, predicate: (a: T) => boolean): void;
export declare function listRemoveFirst<T>(array: Array<T>, predicate: (a: T) => boolean): void;
export declare function listRemoveItem<T>(array: Array<T>, item: T): void;
export declare function iterFirstOrNull<T>(iterable: Iterable<T>): (T | null);
export declare function iterLastOrNull<T>(iterable: Iterable<T>): (T | null);
export declare function setAddCausedChange<T>(set: Set<T>, item: T): boolean;
export declare function kotlinCollectionsMutableMapGetOrPut<K, V>(map: Map<K, V>, key: K, valueGenerator: () => V): V;