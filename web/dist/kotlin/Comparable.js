"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
function safeCompare(left, right) {
    if (left === null) {
        if (right === null) {
            return 0;
        }
        return -1;
    }
    if (right === null) {
        return 1;
    }
    if (typeof left === "object" && !(left instanceof Date)) {
        return left.compareTo(right);
    }
    else {
        if (left < right)
            return -1;
        else if (left == right)
            return 0;
        else
            return 1;
    }
}
exports.safeCompare = safeCompare;
function cMin(a, b) {
    if (safeCompare(a, b) < 0) {
        return a;
    }
    else {
        return b;
    }
}
exports.cMin = cMin;
function cMax(a, b) {
    if (safeCompare(a, b) > 0) {
        return a;
    }
    else {
        return b;
    }
}
exports.cMax = cMax;
function cCoerce(value, low, high) {
    return cMin(high, cMax(value, low));
}
exports.cCoerce = cCoerce;
//# sourceMappingURL=Comparable.js.map