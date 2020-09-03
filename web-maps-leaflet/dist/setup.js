"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const GeoCoordinate_shared_1 = require("khrysalis/dist/location/GeoCoordinate.shared");
const GeoAddress_shared_1 = require("khrysalis/dist/location/GeoAddress.shared");
const Geocoding_actual_1 = require("khrysalis/dist/location/Geocoding.actual");
const rxjs_1 = require("rxjs");
const HttpClient_actual_1 = require("khrysalis/dist/net/HttpClient.actual");
const operators_1 = require("rxjs/operators");
const kotlin_text_1 = require("khrysalis/dist/kotlin/kotlin.text");
const MapView_bind_actual_1 = require("./MapView.bind.actual");
const leaflet_1 = require("leaflet");
function setupMapBox(accessToken) {
    MapView_bind_actual_1.setMapConfiguration((map, styleString) => {
        leaflet_1.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
            attribution: '© <a href="https://www.mapbox.com/about/maps/">Mapbox</a> © <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> <strong><a href="https://www.mapbox.com/map-feedback/" target="_blank">Improve this map</a></strong>',
            tileSize: 512,
            maxZoom: 18,
            zoomOffset: -1,
            id: 'mapbox/satellite-streets-v11',
            accessToken: accessToken
        }).addTo(map);
    });
    Geocoding_actual_1.setGeocodingMethod((this_Geocode, coordinate, maxResults = 1) => {
        let call;
        if (coordinate instanceof GeoCoordinate_shared_1.GeoCoordinate) {
            call = rxjs_1.from(HttpClient_actual_1.HttpClient.INSTANCE.call(`https://api.mapbox.com/geocoding/v5/mapbox.places/${coordinate.longitude},${coordinate.latitude}.json?access_token=${accessToken}&limit=${maxResults}`, HttpClient_actual_1.HttpClient.INSTANCE.GET));
        }
        else {
            if (coordinate === "") {
                return rxjs_1.of([]);
            }
            call = rxjs_1.from(HttpClient_actual_1.HttpClient.INSTANCE.call(`https://api.mapbox.com/geocoding/v5/mapbox.places/${coordinate}.json?access_token=${accessToken}&limit=${maxResults}`, HttpClient_actual_1.HttpClient.INSTANCE.GET));
        }
        return call.pipe(operators_1.flatMap((raw) => {
            return raw.json();
        }), operators_1.map((json) => {
            return json.features.map((x) => {
                const extractedContext = new Map(x.context.map((y) => {
                    return [kotlin_text_1.xStringSubstringBefore(y.id, ".", undefined), y.text];
                }));
                extractedContext.set(kotlin_text_1.xStringSubstringBefore(x.id, ".", undefined), x.text);
                return new GeoAddress_shared_1.GeoAddress(
                /*coordinate*/ new GeoCoordinate_shared_1.GeoCoordinate(x.center[1], x.center[0]), 
                /*name*/ x.matching_place_name, 
                /*street*/ x.address, 
                /*subLocality*/ extractedContext.get("neighborhood"), 
                /*locality*/ extractedContext.get("place"), 
                /*subAdminArea*/ undefined, 
                /*adminArea*/ extractedContext.get("region"), 
                /*countryName*/ extractedContext.get("country"), 
                /*postalCode*/ extractedContext.get("postcode"));
            });
        }));
    });
}
exports.setupMapBox = setupMapBox;
//# sourceMappingURL=setup.js.map