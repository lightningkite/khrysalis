"use strict";
// Kotlin iterables
Object.defineProperty(exports, "__esModule", { value: true });
const Language_1 = require("./Language");
const iterable_operator_1 = require("iterable-operator");
const Collections_1 = require("./Collections");
function test() {
}
//! Declares kotlin.collections.firstOrNull>kotlin.collections.Iterable
function kotlinCollectionsIterableFirstOrNull(iter) {
    const item = iter[Symbol.iterator]().next();
    if (item.done)
        return null;
    else
        return item.value;
}
exports.kotlinCollectionsIterableFirstOrNull = kotlinCollectionsIterableFirstOrNull;
//! Declares kotlin.collections.lastOrNull>kotlin.collections.Iterable
function kotlinCollectionsIterableLastOrNull(iterable) {
    const iter = iterable[Symbol.iterator]();
    let out = iter.next();
    let lastItem = null;
    while (!out.done) {
        lastItem = out.value;
        out = iter.next();
    }
    return lastItem;
}
exports.kotlinCollectionsIterableLastOrNull = kotlinCollectionsIterableLastOrNull;
//! Declares kotlin.collections.first>kotlin.collections.Iterable
function kotlinCollectionsIterableFirst(iter) {
    const r = kotlinCollectionsIterableFirstOrNull(iter);
    if (r == null)
        throw new Language_1.IllegalArgumentException("Iterable is empty", null);
    return r;
}
exports.kotlinCollectionsIterableFirst = kotlinCollectionsIterableFirst;
//! Declares kotlin.collections.last>kotlin.collections.Iterable
function kotlinCollectionsIterableLast(iterable) {
    const r = kotlinCollectionsIterableLastOrNull(iterable);
    if (r == null)
        throw new Language_1.IllegalArgumentException("Iterable is empty", null);
    return r;
}
exports.kotlinCollectionsIterableLast = kotlinCollectionsIterableLast;
//! Declares kotlin.collections.single>kotlin.collections.Iterable
function kotlinCollectionsIterableSingle(iter) {
    const iterator = iter[Symbol.iterator]();
    const item = iterator.next();
    if (item.done || !iterator.next().done)
        return null;
    else
        return item.value;
}
exports.kotlinCollectionsIterableSingle = kotlinCollectionsIterableSingle;
//! Declares kotlin.collections.singleOrNull>kotlin.collections.Iterable
function kotlinCollectionsIterableSingleOrNull(iter) {
    const r = kotlinCollectionsIterableSingle(iter);
    if (r == null)
        throw new Language_1.IllegalArgumentException("Iterable is empty", null);
    return r;
}
exports.kotlinCollectionsIterableSingleOrNull = kotlinCollectionsIterableSingleOrNull;
//! Declares kotlin.collections.joinToString
function kotlinCollectionsIterableJoinToString(iter, separator = ", ", prefix = "", postfix = "", limit, truncated = "...", transform = (x) => `${x}`) {
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
exports.kotlinCollectionsIterableJoinToString = kotlinCollectionsIterableJoinToString;
//! Declares kotlin.collections.distinctBy
function kotlinCollectionsIterableDistinctBy(iter, selector) {
    const seen = new Collections_1.EqualOverrideSet();
    return new Array(...iterable_operator_1.filter(iter, (e) => Collections_1.setAddCausedChange(seen, selector(e))));
}
exports.kotlinCollectionsIterableDistinctBy = kotlinCollectionsIterableDistinctBy;
//! Declares kotlin.sequences.distinctBy
function kotlinSequencesSequenceDistinctBy(iter, selector) {
    const seen = new Collections_1.EqualOverrideSet();
    return iterable_operator_1.filter(iter, (e) => Collections_1.setAddCausedChange(seen, selector(e)));
}
exports.kotlinSequencesSequenceDistinctBy = kotlinSequencesSequenceDistinctBy;
//# sourceMappingURL=Iterables.js.map