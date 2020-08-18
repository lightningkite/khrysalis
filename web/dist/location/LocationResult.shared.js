"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: location/LocationResult.shared.kt
// Package: com.lightningkite.khrysalis.location
const Kotlin_1 = require("../Kotlin");
const GeoCoordinate_shared_1 = require("./GeoCoordinate.shared");
//! Declares com.lightningkite.khrysalis.location.LocationResult
class LocationResult {
    constructor(coordinate = new GeoCoordinate_shared_1.GeoCoordinate(0.0, 0.0), accuracyMeters = 100.0, altitudeMeters = 0.0, altitudeAccuracyMeters = 100.0, headingFromNorth = 0.0, speedMetersPerSecond = 0.0) {
        this.coordinate = coordinate;
        this.accuracyMeters = accuracyMeters;
        this.altitudeMeters = altitudeMeters;
        this.altitudeAccuracyMeters = altitudeAccuracyMeters;
        this.headingFromNorth = headingFromNorth;
        this.speedMetersPerSecond = speedMetersPerSecond;
    }
    hashCode() {
        var _a, _b;
        let hash = 17;
        hash = 31 * hash + ((_b = (_a = this.coordinate) === null || _a === void 0 ? void 0 : _a.hashCode()) !== null && _b !== void 0 ? _b : 0);
        hash = 31 * hash + Math.floor(this.accuracyMeters);
        hash = 31 * hash + Math.floor(this.altitudeMeters);
        hash = 31 * hash + Math.floor(this.altitudeAccuracyMeters);
        hash = 31 * hash + Math.floor(this.headingFromNorth);
        hash = 31 * hash + Math.floor(this.speedMetersPerSecond);
        return hash;
    }
    equals(other) { return other instanceof LocationResult && Kotlin_1.safeEq(this.coordinate, other.coordinate) && this.accuracyMeters === other.accuracyMeters && this.altitudeMeters === other.altitudeMeters && this.altitudeAccuracyMeters === other.altitudeAccuracyMeters && this.headingFromNorth === other.headingFromNorth && this.speedMetersPerSecond === other.speedMetersPerSecond; }
    toString() { return `LocationResult(coordinate = ${this.coordinate}, accuracyMeters = ${this.accuracyMeters}, altitudeMeters = ${this.altitudeMeters}, altitudeAccuracyMeters = ${this.altitudeAccuracyMeters}, headingFromNorth = ${this.headingFromNorth}, speedMetersPerSecond = ${this.speedMetersPerSecond})`; }
    copy(coordinate = this.coordinate, accuracyMeters = this.accuracyMeters, altitudeMeters = this.altitudeMeters, altitudeAccuracyMeters = this.altitudeAccuracyMeters, headingFromNorth = this.headingFromNorth, speedMetersPerSecond = this.speedMetersPerSecond) { return new LocationResult(coordinate, accuracyMeters, altitudeMeters, altitudeAccuracyMeters, headingFromNorth, speedMetersPerSecond); }
}
exports.LocationResult = LocationResult;
//# sourceMappingURL=LocationResult.shared.js.map