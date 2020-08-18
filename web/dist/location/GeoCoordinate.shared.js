"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: location/GeoCoordinate.shared.kt
// Package: com.lightningkite.khrysalis.location
const jsonParsing_1 = require("../net/jsonParsing");
//! Declares com.lightningkite.khrysalis.location.GeoCoordinate
class GeoCoordinate {
    constructor(latitude, longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    static fromJson(obj) {
        return new GeoCoordinate(jsonParsing_1.parse(obj["latitude"], [Number]), jsonParsing_1.parse(obj["longitude"], [Number]));
    }
    toJSON() {
        return {
            latitude: this.latitude,
            longitude: this.longitude
        };
    }
    hashCode() {
        let hash = 17;
        hash = 31 * hash + Math.floor(this.latitude);
        hash = 31 * hash + Math.floor(this.longitude);
        return hash;
    }
    equals(other) { return other instanceof GeoCoordinate && this.latitude === other.latitude && this.longitude === other.longitude; }
    toString() { return `GeoCoordinate(latitude = ${this.latitude}, longitude = ${this.longitude})`; }
    copy(latitude = this.latitude, longitude = this.longitude) { return new GeoCoordinate(latitude, longitude); }
}
exports.GeoCoordinate = GeoCoordinate;
//# sourceMappingURL=GeoCoordinate.shared.js.map