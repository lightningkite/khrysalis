"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const rxjs_1 = require("rxjs");
let geoMethod = (this_Geocode, coordinate, maxResults = 1) => rxjs_1.of([]);
function setGeocodingMethod(method) {
    geoMethod = method;
}
exports.setGeocodingMethod = setGeocodingMethod;
//! Declares com.lightningkite.khrysalis.location.geocode
function comLightningkiteKhrysalisAndroidActivityAccessGeocode(this_Geocode, coordinate, maxResults = 1) {
    return geoMethod(this_Geocode, coordinate);
}
exports.comLightningkiteKhrysalisAndroidActivityAccessGeocode = comLightningkiteKhrysalisAndroidActivityAccessGeocode;
//# sourceMappingURL=Geocoding.actual.js.map