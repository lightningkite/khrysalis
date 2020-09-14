"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const iterable_operator_1 = require("iterable-operator");
function repair(f) {
    return (...args) => {
        return {
            [Symbol.iterator]: () => f(...args)[Symbol.iterator]()
        };
    };
}
exports.chunkBy = repair(iterable_operator_1.chunkBy);
exports.chunk = repair(iterable_operator_1.chunk);
exports.concat = repair(iterable_operator_1.concat);
exports.dropRight = repair(iterable_operator_1.dropRight);
exports.dropUntil = repair(iterable_operator_1.dropUntil);
exports.drop = repair(iterable_operator_1.drop);
exports.filter = repair(iterable_operator_1.filter);
exports.flattenBy = repair(iterable_operator_1.flattenBy);
exports.flattenDeep = repair(iterable_operator_1.flattenDeep);
exports.flatten = repair(iterable_operator_1.flatten);
exports.map = repair(iterable_operator_1.map);
exports.repeat = repair(iterable_operator_1.repeat);
exports.slice = repair(iterable_operator_1.slice);
exports.splitBy = repair(iterable_operator_1.splitBy);
exports.split = repair(iterable_operator_1.split);
exports.takeRight = repair(iterable_operator_1.takeRight);
exports.takeUntil = repair(iterable_operator_1.takeUntil);
exports.take = repair(iterable_operator_1.take);
exports.tap = repair(iterable_operator_1.tap);
exports.transform = repair(iterable_operator_1.transform);
exports.uniqBy = repair(iterable_operator_1.uniqBy);
exports.uniq = repair(iterable_operator_1.uniq);
exports.zip = repair(iterable_operator_1.zip);
exports.countdown = iterable_operator_1.countdown;
exports.countup = iterable_operator_1.countup;
exports.range = iterable_operator_1.range;
exports.consume = iterable_operator_1.consume;
exports.each = iterable_operator_1.each;
exports.every = iterable_operator_1.every;
function find(iter, predicate) {
    for (const item of iter) {
        if (predicate(item)) {
            return item;
        }
    }
    return null;
}
exports.find = find;
exports.first = iterable_operator_1.first;
exports.includes = iterable_operator_1.includes;
exports.match = iterable_operator_1.match;
exports.reduce = iterable_operator_1.reduce;
exports.some = iterable_operator_1.some;
exports.last = iterable_operator_1.last;
exports.toArray = iterable_operator_1.toArray;
exports.toSet = iterable_operator_1.toSet;
//# sourceMappingURL=lazyOp.js.map