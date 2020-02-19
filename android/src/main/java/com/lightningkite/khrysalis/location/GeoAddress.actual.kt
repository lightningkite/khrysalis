package com.lightningkite.khrysalis.location

import android.location.Address
import com.lightningkite.khrysalis.location.GeoCoordinate
import java.util.*

typealias GeoAddress = Address

/* SHARED DECLARATIONS
val GeoAddress.locality: String
val GeoAddress.adminArea: String
val GeoAddress.countryName: String
 */

fun GeoAddress(): GeoAddress = GeoAddress(Locale.getDefault())

val GeoAddress.street: String?
    get() {
        return subThoroughfare?.let { x ->
            thoroughfare?.let { y ->
                "$x $y"
            }
        }
    }

val GeoAddress.oneLineShort: String
    get() = oneLine()

fun GeoAddress.oneLine(withCountry: Boolean = false, withZip: Boolean = false): String = buildString {
    street?.let {
        append(it)
    }
    this@oneLine.locality?.let {
        append(' ')
        append(it)
    }
    this@oneLine.adminArea?.let {
        append(", ")
        append(it)
    }
    if (withCountry) {
        this@oneLine.adminArea?.let {
            append(' ')
            append(it)
        }
    }
    if (withZip) {
        this@oneLine.postalCode?.let {
            append(' ')
            append(it)
        }
    }
}.trim()

val GeoAddress.coordinate: GeoCoordinate? get() = if (hasLatitude()) GeoCoordinate(latitude, longitude) else null

