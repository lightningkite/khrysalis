"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const GeoCoordinate_shared_1 = require("khrysalis/dist/location/GeoCoordinate.shared");
const leaflet_1 = require("leaflet");
//! Declares com.lightningkite.khrysalis.maps.toMaps
function comGoogleAndroidGmsMapsModelLatLngToMaps(this_) {
    return new GeoCoordinate_shared_1.GeoCoordinate(this_.lat, this_.lng);
}
exports.comGoogleAndroidGmsMapsModelLatLngToMaps = comGoogleAndroidGmsMapsModelLatLngToMaps;
//! Declares com.lightningkite.khrysalis.maps.toKhrysalis
function comLightningkiteKhrysalisLocationGeoCoordinateToKhrysalis(this_) {
    return new leaflet_1.LatLng(this_.latitude, this_.longitude);
}
exports.comLightningkiteKhrysalisLocationGeoCoordinateToKhrysalis = comLightningkiteKhrysalisLocationGeoCoordinateToKhrysalis;
//# sourceMappingURL=LatLng.ext.js.map