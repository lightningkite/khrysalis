"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const jsonParsing_1 = require("./net/jsonParsing");
//! Declares com.lightningkite.khrysalis.JsonList
exports.JsonList = Array;
//! Declares com.lightningkite.khrysalis.JsonMap
exports.JsonMap = Map;
//! Declares com.lightningkite.khrysalis.toJsonString>kotlin.Any
function xAnyToJsonString(this_) {
    return jsonParsing_1.stringify(this_);
}
exports.xAnyToJsonString = xAnyToJsonString;
//! Declares com.lightningkite.khrysalis.fromJsonString>kotlin.String
function xStringFromJsonString(this_, T) {
    return jsonParsing_1.parse(JSON.parse(this_), T);
}
exports.xStringFromJsonString = xStringFromJsonString;
//! Declares com.lightningkite.khrysalis.fromJsonStringUntyped>kotlin.String
function xStringFromJsonStringUntyped(this_) {
    return jsonParsing_1.parseUntyped(this_);
}
exports.xStringFromJsonStringUntyped = xStringFromJsonStringUntyped;
//# sourceMappingURL=Codable.actual.js.map