package com.lightningkite.kwift.maps

import com.google.android.gms.maps.model.LatLng
import com.lightningkite.kwift.location.GeoCoordinate

fun GeoCoordinate.toMaps(): LatLng = LatLng(latitude, longitude)
fun LatLng.toKwift(): GeoCoordinate =
    GeoCoordinate(latitude, longitude)
