// Kotlin iterables

import {IllegalArgumentException, safeEq} from "./Language";
import {EqualOverrideSet, setAddCausedChange} from "./Collections";
import {execPipe, filter, isNull, IterableIterator, notNull, reduce, size, Wrappable} from "iter-tools-es";

function __reduceOr<T>(iterable: Iterable<T>, reducer: (result: T, value: T, i: number) => T): T | null {
    const iterator = iterable[Symbol.iterator]()
    const first = iterator.next()
    if (first.done) return null
    let current = first.value
    let index = 1
    for (; ;) {
        let next = iterator.next()
        if (next.done) return current
        current = reducer(current, next.value, index++)
    }
}
export function reduceOr<T>(reducer: (result: T, value: T, i: number) => T): (iterable: Wrappable<T>) => T | null;
export function reduceOr<T>(reducer: (result: T, value: T, i: number) => T, iterable: Wrappable<T>): T | null;
export function reduceOr<T>(reducer: (result: T, value: T, i: number) => T, iterable?: Wrappable<T>): T | null | ((iterable: Wrappable<T>) => T | null) {
    if(iterable) return __reduceOr(iterable, reducer)
    else return iterable => iterable === undefined || iterable === null ? null : __reduceOr(iterable, reducer)
}

export function xIterableSingle<T>(iter: Iterable<T>): T | null {
    const iterator = iter[Symbol.iterator]();
    const item = iterator.next()
    if (item.done || !iterator.next().done) return null
    else return item.value
}

export function xIterableSingleOrNull<T>(iter: Iterable<T>): T {
    const r = xIterableSingle(iter);
    if (r == null) throw new IllegalArgumentException("Iterable is empty", null);
    return r
}

export function __join(
    iter: Iterable<string>,
    separator: string = ", ",
    limit?: number,
    truncated: string = "..."
): string {
    let result = ""
    let count = 0;
    for (const item of iter) {
        if (count > 0) {
            result += separator;
        }
        result += item;
        count++;
        if (limit && count > limit) {
            result += truncated;
            break;
        }
    }
    return result
}

export function join(
    separator: string = ", ",
    limit?: number,
    truncated: string = "..."
): ((iter: Iterable<string>) => string) {
    return iter => __join(iter, separator, limit, truncated)
}

export function xIterableContains<T>(iter: Iterable<T>, item: T): boolean {
    for (const x of iter) {
        if (safeEq(x, item)) {
            return true
        }
    }
    return false
}
