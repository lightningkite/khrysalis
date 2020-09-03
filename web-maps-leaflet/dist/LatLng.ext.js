"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const GeoCoordinate_shared_1 = require("khrysalis/dist/location/GeoCoordinate.shared");
const leaflet_1 = require("leaflet");
//! Declares com.lightningkite.khrysalis.maps.toMaps
function xLatLngToMaps(this_) {
    return new GeoCoordinate_shared_1.GeoCoordinate(this_.lat, this_.lng);
}
exports.xLatLngToMaps = xLatLngToMaps;
//! Declares com.lightningkite.khrysalis.maps.toKhrysalis
function xGeoCoordinateToKhrysalis(this_) {
    return new leaflet_1.LatLng(this_.latitude, this_.longitude);
}
exports.xGeoCoordinateToKhrysalis = xGeoCoordinateToKhrysalis;
//# sourceMappingURL=LatLng.ext.js.map