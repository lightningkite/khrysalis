// Kotlin iterables

import {IllegalArgumentException} from "./Language";
import {filter, map} from './lazyOp'
import {EqualOverrideSet, setAddCausedChange} from "./Collections";

//! Declares kotlin.collections.firstOrNull>kotlin.collections.Iterable
export function xIterableFirstOrNull<T>(iter: Iterable<T>): T | null {
    const item = iter[Symbol.iterator]().next()
    if (item.done) return null
    else return item.value
}

//! Declares kotlin.collections.lastOrNull>kotlin.collections.Iterable
export function xIterableLastOrNull<T>(iterable: Iterable<T>): T | null {
    const iter = iterable[Symbol.iterator]()
    let out = iter.next();
    let lastItem: T | null = null;
    while (!out.done) {
        lastItem = out.value;
        out = iter.next();
    }
    return lastItem;
}

//! Declares kotlin.collections.first>kotlin.collections.Iterable
export function xIterableFirst<T>(iter: Iterable<T>): T {
    const r = xIterableFirstOrNull(iter);
    if (r == null) throw new IllegalArgumentException("Iterable is empty", null);
    return r
}

//! Declares kotlin.collections.last>kotlin.collections.Iterable
export function xIterableLast<T>(iterable: Iterable<T>): T | null {
    const r = xIterableLastOrNull(iterable);
    if (r == null) throw new IllegalArgumentException("Iterable is empty", null);
    return r
}

//! Declares kotlin.collections.single>kotlin.collections.Iterable
export function xIterableSingle<T>(iter: Iterable<T>): T | null {
    const iterator = iter[Symbol.iterator]();
    const item = iterator.next()
    if (item.done || !iterator.next().done) return null
    else return item.value
}

//! Declares kotlin.collections.singleOrNull>kotlin.collections.Iterable
export function xIterableSingleOrNull<T>(iter: Iterable<T>): T {
    const r = xIterableSingle(iter);
    if (r == null) throw new IllegalArgumentException("Iterable is empty", null);
    return r
}

//! Declares kotlin.collections.joinToString
export function xIterableJoinToString<T>(
    iter: Iterable<T>,
    separator: string = ", ",
    prefix: string = "",
    postfix: string = "",
    limit?: number,
    truncated: string = "...",
    transform: (t: T) => string = (x) => `${x}`
): string {
    let result = prefix;
    let count = 0;
    for(const item of iter){
        if(count > 0){
            result += separator;
        }
        result += transform(item);
        count++;
        if(limit && count > limit) {
            result += truncated;
            break;
        }
    }
    return result + postfix;
}

//! Declares kotlin.collections.distinctBy
export function xIterableDistinctBy<T, D>(iter: Iterable<T>, selector: (t: T)=>D): Array<T> {
    const seen = new EqualOverrideSet<D>()
    return new Array(...filter(iter, (e) => setAddCausedChange<D>(seen, selector(e))))
}
//! Declares kotlin.sequences.distinctBy
export function xSequenceDistinctBy<T, D>(iter: Iterable<T>, selector: (t: T)=>D): Iterable<T> {
    const seen = new EqualOverrideSet<D>()
    return filter(iter, (e) => setAddCausedChange<D>(seen, selector(e)))
}