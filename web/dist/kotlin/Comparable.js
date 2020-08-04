"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
function safeCompare(left, right) {
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
//# sourceMappingURL=Comparable.js.map