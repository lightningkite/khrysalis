"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const iterable_operator_1 = require("iterable-operator");
const TimeAlone_actual_1 = require("../time/TimeAlone.actual");
const DateAlone_actual_1 = require("../time/DateAlone.actual");
function parse(item, asType) {
    const mainType = asType[0];
    switch (mainType) {
        case null:
        case String:
        case Number:
        case Boolean:
            return item;
        case Array:
            return item.map(x => parse(x, asType[1]));
        case Map:
            let asObj = item;
            let map = new Map();
            if (asType[1] === String) {
                for (const key in Object.keys(asObj)) {
                    map.set(key, parse(asObj[key], asType[2]));
                }
            }
            else {
                for (const key in Object.keys(asObj)) {
                    map.set(parse(key, asType[1]), parse(asObj[key], asType[2]));
                }
            }
            return map;
        case Date:
            return Date.parse(item);
        case DateAlone_actual_1.DateAlone:
            return DateAlone_actual_1.DateAlone.Companion.INSTANCE.iso(item);
        case TimeAlone_actual_1.TimeAlone:
            return TimeAlone_actual_1.TimeAlone.Companion.INSTANCE.iso(item);
        default:
            if (mainType.fromJson) {
                return mainType.fromJson(item, ...asType.slice(1));
            }
            else if (mainType.values) {
                return iterable_operator_1.find(mainType.values(), (x) => x.name.toLowerCase() == item.toLowerCase());
            }
            else {
                throw `Not sure how to parse something of type ${mainType} - 'fromJson' is missing!`;
            }
    }
}
exports.parse = parse;
//# sourceMappingURL=jsonParsing.js.map