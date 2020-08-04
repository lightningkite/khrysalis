"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const jsonParsing_1 = require("./net/jsonParsing");
//! Declares com.lightningkite.khrysalis.JsonList
exports.JsonList = Array;
//! Declares com.lightningkite.khrysalis.JsonMap
exports.JsonMap = Map;
//! Declares com.lightningkite.khrysalis.toJsonString>kotlin.Any
function kotlinAnyToJsonString(this_) {
    return JSON.stringify(this_);
}
exports.kotlinAnyToJsonString = kotlinAnyToJsonString;
//! Declares com.lightningkite.khrysalis.fromJsonString>kotlin.String
function kotlinStringFromJsonString(this_, T) {
    return jsonParsing_1.parse(JSON.parse(this_), T);
}
exports.kotlinStringFromJsonString = kotlinStringFromJsonString;
//! Declares com.lightningkite.khrysalis.fromJsonStringUntyped>kotlin.String
function kotlinStringFromJsonStringUntyped(this_) {
    return JSON.parse(this_);
}
exports.kotlinStringFromJsonStringUntyped = kotlinStringFromJsonStringUntyped;
//# sourceMappingURL=Codable.actual.js.map