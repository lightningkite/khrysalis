"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Language_1 = require("./Language");
const iterable_operator_1 = require("iterable-operator");
const Iterables_1 = require("./Iterables");
function mapForKeyType(type) {
    switch (type) {
        case Number:
        case String:
        case Boolean:
            return new Map();
        default:
            if (type.prototype.equals === Object.prototype.equals) {
                return new Map();
            }
            else {
                return new EqualOverrideMap();
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
            if (type.prototype.equals === Object.prototype.equals) {
                return new Set();
            }
            else {
                return new EqualOverrideSet();
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
            return iterator.return(value);
        }, throw(e) {
            return iterator.throw(e);
        }
    };
}
class EqualOverrideSet {
    constructor(items, hasher = (k) => Language_1.hashAnything(k), equaler = (k1, k2) => Language_1.safeEq(k1, k2)) {
        this.map = new EqualOverrideMap(iterable_operator_1.map(items, (x) => [x, x]), hasher, equaler);
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
        return Iterables_1.kotlinCollectionsIterableJoinToString(this.keys(), ", ", "[", "]", undefined, undefined, (x) => `${x}`);
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
        return this.internalEntries.get(hash);
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
            return null;
        return entry[1];
    }
    has(key) {
        const list = this.getListForHash(this.hasher(key));
        const entry = this.getEntryFromList(list, key);
        return entry != null;
    }
    keys() {
        return getFullIter(iterable_operator_1.map(iterable_operator_1.flatten(this.internalEntries.values()), (x) => x[0]));
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
        return getFullIter(iterable_operator_1.map(iterable_operator_1.flatten(this.internalEntries.values()), (x) => x[1]));
    }
    entries() {
        return getFullIter(iterable_operator_1.flatten(this.internalEntries.values()));
    }
    clear() {
        this.internalEntries = new Map();
        this.size = 0;
    }
    toString() {
        return Iterables_1.kotlinCollectionsIterableJoinToString(this.entries(), ", ", "[", "]", undefined, undefined, (x) => `${x[0]}: ${x[1]}`);
    }
    [Symbol.iterator]() {
        const flattened = iterable_operator_1.flatten(this.internalEntries.values())[Symbol.iterator]();
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
    return iterable_operator_1.filter(iterable, (x) => x != null);
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
function setAddCausedChange(set, item) {
    if (set.has(item))
        return false;
    set.add(item);
    return true;
}
exports.setAddCausedChange = setAddCausedChange;
//! Declares kotlin.collections.getOrPut
function kotlinCollectionsMutableMapGetOrPut(map, key, valueGenerator) {
    if (map.has(key)) {
        return map.get(key);
    }
    else {
        const newValue = valueGenerator();
        map.set(key, newValue);
        return newValue;
    }
}
exports.kotlinCollectionsMutableMapGetOrPut = kotlinCollectionsMutableMapGetOrPut;
//# sourceMappingURL=Collections.js.map