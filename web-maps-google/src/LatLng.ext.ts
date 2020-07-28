
import {GeoCoordinate} from "khrysalis/dist/location/GeoCoordinate.shared";
import {} from "googlemaps"

//! Declares com.google.android.gms.maps.model.LatLng
export type LatLng = google.maps.LatLng
export const LatLng = google.maps.LatLng

//! Declares com.lightningkite.khrysalis.maps.toMaps
export function comGoogleAndroidGmsMapsModelLatLngToKhrysalis(this_: LatLng): GeoCoordinate {
    return new GeoCoordinate(this_.lat(), this_.lng());
}
//! Declares com.lightningkite.khrysalis.maps.toKhrysalis
export function comLightningkiteKhrysalisLocationGeoCoordinateToMaps(this_: GeoCoordinate): LatLng {
    return new LatLng(this_.latitude, this_.longitude);
}
