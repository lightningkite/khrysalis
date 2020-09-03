"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const DisposeCondition_actual_1 = require("khrysalis/dist/rx/DisposeCondition.actual");
const ObservableProperty_ext_shared_1 = require("khrysalis/dist/observables/ObservableProperty.ext.shared");
const LatLng_ext_1 = require("./LatLng.ext");
const mapSymbol = Symbol("mapSymbol");
//! Declares com.lightningkite.khrysalis.maps.bind>com.google.android.gms.maps.MapView
function xMapViewBind(this_, dependency, style) {
    const map = new google.maps.Map(this_, {
        center: { lat: 0, lng: 0 },
        zoom: 2,
        styles: [] //?
    });
    this_[mapSymbol] = map;
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
            const p = LatLng_ext_1.xGeoCoordinateToMaps(g);
            if (animate && !first) {
                map.panTo(p);
                map.setZoom(zoomLevel);
            }
            else {
                map.setCenter(p);
                map.setZoom(zoomLevel);
            }
            first = false;
            if (!marker) {
                marker = new google.maps.Marker({
                    position: p,
                    map: map
                });
            }
            else {
                marker.setPosition(p);
            }
        }
        else {
            if (marker) {
                marker.setMap(null);
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
        if (g) {
            const p = LatLng_ext_1.xGeoCoordinateToMaps(g);
            if (animate && !first) {
                map.panTo(p);
                map.setZoom(zoomLevel);
            }
            else {
                map.setCenter(p);
                map.setZoom(zoomLevel);
            }
            first = false;
            if (!marker) {
                marker = new google.maps.Marker({
                    position: p,
                    map: map,
                    draggable: true
                });
                marker.addListener("dragend", () => {
                    const pos = marker === null || marker === void 0 ? void 0 : marker.getPosition();
                    if (pos) {
                        position.value = LatLng_ext_1.xLatLngToKhrysalis(pos);
                    }
                });
            }
            else {
                marker.setPosition(p);
            }
        }
        else {
            if (marker) {
                marker.setMap(null);
                marker = null;
            }
        }
    }), DisposeCondition_actual_1.xViewRemovedGet(this_));
    map.addListener("click", (ev) => {
        position.value = LatLng_ext_1.xLatLngToKhrysalis(ev.latLng);
    });
}
exports.xMapViewBindSelect = xMapViewBindSelect;
//# sourceMappingURL=MapView.bind.actual.js.map