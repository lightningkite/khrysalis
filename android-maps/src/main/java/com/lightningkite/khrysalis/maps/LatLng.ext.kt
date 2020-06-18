package com.lightningkite.khrysalis.maps

import com.google.android.gms.maps.model.LatLng
import com.lightningkite.khrysalis.location.GeoCoordinate

fun GeoCoordinate.toMaps(): LatLng = LatLng(latitude, longitude)
fun LatLng.toKhrysalis(): GeoCoordinate =
    GeoCoordinate(latitude, longitude)
