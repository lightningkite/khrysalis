"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
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
        default:
            return mainType.fromJson(item, ...asType.slice(1));
    }
}
exports.parse = parse;
