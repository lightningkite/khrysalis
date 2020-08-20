"use strict";
// export type FullType = Array<any>;
Object.defineProperty(exports, "__esModule", { value: true });
//! Declares kotlin.Exception
//! Declares java.lang.Exception
class Exception extends Error {
    constructor(message, cause) {
        super(message);
        this.cause = cause;
    }
}
exports.Exception = Exception;
function printStackTrace(something) {
    if (something instanceof Error) {
        console.error(`${something.name}: ${something.message}\n${something.stack}`);
    }
    else {
        console.error(`Raw error: ${something}`);
    }
}
exports.printStackTrace = printStackTrace;
//! Declares kotlin.IllegalArgumentException
//! Declares java.lang.IllegalArgumentException
class IllegalArgumentException extends Exception {
}
exports.IllegalArgumentException = IllegalArgumentException;
//! Declares kotlin.IllegalStateException
//! Declares java.lang.IllegalStateException
class IllegalStateException extends Exception {
}
exports.IllegalStateException = IllegalStateException;
//! Declares kotlin.NoSuchElementException
//! Declares java.lang.NoSuchElementException
class NoSuchElementException extends Exception {
}
exports.NoSuchElementException = NoSuchElementException;
function hashString(item) {
    if (item == null)
        return 0;
    let hash = 0, i, chr;
    for (i = 0; i < item.length; i++) {
        chr = item.charCodeAt(i);
        hash = ((hash << 5) - hash) + chr;
        hash |= 0; // Convert to 32bit integer
    }
    return hash;
}
exports.hashString = hashString;
function hashAnything(item) {
    if (item === null || item === undefined)
        return 0;
    switch (typeof item) {
        case "object":
            if (item.hashCode) {
                return item.hashCode();
            }
            else {
                return 0;
            }
        case "number":
            return Math.floor(item);
        case "string":
            return hashString(item);
        case "boolean":
            return item ? 1 : 0;
        default:
            return 0;
    }
}
exports.hashAnything = hashAnything;
function safeEq(left, right) {
    if (left !== null && (typeof left) === "object" && left.equals) {
        return left.equals(right);
    }
    else {
        return left === right;
    }
}
exports.safeEq = safeEq;
function checkReified(item, fullType) {
    const type = fullType[0];
    switch (type) {
        case String:
            return typeof item === "string";
        case Number:
            return typeof item === "number";
        case Boolean:
            return typeof item === "boolean";
        case undefined:
            return typeof item === "undefined";
        case null:
            return !item;
        default:
            return item instanceof type;
    }
}
exports.checkReified = checkReified;
function checkIsInterface(item, key) {
    return item.constructor["implementsInterface" + key];
}
exports.checkIsInterface = checkIsInterface;
function tryCastInterface(item, key) {
    if (item.constructor["implementsInterface" + key]) {
        return item;
    }
    else {
        return null;
    }
}
exports.tryCastInterface = tryCastInterface;
function tryCastPrimitive(item, key) {
    if (typeof item === key) {
        return item;
    }
    else {
        return null;
    }
}
exports.tryCastPrimitive = tryCastPrimitive;
function tryCastClass(item, erasedType) {
    if (item instanceof erasedType) {
        return item;
    }
    else {
        return null;
    }
}
exports.tryCastClass = tryCastClass;
function runOrNull(on, action) {
    if (on !== null) {
        return action(on);
    }
    else {
        return null;
    }
}
exports.runOrNull = runOrNull;
function also(item, action) {
    action(item);
    return item;
}
exports.also = also;
function takeIf(item, action) {
    if (action(item))
        return item;
    else
        return null;
}
exports.takeIf = takeIf;
function takeUnless(item, action) {
    if (!action(item))
        return item;
    else
        return null;
}
exports.takeUnless = takeUnless;
function parseIntOrNull(s) {
    const r = parseInt(s);
    if (isNaN(r))
        return null;
    return r;
}
exports.parseIntOrNull = parseIntOrNull;
function parseFloatOrNull(s) {
    const r = parseFloat(s);
    if (isNaN(r))
        return null;
    return r;
}
exports.parseFloatOrNull = parseFloatOrNull;
Object.defineProperty(Number.prototype, "implementsInterfaceKotlinComparable", { value: true });
Object.defineProperty(String.prototype, "implementsInterfaceKotlinComparable", { value: true });
Object.defineProperty(Boolean.prototype, "implementsInterfaceKotlinComparable", { value: true });
class Range {
    constructor(start, endInclusive) {
        this.start = start;
        this.endInclusive = endInclusive;
    }
    contains(element) {
        return element >= this.start && element <= this.endInclusive;
    }
}
exports.Range = Range;
class NumberRange extends Range {
    constructor(start, endInclusive) {
        super(start, endInclusive);
    }
    [Symbol.iterator]() {
        return new NumberRangeIterator(this);
    }
}
exports.NumberRange = NumberRange;
class NumberRangeIterator {
    constructor(range) {
        this.start = range.start;
        this.endInclusive = range.endInclusive;
    }
    next() {
        if (this.endInclusive < this.start)
            return { done: true, value: undefined };
        const result = { done: this.start > this.endInclusive, value: this.start };
        this.start++;
        return result;
    }
}
class CharRange extends Range {
    constructor(start, endInclusive) {
        super(start, endInclusive);
    }
    [Symbol.iterator]() {
        return new CharRangeIterator(this);
    }
}
exports.CharRange = CharRange;
class CharRangeIterator {
    constructor(range) {
        this.startCode = range.start.charCodeAt(0);
        this.endInclusiveCode = range.endInclusive.charCodeAt(0);
    }
    next() {
        const result = { done: this.startCode >= this.endInclusiveCode, value: String.fromCharCode(this.startCode) };
        this.startCode++;
        return result;
    }
}
const rxjs_1 = require("rxjs");
function doOnSubscribe(observable, action) {
    return rxjs_1.defer(() => {
        action(null);
        return observable;
    });
}
exports.doOnSubscribe = doOnSubscribe;
//# sourceMappingURL=Language.js.map