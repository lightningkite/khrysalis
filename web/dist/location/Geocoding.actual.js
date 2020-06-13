"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
let geoMethod = (this_Geocode, coordinate, onResult) => { onResult([]); };
function setGeocodingMethod(method) {
    geoMethod = method;
}
exports.setGeocodingMethod = setGeocodingMethod;
//! Declares com.lightningkite.khrysalis.location.geocode
function comLightningkiteKhrysalisAndroidActivityAccessGeocode(this_Geocode, coordinate, onResult) {
    geoMethod(this_Geocode, coordinate, onResult);
}
exports.comLightningkiteKhrysalisAndroidActivityAccessGeocode = comLightningkiteKhrysalisAndroidActivityAccessGeocode;
