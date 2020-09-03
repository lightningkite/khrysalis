"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
//! Declares com.lightningkite.khrysalis.location.distanceToMiles
function xGeoCoordinateDistanceToMiles(this_, other) {
    if ((this_.latitude == other.latitude) && (this_.longitude == other.longitude)) {
        return 0;
    }
    else {
        const thisLatRadians = Math.PI * this_.latitude / 180;
        const otherLatRadians = Math.PI * other.latitude / 180;
        const longitudeDelta = this_.longitude - other.longitude;
        const longitudeDeltaRadians = Math.PI * longitudeDelta / 180;
        let dist = Math.sin(thisLatRadians) * Math.sin(otherLatRadians) + Math.cos(thisLatRadians) * Math.cos(otherLatRadians) * Math.cos(longitudeDeltaRadians);
        if (dist > 1) {
            dist = 1;
        }
        dist = Math.acos(dist);
        dist = dist * 180 / Math.PI;
        dist = dist * 60 * 1.1515;
        return dist;
    }
}
exports.xGeoCoordinateDistanceToMiles = xGeoCoordinateDistanceToMiles;
//# sourceMappingURL=GeoCoordinate.actual.js.map