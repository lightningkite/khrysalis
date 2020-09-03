"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const GeoCoordinate_shared_1 = require("khrysalis/dist/location/GeoCoordinate.shared");
const GeoAddress_shared_1 = require("khrysalis/dist/location/GeoAddress.shared");
const Geocoding_actual_1 = require("khrysalis/dist/location/Geocoding.actual");
const rxjs_1 = require("rxjs");
const operators_1 = require("rxjs/operators");
const LatLng_ext_1 = require("./LatLng.ext");
function setupGoogleMaps() {
    const coder = new google.maps.Geocoder();
    Geocoding_actual_1.setGeocodingMethod((this_Geocode, coordinate, maxResults = 1) => {
        let args;
        if (coordinate instanceof GeoCoordinate_shared_1.GeoCoordinate) {
            args = {
                location: LatLng_ext_1.xGeoCoordinateToMaps(coordinate)
            };
        }
        else {
            args = {
                address: coordinate
            };
        }
        let bound = rxjs_1.bindCallback(coder.geocode);
        return bound(args).pipe(operators_1.map((a) => {
            return a[0].map((x) => {
                var _a, _b, _c, _d, _e, _f, _g;
                return new GeoAddress_shared_1.GeoAddress(LatLng_ext_1.xLatLngToKhrysalis(x.geometry.bounds.getCenter()), null, (_a = x.address_components.find((x) => x.types.indexOf("street_address") != -1)) === null || _a === void 0 ? void 0 : _a.short_name, (_b = x.address_components.find((x) => x.types.indexOf("sublocality") != -1)) === null || _b === void 0 ? void 0 : _b.short_name, (_c = x.address_components.find((x) => x.types.indexOf("locality") != -1)) === null || _c === void 0 ? void 0 : _c.short_name, (_d = x.address_components.find((x) => x.types.indexOf("administrative_area_level_2") != -1)) === null || _d === void 0 ? void 0 : _d.short_name, (_e = x.address_components.find((x) => x.types.indexOf("administrative_area_level_1") != -1)) === null || _e === void 0 ? void 0 : _e.short_name, (_f = x.address_components.find((x) => x.types.indexOf("country") != -1)) === null || _f === void 0 ? void 0 : _f.short_name, (_g = x.address_components.find((x) => x.types.indexOf("postal_code") != -1)) === null || _g === void 0 ? void 0 : _g.short_name);
            });
        }));
    });
}
exports.setupGoogleMaps = setupGoogleMaps;
//# sourceMappingURL=setup.js.map