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

val GeoAddress.street: String
    get() {
        return (0..maxAddressLineIndex).joinToString(" ") { getAddressLine(it) }.substringBefore(",")
    }

val GeoAddress.oneLineShort: String
    get() {
        return buildString {
            append((0..maxAddressLineIndex).joinToString(" ") { getAddressLine(it) }.substringBefore(","))
            this@oneLineShort.locality?.let { it ->
                append(", ")
                append(it)
            }
            this@oneLineShort.adminArea?.let {
                append(", ")
                append(it)
            }
        }
    }

fun GeoAddress.oneLine(withCountry: Boolean = false, withZip: Boolean = false): String = buildString {
    for (line in 0..maxAddressLineIndex) {
        append(getAddressLine(line))
        append(' ')
    }
    this@oneLine.locality?.let {
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

