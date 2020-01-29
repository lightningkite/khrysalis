package com.lightningkite.khrysalis.location

import android.location.Location
import com.lightningkite.khrysalis.location.GeoCoordinate

fun GeoCoordinate.distanceToMiles(other: GeoCoordinate): Double {
    val loc1 = Location("")
    loc1.latitude = this.latitude
    loc1.longitude = this.longitude
    val loc2 = Location("")
    loc2.latitude = other.latitude
    loc2.longitude = other.longitude

    //meters to miles
    return loc1.distanceTo(loc2) / 1609.34
}
