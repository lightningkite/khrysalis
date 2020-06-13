"use strict";
// Generated by Khrysalis TypeScript converter
// File: net/HttpResponse.actual.kt
// Package: com.lightningkite.khrysalis.net
Object.defineProperty(exports, "__esModule", { value: true });
//! Declares com.lightningkite.khrysalis.net.code
function getOkhttp3ResponseCode(this_) { return this_.status; }
exports.getOkhttp3ResponseCode = getOkhttp3ResponseCode;
//! Declares com.lightningkite.khrysalis.net.headers
function getOkhttp3ResponseHeaders(this_) {
    let map = new Map();
    this_.headers.forEach((value, key) => {
        const existing = map.get(key);
        if (existing === undefined) {
            map.set(key, value);
        }
        else {
            map.set(key, existing + ";" + value);
        }
    });
    return map;
}
exports.getOkhttp3ResponseHeaders = getOkhttp3ResponseHeaders;
