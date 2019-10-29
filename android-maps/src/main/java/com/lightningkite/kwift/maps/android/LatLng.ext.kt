package com.lightningkite.kwift.maps.android

import com.google.android.gms.maps.model.LatLng
import com.lightningkite.kwift.location.shared.GeoCoordinate

fun GeoCoordinate.toMaps(): LatLng = LatLng(latitude, longitude)
fun LatLng.toKwift(): GeoCoordinate =
    GeoCoordinate(latitude, longitude)
