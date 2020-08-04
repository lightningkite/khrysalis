"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const DisposeCondition_actual_1 = require("khrysalis/dist/rx/DisposeCondition.actual");
const ObservableProperty_ext_shared_1 = require("khrysalis/dist/observables/ObservableProperty.ext.shared");
const LatLng_ext_1 = require("./LatLng.ext");
const mapSymbol = Symbol("mapSymbol");
//! Declares com.lightningkite.khrysalis.maps.bind>com.google.android.gms.maps.MapView
function comGoogleAndroidGmsMapsMapViewBind(this_, dependency, style) {
    const map = new google.maps.Map(this_, {
        center: { lat: 0, lng: 0 },
        zoom: 2,
        styles: [] //?
    });
    this_[mapSymbol] = map;
}
exports.comGoogleAndroidGmsMapsMapViewBind = comGoogleAndroidGmsMapsMapViewBind;
//! Declares com.lightningkite.khrysalis.maps.bindView>com.google.android.gms.maps.MapView
function comGoogleAndroidGmsMapsMapViewBindView(this_, dependency, position, zoomLevel = 15, animate = true, style = null) {
    comGoogleAndroidGmsMapsMapViewBind(this_, dependency, style);
    const map = this_[mapSymbol];
    let first = true;
    let marker = null;
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(position, undefined, undefined, (g) => {
        if (g) {
            const p = LatLng_ext_1.comLightningkiteKhrysalisLocationGeoCoordinateToMaps(g);
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
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
}
exports.comGoogleAndroidGmsMapsMapViewBindView = comGoogleAndroidGmsMapsMapViewBindView;
//! Declares com.lightningkite.khrysalis.maps.bindSelect>com.google.android.gms.maps.MapView
function comGoogleAndroidGmsMapsMapViewBindSelect(this_, dependency, position, zoomLevel = 15, animate = true, style = null) {
    comGoogleAndroidGmsMapsMapViewBind(this_, dependency, style);
    const map = this_[mapSymbol];
    let first = true;
    let marker = null;
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(position, undefined, undefined, (g) => {
        if (g) {
            const p = LatLng_ext_1.comLightningkiteKhrysalisLocationGeoCoordinateToMaps(g);
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
                        position.value = LatLng_ext_1.comGoogleAndroidGmsMapsModelLatLngToKhrysalis(pos);
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
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    map.addListener("click", (ev) => {
        position.value = LatLng_ext_1.comGoogleAndroidGmsMapsModelLatLngToKhrysalis(ev.latLng);
    });
}
exports.comGoogleAndroidGmsMapsMapViewBindSelect = comGoogleAndroidGmsMapsMapViewBindSelect;
//# sourceMappingURL=MapView.bind.actual.js.map