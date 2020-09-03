
import {GeoCoordinate} from "khrysalis/dist/location/GeoCoordinate.shared";
import {LatLng} from "leaflet";

//! Declares com.lightningkite.khrysalis.maps.toMaps
export function xLatLngToMaps(this_: LatLng): GeoCoordinate {
    return new GeoCoordinate(this_.lat, this_.lng);
}
//! Declares com.lightningkite.khrysalis.maps.toKhrysalis
export function xGeoCoordinateToKhrysalis(this_: GeoCoordinate): LatLng {
    return new LatLng(this_.latitude, this_.longitude);
}
