"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const GeoCoordinate_shared_1 = require("khrysalis/dist/location/GeoCoordinate.shared");
const leaflet_1 = require("leaflet");
const DisposeCondition_actual_1 = require("khrysalis/dist/rx/DisposeCondition.actual");
const ObservableProperty_ext_shared_1 = require("khrysalis/dist/observables/ObservableProperty.ext.shared");
const mapSymbol = Symbol("mapSymbol");
let configureMap = () => {
};
/**
 * Set up a map source.
 * MapBox example:
 * (map, styleString) => {
 *     tileLayer(
 *         `https://api.tiles.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}.png?access_token=${accessToken}`,
 *         {
 *             attribution: `Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>`,
 *             maxZoom: 18
 *         }
 *     ).addTo(map);
 * }
 */
function setMapConfiguration(source) {
    configureMap = source;
}
exports.setMapConfiguration = setMapConfiguration;
//! Declares com.google.android.gms.maps.MapView.getMapAsync
function getMapAsync(this_, action) {
    const m = this_[mapSymbol];
    if (m) {
        action(m);
    }
}
exports.getMapAsync = getMapAsync;
//! Declares com.lightningkite.khrysalis.maps.bind>com.google.android.gms.maps.MapView
function xMapViewBind(this_, dependency, style) {
    const map = leaflet_1.map(this_);
    map.setView([0, 0], 1);
    configureMap(map, style);
    this_[mapSymbol] = map;
    const obs = new ResizeObserver(function callback() {
        console.log("Invalidating size...");
        map.invalidateSize();
        if (!document.contains(this_)) {
            obs.disconnect();
        }
    });
    obs.observe(this_);
}
exports.xMapViewBind = xMapViewBind;
//! Declares com.lightningkite.khrysalis.maps.bindView>com.google.android.gms.maps.MapView
function xMapViewBindView(this_, dependency, position, zoomLevel = 15, animate = true, style = null) {
    xMapViewBind(this_, dependency, style);
    const map = this_[mapSymbol];
    let first = true;
    let marker = null;
    DisposeCondition_actual_1.xDisposableUntil(ObservableProperty_ext_shared_1.xObservablePropertySubscribeBy(position, undefined, undefined, (g) => {
        if (g) {
            const p = [g.latitude, g.longitude];
            map.setView(p, zoomLevel, { animate: animate && !first });
            first = false;
            if (!marker) {
                marker = leaflet_1.marker(p);
                map.addLayer(marker);
            }
            else {
                marker.setLatLng(p);
            }
        }
        else {
            if (marker) {
                map.removeLayer(marker);
                marker = null;
            }
        }
    }), DisposeCondition_actual_1.xViewRemovedGet(this_));
}
exports.xMapViewBindView = xMapViewBindView;
//! Declares com.lightningkite.khrysalis.maps.bindSelect>com.google.android.gms.maps.MapView
function xMapViewBindSelect(this_, dependency, position, zoomLevel = 15, animate = true, style = null) {
    xMapViewBind(this_, dependency, style);
    const map = this_[mapSymbol];
    let first = true;
    let marker = null;
    DisposeCondition_actual_1.xDisposableUntil(ObservableProperty_ext_shared_1.xObservablePropertySubscribeBy(position, undefined, undefined, (g) => {
        const currentPos = marker === null || marker === void 0 ? void 0 : marker.getLatLng();
        if ((currentPos === null || currentPos === void 0 ? void 0 : currentPos.lng) == (g === null || g === void 0 ? void 0 : g.longitude) && (currentPos === null || currentPos === void 0 ? void 0 : currentPos.lng) == (g === null || g === void 0 ? void 0 : g.latitude))
            return;
        if (g) {
            const p = [g.latitude, g.longitude];
            map.setView(p, zoomLevel, { animate: animate && !first });
            first = false;
            if (!marker) {
                marker = leaflet_1.marker(p, { draggable: true });
                marker.on("drag", (e) => {
                    if (marker) {
                        const raw = marker.getLatLng();
                        position.value = new GeoCoordinate_shared_1.GeoCoordinate(raw.lat, raw.lng);
                    }
                });
                map.addLayer(marker);
            }
            else {
                marker.setLatLng(p);
            }
        }
        else {
            if (marker) {
                map.removeLayer(marker);
                marker = null;
            }
        }
    }), DisposeCondition_actual_1.xViewRemovedGet(this_));
    map.on('click', (e) => {
        if (!marker) {
            position.value = new GeoCoordinate_shared_1.GeoCoordinate(e.latlng.lat, e.latlng.lng);
        }
    });
}
exports.xMapViewBindSelect = xMapViewBindSelect;
//# sourceMappingURL=MapView.bind.actual.js.map