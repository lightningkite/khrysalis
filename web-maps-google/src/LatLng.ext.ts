
import {GeoCoordinate} from "khrysalis/dist/location/GeoCoordinate.shared";
import {} from "googlemaps"

//! Declares com.lightningkite.khrysalis.maps.toKhrysalis
export function xLatLngToKhrysalis(this_: google.maps.LatLng): GeoCoordinate {
    return new GeoCoordinate(this_.lat(), this_.lng());
}

//! Declares com.lightningkite.khrysalis.maps.toMaps
export function xGeoCoordinateToMaps(this_: GeoCoordinate): google.maps.LatLng {
    return new google.maps.LatLng(this_.latitude, this_.longitude);
}