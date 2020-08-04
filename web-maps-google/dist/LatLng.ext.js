"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const GeoCoordinate_shared_1 = require("khrysalis/dist/location/GeoCoordinate.shared");
//! Declares com.lightningkite.khrysalis.maps.toMaps
function comGoogleAndroidGmsMapsModelLatLngToKhrysalis(this_) {
    return new GeoCoordinate_shared_1.GeoCoordinate(this_.lat(), this_.lng());
}
exports.comGoogleAndroidGmsMapsModelLatLngToKhrysalis = comGoogleAndroidGmsMapsModelLatLngToKhrysalis;
//! Declares com.lightningkite.khrysalis.maps.toKhrysalis
function comLightningkiteKhrysalisLocationGeoCoordinateToMaps(this_) {
    return new google.maps.LatLng(this_.latitude, this_.longitude);
}
exports.comLightningkiteKhrysalisLocationGeoCoordinateToMaps = comLightningkiteKhrysalisLocationGeoCoordinateToMaps;
//# sourceMappingURL=LatLng.ext.js.map