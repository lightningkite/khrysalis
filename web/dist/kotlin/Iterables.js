"use strict";
// Kotlin iterables
Object.defineProperty(exports, "__esModule", { value: true });
const Language_1 = require("./Language");
const lazyOp_1 = require("./lazyOp");
const Collections_1 = require("./Collections");
//! Declares kotlin.collections.firstOrNull>kotlin.collections.Iterable
function xIterableFirstOrNull(iter) {
    const item = iter[Symbol.iterator]().next();
    if (item.done)
        return null;
    else
        return item.value;
}
exports.xIterableFirstOrNull = xIterableFirstOrNull;
//! Declares kotlin.collections.lastOrNull>kotlin.collections.Iterable
function xIterableLastOrNull(iterable) {
    const iter = iterable[Symbol.iterator]();
    let out = iter.next();
    let lastItem = null;
    while (!out.done) {
        lastItem = out.value;
        out = iter.next();
    }
    return lastItem;
}
exports.xIterableLastOrNull = xIterableLastOrNull;
//! Declares kotlin.collections.first>kotlin.collections.Iterable
function xIterableFirst(iter) {
    const r = xIterableFirstOrNull(iter);
    if (r == null)
        throw new Language_1.IllegalArgumentException("Iterable is empty", null);
    return r;
}
exports.xIterableFirst = xIterableFirst;
//! Declares kotlin.collections.last>kotlin.collections.Iterable
function xIterableLast(iterable) {
    const r = xIterableLastOrNull(iterable);
    if (r == null)
        throw new Language_1.IllegalArgumentException("Iterable is empty", null);
    return r;
}
exports.xIterableLast = xIterableLast;
//! Declares kotlin.collections.single>kotlin.collections.Iterable
function xIterableSingle(iter) {
    const iterator = iter[Symbol.iterator]();
    const item = iterator.next();
    if (item.done || !iterator.next().done)
        return null;
    else
        return item.value;
}
exports.xIterableSingle = xIterableSingle;
//! Declares kotlin.collections.singleOrNull>kotlin.collections.Iterable
function xIterableSingleOrNull(iter) {
    const r = xIterableSingle(iter);
    if (r == null)
        throw new Language_1.IllegalArgumentException("Iterable is empty", null);
    return r;
}
exports.xIterableSingleOrNull = xIterableSingleOrNull;
//! Declares kotlin.collections.joinToString
function xIterableJoinToString(iter, separator = ", ", prefix = "", postfix = "", limit, truncated = "...", transform = (x) => `${x}`) {
    let result = prefix;
    let count = 0;
    for (const item of iter) {
        if (count > 0) {
            result += separator;
        }
        result += transform(item);
        count++;
        if (limit && count > limit) {
            result += truncated;
            break;
        }
    }
    return result + postfix;
}
exports.xIterableJoinToString = xIterableJoinToString;
//! Declares kotlin.collections.distinctBy
function xIterableDistinctBy(iter, selector) {
    const seen = new Collections_1.EqualOverrideSet();
    return new Array(...lazyOp_1.filter(iter, (e) => Collections_1.setAddCausedChange(seen, selector(e))));
}
exports.xIterableDistinctBy = xIterableDistinctBy;
//! Declares kotlin.sequences.distinctBy
function xSequenceDistinctBy(iter, selector) {
    const seen = new Collections_1.EqualOverrideSet();
    return lazyOp_1.filter(iter, (e) => Collections_1.setAddCausedChange(seen, selector(e)));
}
exports.xSequenceDistinctBy = xSequenceDistinctBy;
//# sourceMappingURL=Iterables.js.map