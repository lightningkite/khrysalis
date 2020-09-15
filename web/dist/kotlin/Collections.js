"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Language_1 = require("./Language");
const lazyOp_1 = require("./lazyOp");
const Iterables_1 = require("./Iterables");
const Comparable_1 = require("./Comparable");
function mapForKeyType(type) {
    switch (type) {
        case Number:
        case String:
        case Boolean:
            return new Map();
        default:
            if (type.prototype.equals) {
                return new EqualOverrideMap();
            }
            else {
                return new Map();
            }
    }
}
exports.mapForKeyType = mapForKeyType;
function setForType(type) {
    switch (type) {
        case Number:
        case String:
        case Boolean:
            return new Set();
        default:
            if (type.prototype.equals) {
                return new EqualOverrideSet();
            }
            else {
                return new Set();
            }
    }
}
exports.setForType = setForType;
function getFullIter(iter) {
    const iterator = iter[Symbol.iterator]();
    return {
        [Symbol.iterator]() {
            return this;
        }, next(args) {
            return iterator.next(args);
        }, return(value) {
            if (iterator.return) {
                return iterator.return(value);
            }
            else {
                throw new Language_1.Exception("Function not implemented in parent", undefined);
            }
        }, throw(e) {
            if (iterator.throw) {
                return iterator.throw(e);
            }
            else {
                throw new Language_1.Exception("Function not implemented in parent", undefined);
            }
        }
    };
}
class EqualOverrideSet {
    constructor(items, hasher = (k) => Language_1.hashAnything(k), equaler = (k1, k2) => Language_1.safeEq(k1, k2)) {
        this.map = new EqualOverrideMap(items ? lazyOp_1.map(items, (x) => [x, x]) : [], hasher, equaler);
    }
    add(element) {
        this.map.set(element, element);
        return this;
    }
    clear() {
        this.map.clear();
    }
    delete(element) {
        return this.map.delete(element);
    }
    has(element) {
        return this.map.has(element);
    }
    toString() {
        return Iterables_1.xIterableJoinToString(this.keys(), ", ", "[", "]", undefined, undefined, (x) => `${x}`);
    }
    values() {
        return this.map.keys()[Symbol.iterator]();
    }
    keys() {
        return this.map.keys()[Symbol.iterator]();
    }
    forEach(callbackfn, thisArg) {
        let index = 0;
        for (const sublist of this.map.internalEntries) {
            for (const entry of sublist[1]) {
                const item = entry[0];
                callbackfn(item, item, this);
                index++;
            }
        }
    }
    get size() {
        return this.map.size;
    }
    [Symbol.iterator]() {
        return this.map.keys();
    }
    entries() {
        return this.map.entries();
    }
    get [Symbol.toStringTag]() {
        return "EqualOverrideSet";
    }
    ;
}
exports.EqualOverrideSet = EqualOverrideSet;
class EqualOverrideMap {
    constructor(items, hasher = (k) => Language_1.hashAnything(k), equaler = (k1, k2) => Language_1.safeEq(k1, k2)) {
        this.internalEntries = new Map();
        this.size = 0;
        this.hasher = hasher;
        this.equaler = equaler;
        if (items) {
            for (const x of items) {
                this.set(x[0], x[1]);
            }
        }
    }
    getListForHash(hash) {
        let e = this.internalEntries.get(hash);
        if (e === undefined) {
            e = [];
            this.internalEntries.set(hash, e);
        }
        return e;
    }
    getMaybeListForHash(hash) {
        var _a;
        return (_a = this.internalEntries.get(hash)) !== null && _a !== void 0 ? _a : null;
    }
    getEntryFromList(list, key) {
        for (const entry of list) {
            if (this.equaler(entry[0], key)) {
                return entry;
            }
        }
        return null;
    }
    set(key, value) {
        const list = this.getListForHash(this.hasher(key));
        const entry = this.getEntryFromList(list, key);
        if (entry != null) {
            entry[1] = value;
        }
        else {
            list.push([key, value]);
            this.size++;
        }
        return this;
    }
    get(key) {
        const list = this.getListForHash(this.hasher(key));
        const entry = this.getEntryFromList(list, key);
        if (entry == null)
            return undefined;
        return entry[1];
    }
    has(key) {
        const list = this.getListForHash(this.hasher(key));
        const entry = this.getEntryFromList(list, key);
        return entry != null;
    }
    keys() {
        return getFullIter(lazyOp_1.map(lazyOp_1.flatten(this.internalEntries.values()), (x) => x[0]));
    }
    delete(key) {
        const list = this.getMaybeListForHash(this.hasher(key));
        if (!list)
            return false;
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
    values() {
        return getFullIter(lazyOp_1.map(lazyOp_1.flatten(this.internalEntries.values()), (x) => x[1]));
    }
    entries() {
        return getFullIter(lazyOp_1.flatten(this.internalEntries.values()));
    }
    clear() {
        this.internalEntries = new Map();
        this.size = 0;
    }
    toString() {
        return Iterables_1.xIterableJoinToString(this.entries(), ", ", "[", "]", undefined, undefined, (x) => `${x[0]}: ${x[1]}`);
    }
    [Symbol.iterator]() {
        const flattened = lazyOp_1.flatten(this.internalEntries.values())[Symbol.iterator]();
        flattened[Symbol.iterator] = function x() {
            return this;
        };
        return flattened;
    }
    get [Symbol.toStringTag]() {
        return "EqualOverrideMap";
    }
    ;
    forEach(callbackfn, thisArg) {
        let index = 0;
        for (const sublist of this.internalEntries) {
            for (const entry of sublist[1]) {
                callbackfn(entry[1], entry[0], this);
                index++;
            }
        }
    }
}
exports.EqualOverrideMap = EqualOverrideMap;
//Freakin' JS inconsistency.  We'll have to fix it.
Object.defineProperty(Array.prototype, "size", {
    get: function () {
        return this.length;
    }
});
Object.defineProperty(Array.prototype, "equals", {
    value: function (other) {
        if (Array.isArray(other) && this.length === other.length) {
            for (let i = 0; i < this.length; i++) {
                if (!Language_1.safeEq(this[i], other[i])) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
});
function listFilterNotNull(array) {
    return array.filter((it) => it !== null);
}
exports.listFilterNotNull = listFilterNotNull;
function iterableFilterNotNull(iterable) {
    return lazyOp_1.filter(iterable, (x) => x != null);
}
exports.iterableFilterNotNull = iterableFilterNotNull;
function listRemoveAll(array, predicate) {
    let index = 0;
    while (index < array.length) {
        if (predicate(array[index])) {
            array.splice(index, 1);
        }
        else {
            index++;
        }
    }
}
exports.listRemoveAll = listRemoveAll;
function listRemoveFirst(array, predicate) {
    let index = 0;
    while (index < array.length) {
        if (predicate(array[index])) {
            array.splice(index, 1);
            return;
        }
        else {
            index++;
        }
    }
}
exports.listRemoveFirst = listRemoveFirst;
function listRemoveItem(array, item) {
    listRemoveFirst(array, (x) => Language_1.safeEq(item, x));
}
exports.listRemoveItem = listRemoveItem;
//! Declares kotlin.collections.minus
function xIterableMinus(this_, item) {
    let array = [...this_];
    listRemoveFirst(array, (x) => Language_1.safeEq(item, x));
    return array;
}
exports.xIterableMinus = xIterableMinus;
function iterFirstOrNull(iterable) {
    const it = iterable[Symbol.iterator]();
    const result = it.next();
    if (result.done)
        return null;
    return result.value;
}
exports.iterFirstOrNull = iterFirstOrNull;
function iterLastOrNull(iterable) {
    let result = null;
    for (const item of iterable) {
        result = item;
    }
    return result;
}
exports.iterLastOrNull = iterLastOrNull;
function iterCount(iterable, func) {
    let count = 0;
    for (const item of iterable) {
        if (func(item))
            count++;
    }
    return count;
}
exports.iterCount = iterCount;
function setAddCausedChange(set, item) {
    if (set.has(item))
        return false;
    set.add(item);
    return true;
}
exports.setAddCausedChange = setAddCausedChange;
//! Declares kotlin.collections.getOrPut
function xMutableMapGetOrPut(map, key, valueGenerator) {
    if (map.has(key)) {
        return map.get(key);
    }
    else {
        const newValue = valueGenerator();
        map.set(key, newValue);
        return newValue;
    }
}
exports.xMutableMapGetOrPut = xMutableMapGetOrPut;
function iterMaxBy(iter, selector) {
    let result = null;
    let best = null;
    for (const item of iter) {
        const sel = selector(item);
        if (best === null || Comparable_1.safeCompare(sel, best) > 0) {
            result = item;
            best = sel;
        }
    }
    return result;
}
exports.iterMaxBy = iterMaxBy;
function iterMinBy(iter, selector) {
    let result = null;
    let best = null;
    for (const item of iter) {
        const sel = selector(item);
        if (best === null || Comparable_1.safeCompare(sel, best) < 0) {
            result = item;
            best = sel;
        }
    }
    return result;
}
exports.iterMinBy = iterMinBy;
//! Declares kotlin.collections.plus
function xMapPlus(lhs, rhs) {
    const newMap = lhs instanceof EqualOverrideMap ? new EqualOverrideMap() : new Map();
    xMapPutAll(newMap, lhs);
    xMapPutAll(newMap, rhs);
    return newMap;
}
exports.xMapPlus = xMapPlus;
//! Declares kotlin.collections.putAll
function xMapPutAll(map, other) {
    for (let [key, value] of other) {
        map.set(key, value);
    }
}
exports.xMapPutAll = xMapPutAll;
//# sourceMappingURL=Collections.js.map